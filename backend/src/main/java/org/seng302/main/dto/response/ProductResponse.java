package org.seng302.main.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.seng302.main.models.ProductImage;

import java.time.LocalDate;
import java.util.List;

/**
 * Product response DTO object
 */
@Getter
@Setter
@NoArgsConstructor
public class ProductResponse {

    private Long id;
    private Long businessId;
    private String name;
    private String description;
    private String manufacturer;
    private Double recommendedRetailPrice;
    private Long primaryImageId;
    private LocalDate created;
    private List<ProductImage> images;

}
