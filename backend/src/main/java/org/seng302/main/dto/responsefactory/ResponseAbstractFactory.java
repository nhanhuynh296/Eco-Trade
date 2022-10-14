package org.seng302.main.dto.responsefactory;

import org.seng302.main.dto.response.*;
import org.seng302.main.models.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Response Factory interface
 *
 * Creates all responses and singular responses
 */
public interface ResponseAbstractFactory {

    /**
     * Creates a user response entity
     *
     * @param user instance of User
     * @param getFullAddress boolean for setting full address
     * @param getBusinesses boolean for setting business
     * @return user response entity
     */
    UserResponse getUserResponse(User user, boolean getFullAddress, boolean getBusinesses);

    /**
     * Creates a list of user responses
     *
     * @param users List of User instances
     * @param getFullAddress boolean for setting full address
     * @param getBusinesses boolean for setting business
     * @return List of user responses
     */
    List<UserResponse> getUserResponses(List<User> users, boolean getFullAddress, boolean getBusinesses);

    /**
     * Creates a business response entity
     *
     * @param business instance of Business
     * @return business response entity
     */
    BusinessResponse getBusinessResponse(Business business);

    /**
     * Creates a list of business responses
     *
     * @param businesses List of Business instances
     * @return List of business responses
     */
    List<BusinessResponse> getBusinessResponses(List<Business> businesses);

    /**
     * Creates a product response entity
     *
     * @param product instance of Product
     * @return product response entity
     */
    ProductResponse getProductResponse(Product product);

    /**
     * Creates a list of product responses
     *
     * @param products List of Product instances
     * @return List of product responses
     */
    List<ProductResponse> getProductResponses(List<Product> products);

    /**
     * Creates a inventory item response entity
     *
     * @param inventoryItem instance of InventoryItem
     * @return inventory item response entity
     */
    InventoryItemResponse getInventoryItemResponse(InventoryItem inventoryItem);

    /**
     * Creates a list of inventory item responses
     *
     * @param inventoryItems List of InventoryItem instances
     * @return List of inventory item responses
     */
    List<InventoryItemResponse> getInventoryItemResponses(List<InventoryItem> inventoryItems);

    /**
     * Creates a listing response entity
     *
     * @param listing instance of Listing
     * @param getBusiness boolean for setting business
     * @return listing response entity
     */
    ListingResponse getListingResponse(Listing listing, boolean getBusiness);

    /**
     * Creates a list of listing responses
     *
     * @param listings List of Listing instances
     * @param getBusiness boolean for setting business
     * @return List of listing responses
     */
    List<ListingResponse> getListingResponses(List<Listing> listings, boolean getBusiness);

    /**
     * Creates a sale response entity
     *
     * @param sale instance of Sale
     * @return sale response entity
     */
    SaleResponse getSaleResponse(Sale sale);

    /**
     * Creates a list of sale responses
     *
     * @param sales List of Sale instances
     * @return List of sale responses
     */
    List<SaleResponse> getSaleResponses(List<Sale> sales);

    /**
     * Creates a card response entity
     *
     * @param card instance of Card
     * @return card response entity
     */
    CardResponse getCardResponse(Card card);

    /**
     * Creates a list of card responses
     *
     * @param cards List of Card instances
     * @return List of card responses
     */
    List<CardResponse> getCardResponses(List<Card> cards);

    /**
     * Creates a keyword response entity
     *
     * @param keyword instance of Keyword
     * @return keyword response entity
     */
    KeywordResponse getKeywordResponse(Keyword keyword);

    /**
     * Creates a list of keyword responses
     *
     * @param keywords List of Keyword instances
     * @return List of keyword responses
     */
    List<KeywordResponse> getKeywordResponses(List<Keyword> keywords);

    /**
     * Creates a notification response entity
     *
     * @param notification instance of Notification
     * @return notification response entity
     */
    NotificationResponse getNotificationResponse(Notification notification);

    /**
     * Creates a list of notification responses
     *
     * @param notifications List of Notification instances
     * @return List of notification responses
     */
    List<NotificationResponse> getNotificationResponses(List<Notification> notifications);

    /**
     * Retrieves the initial date for sale report row
     *
     * @param initialDate LocalDate initial date
     * @param granulateBy String type of granularity
     * @return LocalDate initial date
     */
    LocalDate getInitialDateForSaleReport(LocalDate initialDate, String granulateBy);

    /**
     * Creates a mapping of sales for sale report
     *
     * @param granulateBy String type of granularity
     * @param startDate LocalDate start date
     * @param endDate the end date input by the user
     * @return Mapping of date range and the sale report response
     */
    Map<LocalDate, SaleReportResponse> getSaleReportResponses(String granulateBy, LocalDate startDate, LocalDate endDate);

    /**
     * Updates the sale report response row
     *
     * @param sale instance of Sale
     * @param saleReportRow instance of SaleReportResponse
     * @return updated sale report response
     */
    SaleReportResponse updateSaleReportResponse(Sale sale, SaleReportResponse saleReportRow);

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
    LocalDate returnTrueInitialDateForSaleReport(String granulateBy, LocalDate currentDate, LocalDate startDate);

    /**
     * Gets the sale graph response
     *
     * @param saleReports list of sale reports
     * @return SaleGraphResponse containing all information required for a graph representation
     */
    SaleGraphResponse getSaleGraphResponse(List<SaleReportResponse> saleReports);

}
