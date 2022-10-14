package org.seng302.main.dto.responsefactory;

import org.seng302.main.dto.response.ProductResponse;
import org.seng302.main.models.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Product Response factory builder class
 */
public class ProductResponses {

    /**
     * Add explicit private constructor
     */
    private ProductResponses() {}

    /**
     * Creates a single response for product
     *
     * @param product instance of Product
     * @return product response
     */
    public static ProductResponse getResponse(Product product) {
        ProductResponse productResponse = new ProductResponse();

        productResponse.setId(product.getId());
        productResponse.setBusinessId(product.getBusinessId());
        productResponse.setName(product.getName());
        productResponse.setDescription(product.getDescription());
        productResponse.setManufacturer(product.getManufacturer());
        productResponse.setRecommendedRetailPrice(product.getRecommendedRetailPrice());
        productResponse.setPrimaryImageId(product.getPrimaryImageId());
        productResponse.setCreated(product.getCreated());
        productResponse.setImages(product.getImages());

        return productResponse;
    }

    /**
     * Creates a list of product responses
     *
     * @param products List of Product instances
     * @return List of product responses
     */
    public static List<ProductResponse> getAllResponses(List<Product> products) {
        List<ProductResponse> productResponses = new ArrayList<>();

        for (Product product: products) {
            productResponses.add(getResponse(product));
        }

        return productResponses;
    }

}
