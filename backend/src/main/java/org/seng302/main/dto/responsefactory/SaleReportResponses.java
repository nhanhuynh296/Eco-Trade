package org.seng302.main.dto.responsefactory;

import org.seng302.main.dto.response.SaleReportResponse;
import org.seng302.main.models.Sale;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.time.temporal.TemporalAdjusters.*;
import static java.time.temporal.TemporalAdjusters.firstDayOfYear;

/**
 * Handles row creation, updates, and other functionality required to create the sales report.
 */
public class SaleReportResponses {

    /**
     * Returns the starting date of the most recent week, month, or year depending on the required granulation.
     *
     * @param initialDate the date for which the start date of the specific time period must be found
     * @param granulateBy the required granularity of the information
     * @return the starting date of the most recent week, month, or year
     */
    public static LocalDate getInitialDate(LocalDate initialDate, String granulateBy) {
        switch (granulateBy) {
            case "DAY":
                // The initial date of the day should be the end date
                break;
            case "WEEK":
                initialDate = initialDate.with(DayOfWeek.MONDAY);
                break;
            case "MONTH":
                initialDate = initialDate.with(firstDayOfMonth());
                break;
            default: // "YEAR"
                initialDate = initialDate.with(firstDayOfYear());
                break;
        }
        return initialDate;
    }

    /**
     * Returns the end date of the most recent week, month, or year depending on the required granulation.
     *
     * @param finalDate   the date for which the end date of the specific time period must be found
     * @param granulateBy the required granularity of the information
     * @return the end date of the most recent week, month, or year
     */
    public static LocalDate getFinalDate(LocalDate finalDate, String granulateBy) {
        switch (granulateBy) {
            case "DAY":
                // The end date of the day should be the initial date
                break;
            case "WEEK":
                finalDate = finalDate.with(DayOfWeek.MONDAY).plusDays(6);
                break;
            case "MONTH":
                finalDate = finalDate.with(lastDayOfMonth());
                break;
            default: // "YEAR"
                finalDate = finalDate.with(lastDayOfYear());
                break;
        }
        return finalDate;
    }

    /**
     * Updates the value of the hash map with the given sale's quantity and selling price.
     * Uses the sale's selling date as the key.
     *
     * @param sale          a sold listing which must have its values added to the saleReportRow
     * @param saleReportRow a specific row of the sales report that needs to be updated.
     * @return the updated saleReportRow
     */
    public static SaleReportResponse updateResponse(Sale sale, SaleReportResponse saleReportRow) {

        if (saleReportRow != null) {
            saleReportRow.setTotalSales(saleReportRow.getTotalSales() + sale.getQuantity());
            saleReportRow.setTotalRevenue(saleReportRow.getTotalRevenue() + sale.getSoldFor());
        }

        return saleReportRow;
    }

    /**
     * Returns a custom name for the period column of the sales report data.
     *
     * @param granulateBy required granularity of sales data
     * @param initialDate a date to be used for month or year
     * @param rowCounter  the counter for days and weeks
     * @return a custom name
     */
    private static String getPeriodString(String granulateBy, LocalDate initialDate, int rowCounter) {
        String period = granulateBy.substring(0, 1).toUpperCase() + granulateBy.substring(1).toLowerCase() + " " + rowCounter;
        switch (granulateBy) {
            case "DAY":
            case "WEEK":
                break;
            case "MONTH":
                period = initialDate.getMonth().toString();
                break;
            default: // "YEAR"
                period = Integer.toString(initialDate.getYear());
                break;
        }
        return period;
    }

    /**
     * Creates a blank sales report response to be added to the paginated sales report data.
     *
     * @param initialDate the date of the initial period (determined according to granulation)
     * @param granulateBy the required granularity of the information
     * @param rowCounter  the count of the sales report row
     * @return a sales report response dto
     */
    public static SaleReportResponse getResponse(LocalDate initialDate, String granulateBy, int rowCounter) {
        SaleReportResponse saleReportResponse = new SaleReportResponse();

        // Sets the string to be combined with a number
        String period = getPeriodString(granulateBy, initialDate, rowCounter);

        saleReportResponse.setPeriod(period);
        saleReportResponse.setInitialDate(initialDate);

        // Depending on the granulation a final date for this row is calculated
        LocalDate finalDate = getFinalDate(initialDate, granulateBy);
        saleReportResponse.setFinalDate(finalDate);

        saleReportResponse.setTotalSales(0);
        saleReportResponse.setTotalRevenue(0.0);

        return saleReportResponse;
    }

    /**
     * Checks if the current date falls under the custom start date which is dependent on granularity of data.
     * If so the returned date is the start date. Otherwise, the returned date is set to the initial date (e.g. the start of a week).
     * Used to calculate the hash key for the sales report information.
     *
     * @param granulateBy a string which determines granularity of information
     * @param currentDate the current date given by the for loop
     * @param startDate the start date that the user inputs (if it's valid)
     * @return An appropriate date
     */
    static LocalDate returnTrueInitialDate(String granulateBy, LocalDate currentDate, LocalDate startDate) {
        LocalDate granulatedDate;
        if (getInitialDate(startDate, granulateBy).toString().equals(getInitialDate(currentDate, granulateBy).toString())) {
            granulatedDate = startDate;
        } else {
            granulatedDate = getInitialDate(currentDate, granulateBy);
        }
        return granulatedDate;
    }

    /**
     * Uses a starting date and ending date to populate the hash map with all the required rows based on the
     * required granularity of the sales report.
     *
     * @param granulateBy the required granularity of the sales report
     * @param startDate   the starting date of the requested sold listings
     * @param endDate     the final date of the requested sold listings
     * @return an empty hash map with only the required keys
     */
    public static Map<LocalDate, SaleReportResponse> getEmptyResponses(String granulateBy, LocalDate startDate, LocalDate endDate) {
        LinkedHashMap<LocalDate, SaleReportResponse> map = new LinkedHashMap<>();
        LocalDate granulatedDate;
        int rowCounter = 0;

        LocalDate previousGranulatedDate = null;
        for (LocalDate currentDate = startDate; currentDate.isBefore(endDate.plusDays(1)); currentDate = currentDate.plusDays(1)) {
            // Uses the initial period of time required by granulation as the key (start of week, month, etc.)
            // If the start date and current date are the same then that must be the hash key e.g. if the start date is halfway through a year
            granulatedDate = returnTrueInitialDate(granulateBy, currentDate, startDate);

            if (previousGranulatedDate == null || !granulatedDate.toString().equals(previousGranulatedDate.toString())) {
                rowCounter++;
                map.put(granulatedDate, getResponse(granulatedDate, granulateBy, rowCounter));
            }

            previousGranulatedDate = granulatedDate;
        }

        return map;
    }

}
