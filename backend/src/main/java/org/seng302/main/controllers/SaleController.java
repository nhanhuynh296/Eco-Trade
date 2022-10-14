package org.seng302.main.controllers;

import lombok.extern.log4j.Log4j2;
import org.seng302.main.dto.response.*;
import org.seng302.main.helpers.ControllerHelper;
import org.seng302.main.models.User;
import org.seng302.main.repository.UserRepository;
import org.seng302.main.services.BusinessService;
import org.seng302.main.services.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

/**
 * Sale Controller
 *
 * Handles all the Sale responses for a business
 */
@Controller
@Log4j2
public class SaleController {

    @Autowired
    private BusinessService businessService;

    @Autowired
    private SaleService saleService;

    @Autowired
    private ControllerHelper controllerHelper;

    private final String nonExistantUserMessage = "There is no user for this session ID!";

    /**
     * Retrieves a page of SaleResponses from the database relating to the given businessId
     *
     * @param id of business whose sales to get
     * @param sessionId of user requesting sales history
     * @param pageNumber index of page to get
     * @param pageSize number of sales per page
     * @return 200 with page as JSON body upon success
     * 401 if session id is not provided
     * 400 if session id does not correspond to a user
     * 406 if business with id is not found
     * 403 if user is not an admin of business or a GAA
     */
    @GetMapping("/businesses/{id}/saleshistory")
    public ResponseEntity<PaginationInfo<SaleResponse>> getSalesFromBusiness(@PathVariable Long id,
                                                                    @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId,
                                                                    @RequestParam(name = "page", defaultValue = "1") Integer pageNumber,
                                                                    @RequestParam(name = "size", defaultValue = "20") Integer pageSize) {
        log.info("[GET /businesses/{id}/saleshistory] Trying to get the sale history of business id: " + id);

        User user = controllerHelper.authorizeUser(sessionId);

        BusinessResponse business = businessService.getBusiness(id);
        if (business == null) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Business does not exist");
        }

        if (!businessService.isAdminOfBusinessOrGAAFromBusinessId(user, id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User must be admin of business or GAA");
        }

        PaginationInfo<SaleResponse> saleHistory = saleService.getSaleHistoryFromBusinessId(id, pageNumber, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(saleHistory);
    }

    /**
     * Throws an error if the dates cannot be parsed with LocalDate.parse.
     *
     * @param startDateString string of expected format yyyy-mm-dd
     * @param endDateString string of expected format yyyy-mm-dd
     */
    public void tryParseDates(String startDateString, String endDateString) {
        try {
            LocalDate.parse(startDateString);
            LocalDate.parse(endDateString);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad dates have been given, the expected format is yyyy-mm-dd");
        }
    }

    /**
     * Gets one page for the sale report for a business. Checks that the user is a business admin
     *
     * @param id business id
     * @param granulateBy granularity string
     * @param pageNumber used to determine which results to return
     * @param pageSize the number of items to return per page
     * @param startDateString custom date input by the user for where to start the sales report
     * @param endDateString custom date input by the user for where to end the sales report
     * @param sessionId the session token for the user attempting to access the sales report
     * @return one page of sales report data
     */
    @GetMapping("/businesses/{id}/salesreport")
    public ResponseEntity<PaginationInfo<SaleReportResponse>> getSalesReport(@PathVariable Long id,
                                                                             @RequestParam(name = "granulateBy", defaultValue = "YEAR") String granulateBy,
                                                                             @RequestParam String startDateString,
                                                                             @RequestParam String endDateString,
                                                                             @RequestParam(name = "page", defaultValue = "0") int pageNumber,
                                                                             @RequestParam(name = "size", defaultValue = "10") int pageSize,
                                                                             @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String.format("[GET /businesses/{id}/salesreport] Trying to get sale report of business with ID: %d", id));

        tryParseDates(startDateString, endDateString);

        LocalDate startDate = LocalDate.parse(startDateString);
        LocalDate endDate = LocalDate.parse(endDateString);

        User currentUser = controllerHelper.authorizeUser(sessionId);
        saleService.checkSaleReportAuthAndDates(id, startDate, endDate, currentUser);

        PaginationInfo<SaleReportResponse> results = saleService.getSaleReportData(granulateBy, id, pageNumber, pageSize, startDate, endDate);

        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    /**
     * Gets all data of the sale report for a business.
     *
     * @param id business id
     * @param granulateBy granularity string
     * @param startDateString custom date input by the user for where to start the sales report
     * @param endDateString custom date input by the user for where to end the sales report
     * @param sessionId the session token for the user attempting to access the sales report
     * @return all data of the sale report for a business.
     */
    @GetMapping("/businesses/{id}/salesgraph")
    public ResponseEntity<SaleGraphResponse> getSalesReportGraph(@PathVariable Long id,
                                                                 @RequestParam String startDateString,
                                                                 @RequestParam String endDateString,
                                                                 @RequestParam(name = "granulateBy", defaultValue = "YEAR") String granulateBy,
                                                                 @CookieValue(value = "JSESSIONID", defaultValue = "None") String sessionId) {
        log.info(String.format("[GET /businesses/{id}/salesgraph] Gets sale graph data of business with ID: %d", id));

        tryParseDates(startDateString, endDateString);

        LocalDate startDate = LocalDate.parse(startDateString);
        LocalDate endDate = LocalDate.parse(endDateString);

        User currentUser = controllerHelper.authorizeUser(sessionId);
        saleService.checkSaleReportAuthAndDates(id, startDate, endDate, currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(saleService.getSaleGraphData(id, startDate, endDate, granulateBy));
    }

}
