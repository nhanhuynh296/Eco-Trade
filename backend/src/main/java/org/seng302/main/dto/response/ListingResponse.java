package org.seng302.main.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * Listing Data Transfer Object
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ListingResponse {

    private Long id;
    private InventoryItemResponse inventoryItem;
    private Integer quantity;
    private Double price;
    private String moreInfo;
    private LocalDate created;
    private LocalDate closes;
    private BusinessResponse business;
    private List<Long> likes;
    private long totalLikes;
}
