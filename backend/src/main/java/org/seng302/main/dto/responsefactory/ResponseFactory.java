package org.seng302.main.dto.responsefactory;

import org.seng302.main.dto.response.*;
import org.seng302.main.models.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Response Factory class
 *
 * Creates all responses and singular responses
 */
public class ResponseFactory implements ResponseAbstractFactory {

    @Override
    public UserResponse getUserResponse(User user, boolean getFullAddress, boolean getBusinesses) {
        return UserResponses.getResponse(user, getFullAddress, getBusinesses);
    }

    @Override
    public List<UserResponse> getUserResponses(List<User> users, boolean getFullAddress, boolean getBusinesses) {
        return UserResponses.getAllResponses(users, getFullAddress, getBusinesses);
    }

    @Override
    public BusinessResponse getBusinessResponse(Business business) {
        return BusinessResponses.getFullResponse(business, true);
    }

    @Override
    public List<BusinessResponse> getBusinessResponses(List<Business> businesses) {
        return BusinessResponses.getAllResponses(businesses, true, false);
    }

    @Override
    public ProductResponse getProductResponse(Product product) {
        return ProductResponses.getResponse(product);
    }

    @Override
    public List<ProductResponse> getProductResponses(List<Product> products) {
        return ProductResponses.getAllResponses(products);
    }

    @Override
    public InventoryItemResponse getInventoryItemResponse(InventoryItem inventoryItem) {
        return InventoryItemResponses.getResponse(inventoryItem);
    }

    @Override
    public List<InventoryItemResponse> getInventoryItemResponses(List<InventoryItem> inventoryItems) {
        return InventoryItemResponses.getAllResponses(inventoryItems);
    }

    @Override
    public ListingResponse getListingResponse(Listing listing, boolean getBusiness) {
        return ListingResponses.getResponse(listing, getBusiness);
    }

    @Override
    public List<ListingResponse> getListingResponses(List<Listing> listings, boolean getBusiness) {
        return ListingResponses.getAllResponses(listings, getBusiness);
    }

    @Override
    public SaleResponse getSaleResponse(Sale sale) {
        return SaleResponses.getResponse(sale);
    }

    @Override
    public List<SaleResponse> getSaleResponses(List<Sale> sales) {
        return SaleResponses.getAllResponses(sales);
    }

    @Override
    public CardResponse getCardResponse(Card card) {
        return CardResponses.getResponse(card);
    }

    @Override
    public List<CardResponse> getCardResponses(List<Card> cards) {
        return CardResponses.getAllResponses(cards);
    }

    @Override
    public KeywordResponse getKeywordResponse(Keyword keyword) {
        return KeywordResponses.getResponse(keyword);
    }

    @Override
    public List<KeywordResponse> getKeywordResponses(List<Keyword> keywords) {
        return KeywordResponses.getAllResponses(keywords);
    }

    @Override
    public NotificationResponse getNotificationResponse(Notification notification) {
        return NotificationResponses.getResponse(notification);
    }

    @Override
    public List<NotificationResponse> getNotificationResponses(List<Notification> notifications) {
        return NotificationResponses.getAllResponses(notifications);
    }

    @Override
    public LocalDate getInitialDateForSaleReport(LocalDate initialDate, String granulateBy) {
        return SaleReportResponses.getInitialDate(initialDate, granulateBy);
    }

    @Override
    public Map<LocalDate, SaleReportResponse> getSaleReportResponses(String granulateBy, LocalDate startDate, LocalDate endDate) {
        return SaleReportResponses.getEmptyResponses(granulateBy, startDate, endDate);
    }

    @Override
    public SaleReportResponse updateSaleReportResponse(Sale sale, SaleReportResponse saleReportRow) {
        return SaleReportResponses.updateResponse(sale, saleReportRow);
    }

    @Override
    public LocalDate returnTrueInitialDateForSaleReport(String granulateBy, LocalDate currentDate, LocalDate startDate) {
        return SaleReportResponses.returnTrueInitialDate(granulateBy, currentDate, startDate);
    }

    @Override
    public SaleGraphResponse getSaleGraphResponse(List<SaleReportResponse> saleReports) {
        return SaleGraphResponses.getGraphResponse(saleReports);
    }

}
