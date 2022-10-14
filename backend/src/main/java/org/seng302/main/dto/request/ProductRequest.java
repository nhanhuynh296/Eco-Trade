package org.seng302.main.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.seng302.main.models.Product;

@Getter
@Setter
@NoArgsConstructor
public class ProductRequest {
    private String name;
    private String description;
    private String manufacturer;
    private Double recommendedRetailPrice;

    public Product getProductObject() {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setManufacturer(manufacturer);
        product.setRecommendedRetailPrice(recommendedRetailPrice);
        return product;
    }
}
