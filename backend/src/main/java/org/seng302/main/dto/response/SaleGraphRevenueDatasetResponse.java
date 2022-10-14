package org.seng302.main.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Sale Graph Revenue Dataset response DTO object
 */
@Getter
@Setter
@NoArgsConstructor
public class SaleGraphRevenueDatasetResponse {

    private String label;
    private List<String> backgroundColor;
    private List<Double> data;

}
