package org.seng302.main.services;

import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import org.seng302.main.dto.response.ListingResponse;
import org.seng302.main.dto.response.PaginationInfo;
import org.seng302.main.dto.responsefactory.ResponseFactory;
import org.seng302.main.models.*;
import org.seng302.main.repository.BusinessRepository;
import org.seng302.main.repository.InventoryRepository;
import org.seng302.main.repository.ListingRepository;
import org.seng302.main.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

/**
 * Product Service that communicates with Product Repo
 */
@Service
@Log4j2
public class ListingService {

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SaleService saleService;

    private ResponseFactory responseFactory = new ResponseFactory();

    /**
     * Gets whether a new listing for a business is valid
     *
     * @param user instance of the current user
     * @param listingJson information provided
     * @param businessId business to add listing to
     * @return int -1 if the id of the inventory in the listing does not exist int -2 if the current
     * user is not a primary admin of the business and is not a DGAA int -3 if the provided inventory
     * item contains incorrect information int 0 if successful
     */
    public int isValidListing(User user, JSONObject listingJson, Long businessId) {
        long inventoryItemId;

        try {
            inventoryItemId = listingJson.getAsNumber("inventoryItemId").longValue();
        } catch (NullPointerException exception) {
            return -1;
        }

        InventoryItem inventoryItem = inventoryRepository.findInventoryItemById(inventoryItemId);

        if (inventoryItem == null) {
            return -1;
        }

        Listing newListing = getListingFromJSON(listingJson);
        Business business = businessRepository.findBusinessById(businessId);

        if (!businessService.isAdminOfBusinessOrGAA(user, business)) {
            return -2;
        } else if (!getListingErrorCode(newListing).isEmpty()) {
            return -3;
        }

        return 0;

    }

    /**
     * Persist listing into the repository and subtract quantity from inventory
     *
     * @param newListing listing to save
     * @return the id of the newly created listing
     */
    @Transactional
    public Long saveItemInRepository(Listing newListing) {
        InventoryItem inventoryItem = newListing.getInventoryItem();
        inventoryItem.setQuantity(inventoryItem.getQuantity() - newListing.getQuantity());
        inventoryRepository.save(inventoryItem);
        Listing savedListing = listingRepository.save(newListing);
        return savedListing.getId();
    }

    /**
     * Get listing from JSON object
     *
     * @param listingJson JSON representation of Listing
     * @return Listing object
     */
    public Listing getListingFromJSON(JSONObject listingJson) {

        Long inventoryItemId = listingJson.getAsNumber("inventoryItemId").longValue();
        InventoryItem inventoryItem = inventoryRepository.findInventoryItemById(inventoryItemId);

        Listing newListing = new Listing();
        newListing.setInventoryItem(inventoryItem);

        if (listingJson.getAsNumber("quantity") != null) {
            newListing.setQuantity(listingJson.getAsNumber("quantity").intValue());
        }

        if (listingJson.getAsNumber("price") != null) {
            newListing.setPrice(listingJson.getAsNumber("price").doubleValue());
        }

        newListing.setMoreInfo(listingJson.getAsString("moreInfo"));

        if (listingJson.getAsString("closes") != null) {
            newListing.setCloses(DateUtil.getInstance().dateFromString(listingJson.getAsString("closes")));
        } else {
            newListing.setCloses(null);
        }

        newListing.setCreated(LocalDate.now());
        return newListing;
    }

    /**
     * Get all listings from business
     *
     * @param businessId Businesses id
     * @return List of Listings with pagination info
     */
    public PaginationInfo<ListingResponse> getAllListingFromBusinessId(Long businessId, int pageNumber, int pageSize, String sortBy) {
        Pageable pageRequest;
        Sort sortingAttributes = getSortType(sortBy);
        if (sortingAttributes != null) {
            pageRequest = PageRequest.of(pageNumber - 1, pageSize, sortingAttributes);
        } else {
            pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        }
        Page<Listing> page =  listingRepository.findByInventoryItem_Product_BusinessId(businessId, pageRequest);
        List<Listing> listings = page.getContent();
        int totalPages = page.getTotalPages();
        long totalElements = page.getTotalElements();

        return new PaginationInfo<>(responseFactory.getListingResponses(listings, false), totalPages, totalElements);
    }


    /**
     * Validates that a given InventoryItem has correct values
     *
     * @param listing InventoryItem to be added to the database
     * @return A string representing the error message, the message if empty if there are no errors
     * found.
     */
    public String getListingErrorCode(Listing listing) {
        String message = "";

        InventoryItem inventoryItem = listing.getInventoryItem();
        LocalDate created = listing.getCreated();
        LocalDate closes = listing.getCloses();
        LocalDate expiry = inventoryItem.getExpires();

        // Quantity and price are mandatory fields
        if (listing.getQuantity() == null) {
            message += "Listing must have a specified quantity.\n";
        } else if (listing.getQuantity() > inventoryItem.getQuantity()) {
            message += "Listing cannot have quantity larger than the corresponding inventory.\n";
        } else if (listing.getPrice() == null) {
            message += "Listing must have a specified price.\n";
        }
        // Quantity, price cannot be negative
        else if (listing.getQuantity() < 0) {
            message += "Listing quantity must be a positive, whole number.\n";
        } else if (listing.getPrice() < 0) {
            message += "Listing price must be positive.\n";
        }
        // Created day cannot be after closes date
        // Closes date cannot be after expiry date and cannot be in the past
        else if (created.isAfter(closes)) {
            message += "Closing date cannot be before the created date.\n";
            // Removes the time added to the closing date and increases the expiry date by one day used for checking the closing date is not afterwards
        } else if (closes.isAfter(expiry)) {
            message += "Closing date cannot be after the expiry date.\n";
        } else if (closes.isBefore(LocalDate.now())) {
            message += "Closing date must be in the future.\n";
        }

        return message.trim();
    }

    /**
     * Retrieves the search query result in a paginated form with total pages and elements values
     *
     * @param specs the search query
     * @param pageNumber int value of the page
     * @param pageSize int value of the page size
     * @return List<?> of all the businesses in the page and number of total pages and elements
     */
    public PaginationInfo<ListingResponse> listingSearch(Specification<Listing> specs, String sortBy, int pageNumber, int pageSize) {
        Sort sort;

        switch (sortBy) {
            case "DATE_ASC":
                sort = Sort.by(Sort.Direction.ASC, "created");
                break;
            case "DATE_DESC":
                sort = Sort.by(Sort.Direction.DESC, "created");
                break;
            case "TITLE_AZ":
                sort = Sort.by(Sort.Direction.ASC, "inventoryItem.product.name");
                break;
            default: // "TITLE_ZA"
                sort = Sort.by(Sort.Direction.DESC, "inventoryItem.product.name");
                break;
        }

        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, sort);

        Page<Listing> page = listingRepository.findAll(Specification.where(specs), pageRequest);
        List<Listing> listings = page.getContent();

        int totalPages = page.getTotalPages();
        long totalElements = page.getTotalElements();

        return new PaginationInfo<>(responseFactory.getListingResponses(listings, true), totalPages, totalElements);
    }

    /**
     * Get one listing with the corresponding ID from the database, return it as the DTO ListingResponse
     *
     * @param id of listing to fetch
     * @return DTO ListingResponse
     */
    public ListingResponse getOneListing(Long id) {
        Listing listing = listingRepository.getListingById(id);
        return responseFactory.getListingResponse(listing, true);
    }

    /**
     * Delete Listing from the listings table update the associated business' sales history
     *
     * @param listingId id of listing to delete (being bought)
     * @return newly created sale
     */
    @Transactional
    public Sale buyListing(Long listingId, User user) {
        Listing listing = listingRepository.getListingById(listingId);
        Sale sale = saleService.createSaleFromListing(listingId);
        sale = saleRepository.save(sale);

        notificationService.generateBoughtNotification(sale, user);

        notificationService.notifyAllListingLikers(listing, user);
        listing.removeAllLikedUsers();

        listingRepository.deleteById(listingId);
        return sale;
    }

    /**
     * Get sort options for searching for inventory items
     *
     * @param sortBy Sort by string
     * @return Sort object to sort users by specified attribute
     */
    private Sort getSortType(String sortBy)
    {
        Sort sort;
        switch (sortBy) {
            case "ID_ASC":
                sort = Sort.by(Sort.Order.asc("id"));
                break;
            case "ID_DESC":
                sort = Sort.by(Sort.Order.desc("id"));
                break;
            case "NAME_ASC":
                sort = Sort.by(Sort.Order.asc("inventoryItem.product.name"), Sort.Order.desc("id"));
                break;
            case "NAME_DESC":
                sort = Sort.by(Sort.Order.desc("inventoryItem.product.name"), Sort.Order.desc("id"));
                break;
            case "QUANTITY_ASC":
                sort = Sort.by(Sort.Order.asc("quantity"), Sort.Order.desc("id"));
                break;
            case "QUANTITY_DESC":
                sort = Sort.by(Sort.Order.desc("quantity"), Sort.Order.desc("id"));
                break;
            case "PRICE_ASC":
                sort = Sort.by(Sort.Order.asc("price"), Sort.Order.desc("id"));
                break;
            case "PRICE_DESC":
                sort = Sort.by(Sort.Order.desc("price"), Sort.Order.desc("id"));
                break;
            case "CREATED_ASC":
                sort = Sort.by(Sort.Order.asc("created"), Sort.Order.desc("id"));
                break;
            case "CREATED_DESC":
                sort = Sort.by(Sort.Order.desc("created"), Sort.Order.desc("id"));
                break;
            case "CLOSES_ASC":
                sort = Sort.by(Sort.Order.asc("closes"), Sort.Order.desc("id"));
                break;
            case "CLOSES_DESC":
                sort = Sort.by(Sort.Order.desc("closes"), Sort.Order.desc("id"));
                break;
            default:
                sort = null;
        }
        return sort;
    }
}
