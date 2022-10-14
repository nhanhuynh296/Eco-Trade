package org.seng302.main.services;

import lombok.extern.log4j.Log4j2;
import org.seng302.main.dto.request.ProductRequest;
import org.seng302.main.dto.response.PaginationInfo;
import org.seng302.main.dto.response.ProductResponse;
import org.seng302.main.dto.responsefactory.ResponseFactory;
import org.seng302.main.models.Business;
import org.seng302.main.models.Product;
import org.seng302.main.models.User;
import org.seng302.main.repository.BusinessRepository;
import org.seng302.main.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

/**
 * Product Service that communicates with Product Repo
 */
@Service
@Log4j2
public class ProductService {

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageService productImageService;

    private ResponseFactory responseFactory = new ResponseFactory();

    /**
     * Creates a new product for a business
     *
     * @param user instance of the current user
     * @param businessId the id of the business
     * @param productRequest information provided (dto)
     * @return int -1 if the business does not exist
     * int -2 if the current user is not a primary admin of the business and is not a DGAA
     * int -3 if the product provided contains incorrect information
     * int 0 if successful
     */
    public Product createProduct(User user, Long businessId, ProductRequest productRequest) {
        Product product = productRequest.getProductObject();
        Business business = businessRepository.findBusinessById(businessId);

        if (business == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Business doesn't exist!");
        }
        if (!businessService.isAdminOfBusinessOrGAA(user, business)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only a administrator of the business or a DGAA can add a product to its inventory");
        }

        String errorMessage = isValidProduct(product);

        if (!errorMessage.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }

        product.setBusinessId(businessId);
        product.setBusiness(business);
        product.setCreated(LocalDate.now());

        return productRepository.save(product);
    }

    /**
     * Retrieves all the products that belong to the provided business
     *
     * @param businessId id of the business
     * @return JSONArray that only contains "invalid user or business" if provided invalid user or business
     * JSONArray that only contains "not a primary admin" if the current user is not a primary admin
     * JSONArray that contains all the products that belong to the business if successful
     */
    public PaginationInfo<ProductResponse> getAllProducts(long businessId, int pageNumber, int pageSize, String sortBy) {
        Pageable pageRequest;

        Sort sortingAttributes = getSortType(sortBy);
        if (sortingAttributes != null) {
            pageRequest = PageRequest.of(pageNumber - 1, pageSize, sortingAttributes);
        } else {
            pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        }

        Page<Product> page =  productRepository.findProductsByBusinessId(businessId, pageRequest);
        List<Product> products = page.getContent();
        int totalPages = page.getTotalPages();
        long totalElements = page.getTotalElements();

        return new PaginationInfo<>(responseFactory.getProductResponses(products), totalPages, totalElements);
    }

    /**
     * Updates given product
     *
     * @param user the currently logged in user
     * @param businessId id of the business to which the product belongs
     * @param productId the id of the product being modified
     * @param productRequest the new product dto being that will replace the original
     * @return int -1 if the business does not exist
     *         int -2 if the current user is not a primary admin of the business and is not a DGAA
     *         int -3 if the product provided contains incorrect information
     *         int 0 if successful
     */
    public void modifyProduct(User user, Long businessId, Long productId, ProductRequest productRequest) {
        Product product = productRequest.getProductObject();
        Business business = businessRepository.findBusinessById(businessId);

        if (business == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Business doesn't exist!");
        }
        if (!businessService.isAdminOfBusinessOrGAA(user, business)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only a administrator of the business or a DGAA can add a product to its inventory");
        }
        String errorMessage = isValidProduct(product);
        if (!errorMessage.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }

        product.setCreated(LocalDate.now());
        product.setBusinessId(businessId);
        product.setBusiness(business);

        productRepository.updateProductInBusiness(businessId, productId, product.getDescription(), product.getName(), product.getManufacturer(), product.getRecommendedRetailPrice());
    }

    /**
     * Sets new primary image id for given product
     *
     * @param productId the id of the product being modified
     * @param imageId the id of the new primary image
     */
    public void setProductPrimaryImage(Long productId, Long imageId) {
        productRepository.updatePrimaryProductImage(imageId, productId);
    }

    /**
     * Get a product by product ID
     *
     * @param productId ID of the product
     * @return Product or null
     */
    public Product getProductById(long productId) {
        return productRepository.findProductById(productId);
    }

    /**
     * Error checking to ensure product to be added is valid
     *
     * @param newProduct Product model
     * @return An error message if there is a error,
     * otherwise returns the empty string
     */
    public String isValidProduct(Product newProduct) {
        int maxNameLength = 70;
        int maxDescriptionLength = 250;
        String message = "";

        if (newProduct.getName() == null || newProduct.getName().strip().length() == 0) {
            message += "Product name cannot be empty.\n";
        } else if (newProduct.getName().length() > maxNameLength) {
            message += "Product name length exceeds character limit.\n";
        }

        if (newProduct.getRecommendedRetailPrice() < 0) {
            message += "Product price must be positive.\n";
        }

        if (newProduct.getDescription() != null && newProduct.getDescription().length() > maxDescriptionLength) {
            message += "Product description exceeds character limit.\n";
        }

        return message.trim();
    }

    /**
     * Get sort options for searching for inventory items
     *
     * @param sortBy Sort by string
     * @return Sort object to sort users by specified attribute
     */
    private Sort getSortType(String sortBy)
    {
        Sort sort;
        switch (sortBy) {
            case "ID_ASC":
                sort = Sort.by(Sort.Order.asc("id"));
                break;
            case "ID_DESC":
                sort = Sort.by(Sort.Order.desc("id"));
                break;
            case "NAME_ASC":
                sort = Sort.by(Sort.Order.asc("name"), Sort.Order.desc("id"));
                break;
            case "NAME_DESC":
                sort = Sort.by(Sort.Order.desc("name"), Sort.Order.desc("id"));
                break;
            case "RRP_ASC":
                sort = Sort.by(Sort.Order.asc("recommendedRetailPrice"), Sort.Order.desc("id"));
                break;
            case "RRP_DESC":
                sort = Sort.by(Sort.Order.desc("recommendedRetailPrice"), Sort.Order.desc("id"));
                break;
            case "CREATED_ASC":
                sort = Sort.by(Sort.Order.asc("created"), Sort.Order.desc("id"));
                break;
            case "CREATED_DESC":
                sort = Sort.by(Sort.Order.desc("created"), Sort.Order.desc("id"));
                break;
            case "MANUFACTURER_ASC":
                sort = Sort.by(Sort.Order.asc("manufacturer"), Sort.Order.desc("id"));
                break;
            case "MANUFACTURER_DESC":
                sort = Sort.by(Sort.Order.desc("manufacturer"), Sort.Order.desc("id"));
                break;
            case "DESCRIPTION_ASC":
                sort = Sort.by(Sort.Order.asc("description"), Sort.Order.desc("id"));
                break;
            case "DESCRIPTION_DESC":
                sort = Sort.by(Sort.Order.desc("description"), Sort.Order.desc("id"));
                break;
            default:
                sort = null;
        }
        return sort;
    }

}
