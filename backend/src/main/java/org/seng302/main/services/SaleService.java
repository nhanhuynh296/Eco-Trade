package org.seng302.main.services;

import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import org.seng302.main.dto.response.PaginationInfo;
import org.seng302.main.dto.response.SaleGraphResponse;
import org.seng302.main.dto.response.SaleReportResponse;
import org.seng302.main.dto.response.SaleResponse;
import org.seng302.main.dto.responsefactory.ResponseFactory;
import org.seng302.main.models.Listing;
import org.seng302.main.models.Sale;
import org.seng302.main.models.User;
import org.seng302.main.repository.ListingRepository;
import org.seng302.main.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Sales History Service that communicates with Sale Repo to get granulated sales report data and sales history information.
 */
@Service
@Log4j2
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private ListingRepository listingRepository;

    private ResponseFactory responseFactory = new ResponseFactory();

    /**
     * Retrieves a listing entity given its id, then creates and returns a sale entity
     *
     * @param listingId for which to create a sale
     * @return a Sale entity corresponding to the listing
     */
    @Transactional
    public Sale createSaleFromListing(Long listingId) {
        Listing listing = listingRepository.getListingById(listingId);
        Sale sale = new Sale();

        sale.setBusinessId(listing.getInventoryItem().getProduct().getBusinessId());
        sale.setProduct(listing.getInventoryItem().getProduct());
        sale.setSaleDate(LocalDate.now());
        sale.setListingDate(listing.getCreated());
        sale.setQuantity(listing.getQuantity());
        sale.setNumLikes(listing.getLikedUsers().size());
        sale.setSoldFor(listing.getPrice());

        return sale;
    }

    /**
     * Loops through the sales list to combine the information as required by granularity.
     * Uses an initial date determined by granularity for the key of the hash map for each row of data.
     *
     * @param salesList list of sold items
     * @param granulateBy the required granularity of the sales report
     * @param startDate the starting date of the requested sold listings
     * @param endDate the final date of the requested sold listings
     * @return a granulated sales report as an unsorted hash map, no pagination
     */
    public Map<LocalDate, SaleReportResponse> granulateSalesHistory(List<Sale> salesList,
                                                                    String granulateBy,
                                                                    LocalDate startDate,
                                                                    LocalDate endDate) {
        Map<LocalDate, SaleReportResponse> map = new LinkedHashMap<>();

        // Checks that the start and end dates aren't overlapping/conflicting
        if (startDate.isEqual(endDate) || startDate.isBefore(endDate)) {
            map = responseFactory.getSaleReportResponses(granulateBy, startDate, endDate);

            for (Sale sale : salesList) {
                // Gets the correct date to file this sale's information under (used as the hash map key)
                LocalDate initialDate = responseFactory.returnTrueInitialDateForSaleReport(granulateBy, sale.getSaleDate(), startDate);

                sale.setSaleDate(initialDate);
                // Updates the correct sale report row (will not be null) because of getSaleReportResponses
                SaleReportResponse updatedSaleReportRow = responseFactory.updateSaleReportResponse(sale, map.get(sale.getSaleDate()));
                map.put(sale.getSaleDate(), updatedSaleReportRow);
            }
        }

        return map;
    }

    /**
     * Turns a hash map into an ordered list and paginates it to the required results. Also, calculates the
     * total pages and elements.
     *
     * @param map an unordered, unpaginated hash map that contains sale report rows.
     * @param endDate the true end date
     */
    public List<SaleReportResponse> returnOrderedList(Map<LocalDate, SaleReportResponse> map, LocalDate endDate) {
        List<SaleReportResponse> orderedSalesReportList = new ArrayList<>();

        map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> orderedSalesReportList.add(entry.getValue()));

        if (map.size() > 0) {
            SaleReportResponse lastRow = orderedSalesReportList.get(orderedSalesReportList.size() - 1);
            orderedSalesReportList.remove(lastRow);
            lastRow.setFinalDate(endDate);
            orderedSalesReportList.add(lastRow);
        }

        return orderedSalesReportList;
    }

    /**
     * Turns a hash map in an ordered list and paginates it to the required results. Also, calculates the
     * total pages and elements.
     *
     * @param orderedSalesReportList Ordered list that contains sale report rows
     * @param pageNumber the requested page (starting from 0)
     * @param pageSize the number of rows per page
     *
     */
    public List<SaleReportResponse> returnPaginatedList(List<SaleReportResponse> orderedSalesReportList, int pageNumber, int pageSize) {
        List<SaleReportResponse> paginatedSalesReportList = new ArrayList<>();

        int totalElements = orderedSalesReportList.size();
        int elementsCount = totalElements - (pageNumber * pageSize) - 1;
        if (elementsCount > pageSize) {
            elementsCount = pageSize - 1;
        }

        for (int i = 0; i <= elementsCount; i++) {
            paginatedSalesReportList.add(orderedSalesReportList.get(pageNumber * pageSize + i));
        }

        return paginatedSalesReportList;
    }

    /**
     * Turns a hash map into an ordered list and paginates it to the required results. Also, calculates the
     * total pages and elements.
     *
     * @param map An unordered, un-paginated hash map that contains sale report rows.
     * @param pageNumber the requested page (starting from 0)
     * @param pageSize the number of rows per page
     * @param endDate the true end date
     */
    public PaginationInfo<SaleReportResponse> returnPaginatedOrderedList(Map<LocalDate, SaleReportResponse> map,
                                                                         int pageNumber,
                                                                         int pageSize,
                                                                         LocalDate endDate) {

        List<SaleReportResponse> orderedSalesReportList = returnOrderedList(map, endDate);

        int totalElements = orderedSalesReportList.size();
        orderedSalesReportList = returnPaginatedList(orderedSalesReportList, pageNumber, pageSize);
        int totalPages = ((totalElements - 1) / pageSize) + 1;

        return new PaginationInfo<>(orderedSalesReportList, totalPages, totalElements);
    }

    /**
     * Checks if the difference between the given dates is greater than 100 and if so returns a maximum of 100 years
     * from the start date as the end date. Returns the original end date otherwise.
     *
     * @param startDate custom date input by the user for where to start the sales report
     * @param endDate custom date input by the user for where to end the sales report
     * @return a valid date that won't cause an infinite loop
     */
    public LocalDate returnDateRestriction(LocalDate startDate,
                                       LocalDate endDate) {
        // reformat the end date to today as the sale can not be in the future, automatically reduce to LocalDate.now()
        if (startDate.until(endDate, ChronoUnit.YEARS) > 100) {
            endDate = startDate.plusYears(100);
        }

        return endDate;
    }

    /**
     * Takes a search specification made up of a start date, end date, and business ID. Then uses this to retrieve
     * the relevant business information which is then granulated depending on the given granulation code.
     *
     * @param granulateBy a string constant that is used to determine which granulation calculation to apply
     * @param businessID id of the business who's sales history has been requested to granulate
     * @param pageNumber used to determine which results to return
     * @param pageSize the number of items to return per page
     * @param startDate custom date input by the user for where to start the sales report
     * @param endDate custom date input by the user for where to end the sales report
     * @return a paginated and granulated sales report
     */
    public PaginationInfo<SaleReportResponse> getSaleReportData(String granulateBy,
                                                                long businessID,
                                                                int pageNumber,
                                                                int pageSize,
                                                                LocalDate startDate,
                                                                LocalDate endDate) {
        endDate = returnDateRestriction(startDate, endDate);

        List<Sale> saleList = saleRepository.findAllByBusinessIdAndSaleDateBetween(businessID, startDate, endDate);

        Map<LocalDate, SaleReportResponse> map = granulateSalesHistory(saleList, granulateBy, startDate, endDate);

        return returnPaginatedOrderedList(map, pageNumber, pageSize, endDate);
    }

    /**
     * Checks the user is an admin for the business or higher level admin. Also, checks
     * that the end date and start date are not contradictory.
     *
     * @param businessID id of the business who's sales history has been requested to granulate
     * @param startDate custom date input by the user for where to start the sales report
     * @param endDate custom date input by the user for where to end the sales report
     * @throws ResponseStatusException throws a relevant error
     */
    @Transactional
    public void checkSaleReportAuthAndDates(long businessID,
                                            LocalDate startDate,
                                            LocalDate endDate,
                                            User user) {
        if (!businessService.isAdminOfBusinessOrGAAFromBusinessId(user, businessID)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorised to access this business's sales report!\n");
        }

        if (startDate.isAfter(endDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The start date cannot be after the end date!\n");
        }
    }

    /**
     * Retrieves a page of SaleResponses from the database relating to the given businessId
     *
     * @param businessId of business whose sales to get
     * @param pageNumber index of page to get
     * @param pageSize number of sales per page
     * @return Page of SaleResponses (PaginationInfo<SaleResponse>)
     */
    public PaginationInfo<SaleResponse> getSaleHistoryFromBusinessId(Long businessId, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Sale> page = saleRepository.getAllByBusinessIdOrderBySaleDateDesc(businessId, pageable);
        List<Sale> sales = page.getContent();
        int totalPages = page.getTotalPages();
        long totalElements = page.getTotalElements();
        return new PaginationInfo<>(responseFactory.getSaleResponses(sales), totalPages, totalElements);
    }

    /**
     * Retrieves the sales graph data from the sales report.
     *
     * @param id id of business
     * @param startDate start date of all sale listing
     * @param endDate cut off date of all sale listing
     * @return SaleGraphResponse containing all the data for the sales graph
     */
    public SaleGraphResponse getSaleGraphData(Long id, LocalDate startDate, LocalDate endDate, String granulateBy) {
        endDate = returnDateRestriction(startDate, endDate);
        List<Sale> saleList = saleRepository.findAllByBusinessIdAndSaleDateBetween(id, startDate, endDate);
        Map<LocalDate, SaleReportResponse> map = granulateSalesHistory(saleList, granulateBy, startDate, endDate);
        List<SaleReportResponse> saleReportResponses = returnOrderedList(map, endDate);

        return responseFactory.getSaleGraphResponse(saleReportResponses);
    }

}
