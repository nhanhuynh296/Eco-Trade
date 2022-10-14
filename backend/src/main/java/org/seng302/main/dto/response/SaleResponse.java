package org.seng302.main.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

/**
 * Sale response DTO object
 */
@ToString
@Getter
@Setter
@NoArgsConstructor
public class SaleResponse {

    private Long id;
    private Long businessId;
    private ProductResponse product;
    private LocalDate saleDate;
    private LocalDate listingDate;
    private Integer quantity;
    private Long numLikes;
    private Double soldFor;
    private BusinessResponse business;

}
