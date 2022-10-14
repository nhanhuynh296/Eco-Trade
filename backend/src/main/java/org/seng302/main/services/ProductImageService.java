package org.seng302.main.services;

import lombok.extern.log4j.Log4j2;
import org.seng302.main.helpers.ImageHelper;
import org.seng302.main.models.Product;
import org.seng302.main.models.ProductImage;
import org.seng302.main.models.User;
import org.seng302.main.repository.ProductImageRepository;
import org.seng302.main.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

/**
 * Service for dealing with images
 */
@Service
@Log4j2
public class ProductImageService {

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ImageService imageService;

    @Value("${wasteless.images.rootdir}")
    private String rootDir;

    @Value("${wasteless.images.productdir}")
    private String productImagesDir;

    @Value("${wasteless.images.thumbnail.scale}")
    private Integer thumbnailScale;

    /**
     * Checks that the image that the user is requesting exists and that the
     * user is a business admin for the image (or higher level admin).
     *
     * @param user instance of User
     * @param businessId id of the business
     * @param imageId ID of the image
     * @return 0 on success
     * 1 if image does not exist
     * 2 if the business does not exist or the image does not belong to the business that's currently being accessed
     * 3 if the use is not one of the business admins or higher level admin
     */
    public int checkUserCanAccessImage(User user, long businessId, long imageId) {
        ProductImage image = getImageById(imageId);

        if (image == null) {
            return 1;
        } else if (businessService.getBusinessById(businessId) == null) {
            return 2;
        } else if (!businessService.isAdminOfBusinessOrGAAFromBusinessId(user, businessId)) {
            return 3;
        } else {
            return 0;
        }
    }

    /**
     * Checks that the image that the user is requesting exists and that the
     * image is used in a listing. If there is no listing then the user is
     * not allowed to see it.
     *
     * @param imageId ID of the image
     * @return 0 if successful
     * 1 if the image does not exist
     * 2 if that image does not appear in any listings the user cannot view it
     * (for security reasons)
     */
    public int checkImageIsInListing(long imageId) {
        ProductImage image = getImageById(imageId);

        if (image == null) {
            return 1;
        }

        int count = productImageRepository.getNumberOfListings(productService.getProductById(image.getProduct().getId()).getId());
        if (count == 0) {
            return 2;
        }

        return 0;
    }

    /**
     * Validate a new product image request, returns a status code.
     * 0 = success
     *
     * @param user Current user making the request
     * @param businessId BusinessID of the business
     * @param productId ID of the product to add an image to
     * @param file File to upload
     * @return 0 on success
     * 1 on business/product doesn't exist
     * 2 on invalid filetype
     * 3 on non business admin / global admin
     * 4 on image limit met for this product
     */
    public int validateProductImageRequest(User user, long businessId, long productId, MultipartFile file) {
        if (businessService.getBusinessById(businessId) == null || productService.getProductById(productId) == null) {
            return 1;
        }

        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (!ImageHelper.validFiletypes.contains(extension)) {
            return 2;
        }

        if (!businessService.isAdminOfBusinessOrGAAFromBusinessId(user, businessId)) {
            return 3;
        }

        if (productImageRepository.findImagesByProductId(productId).size() >= 10) {
            return 4;
        }

        return 0;
    }

    /**
     * Validate a primary image request, returns a status code.
     *
     * @param user Current user making the request
     * @param businessId ID of the business
     * @param productId ID of the product
     * @param imageId ID of the image that is being updated to primary image
     * @return 0 on success
     * 1 on business/product doesn't exist
     * 2 on non business admin / global admin
     * 3 on image that doesn't exist
     */
    public int validatePrimaryProductImageRequest(User user, long businessId, long productId, long imageId) {

        if (businessService.getBusinessById(businessId) == null || productService.getProductById(productId) == null) {
            return 1;
        }

        if (!businessService.isAdminOfBusinessOrGAAFromBusinessId(user, businessId)) {
            return 2;
        }

        if (getImageById(imageId) == null) {
            return 3;
        }

        return 0;
    }

    /**
     * Save a product image.
     * Follows pattern of 'id_businessId_productId_filename' to allow for uniqueness.
     *
     * @param businessId ID of the business
     * @param productId ID of the product
     * @param file File image to save
     * @return ID of the saved image
     */
    public long saveProductImage(long businessId, long productId, MultipartFile file) throws IOException {
        ProductImage image = new ProductImage();
        Product product = productService.getProductById(productId);
        image.setProduct(product);
        image = productImageRepository.save(image);

        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());

        String filename = String.format("%d_%d_%s_%s", image.getId(), businessId,
                productId, StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));

        String thumbnailFilename = String.format("%d_%d_%s_thumbnail_%s", image.getId(), businessId,
                productId, StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));

        Path folderPath = Paths.get(rootDir);
        folderPath = folderPath.resolve(productImagesDir);

        imageService.saveImage(folderPath, filename, file);
        imageService.saveImageThumbnail(folderPath.resolve(filename), folderPath.resolve(thumbnailFilename), extension, thumbnailScale, thumbnailScale);

        //Set image filename
        image.setImageFilename(filename);
        //Set image thumbnail filename
        image.setImageThumbnail(thumbnailFilename);

        image = productImageRepository.save(image);

        // If the product has no current primary image, also set primary image id of the product to image id
        if (productService.getProductById(productId).getPrimaryImageId() == null) {
            productService.setProductPrimaryImage(productId, image.getId());
        }

        return image.getId();
    }

    /**
     * Try and delete an image from the specified path within the images rootdir,
     * if image already doesn't with filename, do nothing.
     *
     * @param path Image path within root directory
     * @param filename Name of the file to be deleted
     * @throws IOException IOException if cannot be deleted
     */
    public void deleteImage(Path path, String filename) throws IOException {
        Path filePath = path.resolve(filename);
        Files.delete(filePath);
    }

    /**
     * Deletes an image from product
     *
     * @param productId the id of the product for image deletion
     * @param imageId the id of the image being deleted
     */
    @Transactional
    public void deleteProductImage(Long productId, Long imageId) throws IOException {
        // The image to be deleted
        ProductImage image = productImageRepository.findImageByImageId(imageId);

        // Product with image in case primary image
        Product product = productService.getProductById(productId);
        List<ProductImage> images = product.getImages();
        images.remove(image);
        product.setImages(images);
        product.setPrimaryImageId(null);
        productRepository.save(product);

        Path folderPath = Paths.get(rootDir);
        folderPath = folderPath.resolve(productImagesDir);

        // Get the filename and thumbnail path to delete image from disk
        String filename = image.getFilename();
        String thumbnail = image.getThumbnailFilename();

        //Delete the file name from the root directory
        deleteImage(folderPath, filename);
        deleteImage(folderPath, thumbnail);

        productImageRepository.delete(image);
    }

    /**
     * Find image in the database by image ID
     *
     * @param imageId ID of the image
     * @return Image object or null
     */
    public ProductImage getImageById(long imageId) {
        return productImageRepository.findImageByImageId(imageId);
    }

    public List<ProductImage> getImagesByProductId(long productId) {
        return productImageRepository.findImagesByProductId(productId);
    }

}
