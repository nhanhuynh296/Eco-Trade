package org.seng302.main.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Sale Graph response DTO object
 */
@Getter
@Setter
@NoArgsConstructor
public class SaleGraphResponse {

    private List<String> labels;
    private SaleGraphSaleDatasetResponse datasetSale;
    private SaleGraphRevenueDatasetResponse datasetRevenue;
    private List<DateRangeResponse> dates;

}
