package org.seng302.main.serviceTests;

import io.restassured.internal.util.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.models.*;
import org.seng302.main.repository.*;
import org.seng302.main.services.BusinessTypeService;
import org.seng302.main.services.ProductImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Tests for the ImageService class
 */
@SpringBootTest
class ProductImageServiceTests {
    @Autowired
    ProductImageRepository productImageRepository;

    @Autowired
    ProductImageService productImageService;

    @Autowired
    BusinessRepository businessRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    ListingRepository listingRepository;

    @Value("${wasteless.images.rootdir}")
    private String imageRootDir;

    @Value("${wasteless.images.productdir}")
    private String productImageDir;

    private Business business;
    private Product product;
    private InventoryItem inventoryItem;

    File goodFile = new File("./src/test/resources/media/test_image.jpg");
    File badFile = new File("./src/test/resources/media/not_an_image.txt");

    @BeforeEach
    public void init() {
        productImageRepository.deleteAll();
        productRepository.deleteAll();
        businessRepository.deleteAll();
        listingRepository.deleteAll();

        business = new Business(1L, "Test Business", "Some Desc",
                new Address("1", "2", "3", "4", "5", "6"),
                BusinessTypeService.CHARITABLE.toString(), LocalDate.now());
        business = businessRepository.save(business);

        product = new Product(business.getId(), business, "AAA", "BBB", "CCC", 50d, LocalDate.now());
        product = productRepository.save(product);

        inventoryItem = new InventoryItem(product, 69, 10.0, 100.0, LocalDate.now(), null, null,
                LocalDate.now().plusDays(20));
        inventoryItem = inventoryRepository.save(inventoryItem);
    }

    /**
     * Test we can upload an image to a product
     */
    @Test
    void testSaveImage() throws IOException {
        long businessId = business.getId();
        long productId = product.getId();

        long imageId = productImageService.saveProductImage(businessId, productId,
                new MockMultipartFile("file", goodFile.getName(), "image/png",
                        IOUtils.toByteArray(new FileInputStream(goodFile)))
        );

        Assertions.assertTrue(imageId > 0);
        Assertions.assertTrue(
                new File(
                        String.format("./%s/%s/%d_%d_%d_test_image.jpg",
                                imageRootDir, productImageDir, imageId, business.getId(), product.getId()))
                        .exists()
        );
    }

    /**
     * Test we can upload an image to a product and it saves a thumbnail
     */
    @Test
    void testSaveThumbnail() throws IOException {
        long businessId = business.getId();
        long productId = product.getId();

        long imageId = productImageService.saveProductImage(businessId, productId,
                new MockMultipartFile("file", goodFile.getName(), "image/png",
                        IOUtils.toByteArray(new FileInputStream(goodFile)))
        );

        Assertions.assertTrue(imageId > 0);
        Assertions.assertTrue(
                new File(
                        String.format("./%s/%s/%d_%d_%d_thumbnail_test_image.jpg",
                                imageRootDir, productImageDir, imageId, business.getId(), product.getId()))
                        .exists()
        );
    }

    /**
     * Test invalid business id when updating primary image id
     */
    @Test
    void testInvalidBusinessIdOnValidatingPrimaryImage() {
        long res = productImageService.validatePrimaryProductImageRequest(null, -1, product.getId(), 1);

        Assertions.assertEquals(1, res);
    }

    /**
     * Test invalid business when adding images
     */
    @Test
    void testInvalidBusinessValidateProduct() {
        long res = productImageService.validateProductImageRequest(null, -1, product.getId(), null);

        Assertions.assertEquals(1, res);
    }

    /**
     * Test invalid product when adding images
     */
    @Test
    void testInvalidProductValidateProduct() {
        long res = productImageService.validateProductImageRequest(null, business.getId(), -1, null);

        Assertions.assertEquals(1, res);
    }

    /**
     * Test invalid filename when adding images
     */
    @Test
    void testInvalidFileValidateProduct() throws IOException {
        MockMultipartFile mockFile = new MockMultipartFile("file", badFile.getName(), "text/txt",
                IOUtils.toByteArray(new FileInputStream(badFile))
        );

        long res = productImageService.validateProductImageRequest(null, business.getId(), product.getId(), mockFile);

        Assertions.assertEquals(2, res);
    }

    /**
     * Test delete image from image repository and disk
     */
    @Test
    void testDeleteImage() throws IOException {
        long businessId = business.getId();
        long productId = product.getId();

        Long imageId = productImageService.saveProductImage(businessId, product.getId(),
                new MockMultipartFile("file", goodFile.getName(), "image/png",
                        IOUtils.toByteArray(new FileInputStream(goodFile)))
        );

        Assertions.assertTrue(
                new File(
                        String.format("./%s/%s/%d_%d_%d_test_image.jpg",
                                imageRootDir, productImageDir, imageId, business.getId(), product.getId()))
                        .exists()
        );

        Assertions.assertNotNull(productImageRepository.findImageByImageId(imageId));
        productImageService.deleteProductImage(productId, imageId);

        Assertions.assertFalse(
                new File(
                        String.format("./%s/%s/%d_%d_%d_test_image.jpg",
                                imageRootDir, productImageDir, imageId, business.getId(), product.getId()))
                        .exists()
        );

        Assertions.assertNull(productImageRepository.findImageByImageId(imageId));
    }

    /**
     * Test delete image thumbnail from repository and disk
     */
    @Test
    void testDeleteImageThumbnail() throws IOException {
        long businessId = business.getId();
        long productId = product.getId();

        long imageId = productImageService.saveProductImage(businessId, productId,
                new MockMultipartFile("file", goodFile.getName(), "image/png",
                        IOUtils.toByteArray(new FileInputStream(goodFile)))
        );

        Assertions.assertTrue(
                new File(
                        String.format("./%s/%s/%d_%d_%d_thumbnail_test_image.jpg",
                                imageRootDir, productImageDir, imageId, business.getId(), product.getId()))
                        .exists()
        );

        Assertions.assertNotNull(productImageRepository.findImageByImageId(imageId));
        productImageService.deleteProductImage(productId, imageId);

        Assertions.assertFalse(
                new File(
                        String.format("./%s/%s/%d_%d_%d_thumbnail_test_image.jpg",
                                imageRootDir, productImageDir, imageId, business.getId(), product.getId()))
                        .exists()
        );

        Assertions.assertNull(productImageRepository.findImageByImageId(imageId));
    }


    /**
     * Checks that if the product for an image exists as a listing then the
     * checkImageIsInListing function confirms there is at least 1
     * (meaning it returns 0).
     */
    @Test
    void testExistingImageListingReturnsZero() throws IOException {
        long businessId = business.getId();
        long productId = product.getId();

        // Creates a listing for the inventory for the product that is receiving an image
        Listing listing = new Listing(inventoryItem, 2, 4.0, "Beans",
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(5));
        listingRepository.save(listing);

        // Product receives an image
        long imageId = productImageService.saveProductImage(businessId, productId,
                new MockMultipartFile("file", goodFile.getName(), "image/png",
                        IOUtils.toByteArray(new FileInputStream(goodFile)))
        );

        Assertions.assertEquals(0, productImageService.checkImageIsInListing(imageId));
    }

    /**
     * Checks that for a non-existent image checkImageIsInListing returns 1.
     */
    @Test
    void testNonExistingImageReturnsOne() throws IOException {
        long businessId = business.getId();
        long productId = product.getId();

        long imageId = productImageService.saveProductImage(businessId, productId,
                new MockMultipartFile("file", goodFile.getName(), "image/png",
                        IOUtils.toByteArray(new FileInputStream(goodFile)))
        );

        Assertions.assertEquals(1, productImageService.checkImageIsInListing(imageId + 1));
    }

    /**
     * Checks that if the product for an image does not exist as a listing
     * then the checkImageIsInListing function confirms there is no listing
     * (meaning it returns 2).
     */
    @Test
    void testNonListingImageReturnsTwo() throws IOException {
        long businessId = business.getId();
        long productId = product.getId();

        long imageId = productImageService.saveProductImage(businessId, productId,
                new MockMultipartFile("file", goodFile.getName(), "image/png",
                        IOUtils.toByteArray(new FileInputStream(goodFile)))
        );

        Assertions.assertEquals(2, productImageService.checkImageIsInListing(imageId));
    }
}
