package org.seng302.main.dto.responsefactory;

import org.seng302.main.dto.response.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Sale Graph Response factory builder class
 */
public class SaleGraphResponses {

    private static List<String> saleColours = List.of("#004b94", "#0055a7", "#005fbb", "#0069cf", "#0073e2",
            "#007df6", "#0a86ff", "#1e90ff", "#329aff", "#45a3ff", "#59adff", "#6cb7ff");
    private static List<String> revenueColours = List.of("#890000", "#9d0000", "#b10000", "#c40000", "#d80000",
            "#eb0000", "#ff0000", "#ff1414", "#ff2727", "#ff3b3b", "#ff4e4e", "#ff6262");

    /**
     * Explicit private constructor
     */
    private SaleGraphResponses() {}

    /**
     * Builds a SaleGraph Response with provided sale report data
     * Creates dataset for sales data
     * Creates dataset for revenue data
     *
     * @param saleReports list of SaleReportResponse
     * @return SaleGraphResponse
     */
    public static SaleGraphResponse getGraphResponse(List<SaleReportResponse> saleReports) {
        SaleGraphResponse response = new SaleGraphResponse();
        SaleGraphSaleDatasetResponse datasetSale = new SaleGraphSaleDatasetResponse();
        SaleGraphRevenueDatasetResponse datasetRevenue = new SaleGraphRevenueDatasetResponse();
        List<DateRangeResponse> dateRangeResponsesList = new ArrayList<>();

        response.setLabels(new ArrayList<>());
        datasetSale.setLabel("Sale Dataset");
        datasetRevenue.setLabel("Revenue Dataset");
        datasetSale.setBackgroundColor(new ArrayList<>());
        datasetRevenue.setBackgroundColor(new ArrayList<>());
        datasetSale.setData(new ArrayList<>());
        datasetRevenue.setData(new ArrayList<>());
        int index = 0;

        for (SaleReportResponse saleReport: saleReports) {
            response.getLabels().add(saleReport.getPeriod());

            datasetSale.getData().add(saleReport.getTotalSales());
            datasetRevenue.getData().add(saleReport.getTotalRevenue());

            datasetSale.getBackgroundColor().add(saleColours.get(index));
            datasetRevenue.getBackgroundColor().add(revenueColours.get(index));

            DateRangeResponse rangeResponse = new DateRangeResponse();
            rangeResponse.setStartDate(saleReport.getInitialDate());
            rangeResponse.setEndDate(saleReport.getFinalDate());
            dateRangeResponsesList.add(rangeResponse);

            index++;

            if (index == 12) {
                index = 0;
            }
        }

        response.setDatasetSale(datasetSale);
        response.setDatasetRevenue(datasetRevenue);
        response.setDates(dateRangeResponsesList);

        return response;
    }

}
