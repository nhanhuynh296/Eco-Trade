package org.seng302.main.controllers;

import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import org.seng302.main.dto.request.ProductRequest;
import org.seng302.main.dto.response.PaginationInfo;
import org.seng302.main.dto.response.ProductResponse;
import org.seng302.main.models.Business;
import org.seng302.main.models.Product;
import org.seng302.main.models.ProductImage;
import org.seng302.main.models.User;
import org.seng302.main.repository.UserRepository;
import org.seng302.main.services.BusinessService;
import org.seng302.main.services.ProductImageService;
import org.seng302.main.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Product Controller
 *
 * Handles all the responses
 */
@Controller
@Log4j2
public class ProductController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private BusinessService businessService;

    @Value("${wasteless.images.rootdir}")
    private String rootDir;

    @Value("${wasteless.images.productdir}")
    private String productImagesDir;

    /**
     * Adds a new item to a businesses catalogue
     *
     * @param id id of business
     * @param sessionId the session id of the logged in user
     */
    @PostMapping("businesses/{id}/products")
    public ResponseEntity<JSONObject> addProductToBusiness(@PathVariable Long id, @RequestBody ProductRequest newProduct,
                                                           @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String.format("[POST /businesses/{id}/products] Trying to add product to business with ID: %d", id));

        // If no session id is provided, then the current user is not logged in
        if (sessionId.equals("None")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user has to be logged in to make this request");
        }

        // Get the current user using the session ticket
        User currentUser = userRepository.findUserBySessionTicket(sessionId);
        Product product = productService.createProduct(currentUser, id, newProduct);


        JSONObject idObject = new JSONObject(Map.of("productId", product.getId()));

        return new ResponseEntity<>(idObject, HttpStatus.CREATED);
    }

    /**
     * Retrieves a Product by using the business ID
     *
     * @param id of the business
     * @param sessionId the session id of the logged in user
     * @return 200 ok if the user was added successfully as an administrator of the business
     * 401 partially handled by Spring security
     * 403 forbidden user
     * 406 not acceptable if the user or business do not exist
     */
    @GetMapping("businesses/{id}/products")
    public ResponseEntity<PaginationInfo<ProductResponse>> getAllProducts(@PathVariable long id, @RequestParam(name = "page", defaultValue = "1") Integer pageNumber,
                                                        @RequestParam(name = "size", defaultValue = "8") Integer pageSize,
                                                        @RequestParam(name = "sortBy", defaultValue = "NAME_DESC") String sortBy,
                                                        @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String.format("[GET /businesses/{id}/products] Retrieve all products for business with ID: %d", id));

        // If no session id is provided, then the current user is not logged in
        if (sessionId.equals("None")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user has to be logged in to make this request");
        }

        // Get the current user using the session ticket
        User currentUser = userRepository.findUserBySessionTicket(sessionId);

        Business business = businessService.getBusinessById(id);
        if (business == null || currentUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Invalid user or business.");
        }
        if (!businessService.isAdminOfBusinessOrGAA(currentUser, business)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only a Primary Administrator can retrieve products from a business.");
        }

        PaginationInfo<ProductResponse> result = productService.getAllProducts(id, pageNumber, pageSize, sortBy);

        log.info(String.format("Retrieved all from products from a business with ID %d", id));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Updates an existing product
     *
     * @param businessId of the business
     * @param sessionId the session id of the logged in user
     * @return 201 when the product was modified successfully
     * 401 partially handled by Spring security
     * 403 forbidden user
     * 406 not acceptable if the user or business do not exist
     */
    @Transactional
    @PutMapping("/businesses/{businessId}/products/{productId}")
    public ResponseEntity<HttpStatus> modifyProductInCatalogue(@PathVariable Long businessId,
                                                               @PathVariable Long productId,
                                                               @RequestBody ProductRequest newProduct,
                                                               @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String.format("[PUT /businesses/{businessId}/products/{productId}] Trying to modify product %d in business %d", productId, businessId));
        // if no session id is provided, then the current user is not logged in
        if (sessionId.equals("None")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user has to be logged in to make this request");
        }

        // Get the current user using the session ticket,  and the business using businessId
        User currentUser = userRepository.findUserBySessionTicket(sessionId);

        productService.modifyProduct(currentUser, businessId, productId, newProduct);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Adds an image to an existing product
     *
     * @param businessId Business ID
     * @param productId Product ID
     * @param multipartImage Image of product
     * @param sessionId Cookie session value
     * @return HTTP Response Code indicating success with image ID as body (if applicable)
     */
    @PostMapping("/businesses/{businessId}/products/{productId}/images")
    public ResponseEntity<JSONObject> addImageToProduct(@PathVariable Long businessId,
                                                        @PathVariable Long productId,
                                                        @RequestParam("file") MultipartFile multipartImage,
                                                        @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String.format("[POST /businesses/{businessId}/products/{productId}/images] Trying to set product %d's image of business %d", productId, businessId));

        if (multipartImage == null || multipartImage.getOriginalFilename() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Supplied image must not be null");
        }

        // Get the current user using the session ticket
        User currentUser = userRepository.findUserBySessionTicket(sessionId);
        int result = productImageService.validateProductImageRequest(currentUser, businessId, productId, multipartImage);

        switch (result) {
            case (1): //Business / product doesn't exist
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Business or product does not exist");
            case (2): //Invalid filetype
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Supplied image is invalid");
            case (3): //Not a business admin or GAA
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not administer this business");
            case (4): // Max number of images per product reached
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The maximum number of images has been reached");
            default:
                break;
        }

        long id;

        try {
            id = productImageService.saveProductImage(businessId, productId, multipartImage);
        } catch (Exception e) {
            log.warn("Failed to save image");
            log.warn(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to save image, unknown error");
        }

        JSONObject json = new JSONObject();
        json.put("imageId", id);

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    /**
     * Adds an image to an existing product.
     *
     * @param businessId Business ID
     * @param productId Product ID
     * @param imageId ID of the image that is being updated to primary image
     * @param sessionId Cookie session value
     * @return HTTP Response Code indicating success/failure
     */
    @PutMapping("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary")
    public ResponseEntity<HttpStatus> updateProductPrimaryImage(@PathVariable Long businessId,
                                                                @PathVariable Long productId,
                                                                @PathVariable Long imageId,
                                                                @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String.format("[PUT /businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary]" +
                " Trying to set product %d's primary image of business %d to image with id %d", productId, businessId, imageId));

        // Check that user is valid
        // Check that the business is valid, it has a valid product, and that product has an id with the given id

        // Get the current user using the session ticket
        User currentUser = userRepository.findUserBySessionTicket(sessionId);
        int result = productImageService.validatePrimaryProductImageRequest(currentUser, businessId, productId, imageId);

        switch (result) {
            case (1): // Business/product does not exist
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Business or product does not exist");
            case (2): // Not a business admin or GAA
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not administer this business");
            case (3): // Image with given id does not exist
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Image doesn't exist with that id");
            default:
                break;
        }

        try {
            productService.setProductPrimaryImage(productId, imageId);
        } catch (Exception e) {
            log.warn("Failed to set image as primary");
            log.warn(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to set image as primary, unknown server error");
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Deletes an image of a product. If the last image is deleted, a standard default image is displayed.
     *
     * @param businessId Business ID
     * @param productId Product ID
     * @param imageId ID of the image that is being updated to primary image
     * @param sessionId the session id of the logged in user
     * @return 200 The image is deleted
     * 401 If the session token is not correct
     * 403 If the user is not an administrator for the business or GAA
     * 406 Not Acceptable if the business, product, or image do not exist
     */
    @DeleteMapping("/businesses/{businessId}/products/{productId}/images/{imageId}")
    public ResponseEntity<Object> deleteProductImage(@PathVariable Long businessId,
                                                     @PathVariable Long productId,
                                                     @PathVariable Long imageId,
                                                     @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String.format("[DELETE /businesses/{businessId}/products/{productId}/images/{imageId}]" +
                " Trying to delete product %d's image of business %d to image with id %d", productId, businessId, imageId));

        // Get the current user using the session ticket
        User currentUser = userRepository.findUserBySessionTicket(sessionId);

        // Get the request image to delete
        int result = productImageService.validatePrimaryProductImageRequest(currentUser, businessId, productId, imageId);

        switch (result) {
            case (1): // Business/product does not exist
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Business or product does not exist");
            case (2): // Not a business admin or GAA
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not administer this business");
            case (3): // Image with given id does not exist
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Image does not exist with that id");
            default:
                break;
        }

        try {
            productImageService.deleteProductImage(productId, imageId);
        } catch (Exception e) {
            log.warn("Failed to delete image from product");
            log.warn(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Failed to delete image from product, unknown server error");
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Tries to send an image in the directory to the client.
     *
     * @param imageId id of the image
     * @return 200 OK status and the requested image
     * 404 Not Found status if it doesn't exist
     */
    private ResponseEntity<byte[]> sendImageFromDirectory(long imageId, boolean isThumbnail) {
        try {
            ProductImage image = productImageService.getImageById(imageId);
            HttpHeaders headers = new HttpHeaders();

            String filePath;
            Path folderPath = Paths.get(rootDir);
            folderPath = folderPath.resolve(productImagesDir);

            if (isThumbnail) {
                filePath = folderPath.resolve(image.getThumbnailFilename()).toString();
            } else {
                filePath = folderPath.resolve(image.getFilename()).toString();
            }

            File img = new File(filePath);
            byte[] media = Files.readAllBytes(img.toPath());
            headers.setCacheControl(CacheControl.noCache().getHeaderValue());
            return new ResponseEntity<>(media, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("Failed to get requested image");
            log.warn(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Takes the result of a service function and return the correct response code.
     *
     * @param imageId id of the requested image
     * @param result A integer from 0 to 3
     * @return result == 0 then a function is called to send the image to the client
     * result == 1 returns a 406 Not Acceptable status code indicating it does not exist
     * result >= 2 returns a 403 Forbidden status code if they are not the required
     * admin/admin type or any other problem.
     */
    private ResponseEntity<byte[]> handleResultCode(int result, long imageId, boolean isThumbnail) {
        if (result == 0) {
            return sendImageFromDirectory(imageId, isThumbnail);
        } else if (result == 1) {
            log.warn(String.format("Requested image with id %d does not exist", imageId));
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        } else {
            log.warn(String.format("Unable to access image id %s", imageId));
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Takes the id of a specific image and business and tries to return the corresponding image.
     * The user must be an admin of the business or higher level admin to access these.
     *
     * @param businessId ID of the business access the image
     * @param imageId ID of the image
     * @return A status code and an image if successful
     */
    @GetMapping(value = "/business/{businessId}/products/images/{imageId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable Long businessId,
                                           @PathVariable Long imageId,
                                           @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String.format("[GET /businesses/{businessId}/products/images/{imageId}] Trying to get image with image id %s", imageId));
        int result;
        try {
            User currentUser = userRepository.findUserBySessionTicket(sessionId);
            result = productImageService.checkUserCanAccessImage(currentUser, businessId, imageId);
        } catch (Exception e) {
            log.warn(String.format("User with session token %s does not have a unique token", sessionId));
            result = 3;
        }
        return handleResultCode(result, imageId, false);
    }

    /**
     * Takes an image id and tries to return the corresponding image.
     * The image must be for a listed product otherwise
     * the user cannot access it for security purposes.
     *
     * @param imageId ID of the image
     * @return A status code and an image if successful
     */
    @GetMapping(value = "/images/{imageId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getListingImage(@PathVariable Long imageId) {
        log.info(String.format("[GET /images/{imageId}] Trying to get image with image id %s", imageId));
        int result = productImageService.checkImageIsInListing(imageId);
        return handleResultCode(result, imageId, false);
    }

    /**
     * Takes the id of a specific image and business and tries to return the corresponding thumbnail for that product.
     * The user must be an admin of the business or higher level admin to access these.
     *
     * @param businessId ID of the business accessing the image
     * @param imageId ID of the image
     * @return A status code and an image if successful
     */
    @GetMapping(value = "/business/{businessId}/products/thumbnail/{imageId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getThumbnail(@PathVariable Long businessId,
                                               @PathVariable Long imageId,
                                               @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String.format("[GET /businesses/{businessId}/products/thumbnail/{imageId}] Trying to get image with image id %s", imageId));

        // If no session id is provided, then the current user is not logged in. (Important for controller tests)
        if (sessionId.equals("None")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user has to be logged in to make this request.");
        }

        int result;
        try {
            User currentUser = userRepository.findUserBySessionTicket(sessionId);
            result = productImageService.checkUserCanAccessImage(currentUser, businessId, imageId);
        } catch (Exception e) {
            log.warn(String.format("User with session token %s does not have a unique token", sessionId));
            result = 3;
        }
        return handleResultCode(result, imageId, true);
    }

    /**
     * Takes the id of a specific image and tries to return the corresponding thumbnail for that product.
     * The image must be for a product that is also an existing listing otherwise
     * the user cannot access it for security purposes.
     *
     * @param imageId ID of the image
     * @return A status code and an image if successful
     */
    @GetMapping(value = "/thumbnail/{imageId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getListingThumbnail(@PathVariable Long imageId,
                                                      @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String.format("[GET /thumbnail/{imageId}] Trying to get image with image id %s", imageId));

        // If no session id is provided, then the current user is not logged in. (Important for controller tests)
        if (sessionId.equals("None")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A user has to be logged in to make this request.");
        }

        int result = productImageService.checkImageIsInListing(imageId);
        return handleResultCode(result, imageId, true);
    }

}
