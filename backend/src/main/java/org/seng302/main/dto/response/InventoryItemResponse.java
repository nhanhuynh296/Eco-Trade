package org.seng302.main.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * InventoryItem response DTO object
 */
@Getter
@Setter
@NoArgsConstructor
public class InventoryItemResponse {

    private Long id;
    private ProductResponse product;
    private Integer quantity;
    private Double pricePerItem;
    private Double totalPrice;
    private LocalDate manufactured;
    private LocalDate sellBy;
    private LocalDate bestBefore;
    private LocalDate expires;

}
