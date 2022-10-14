package org.seng302.main.services;

import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import org.seng302.main.dto.response.InventoryItemResponse;
import org.seng302.main.dto.response.PaginationInfo;
import org.seng302.main.dto.responsefactory.ResponseFactory;
import org.seng302.main.helpers.AdminRoles;
import org.seng302.main.models.*;
import org.seng302.main.repository.BusinessRepository;
import org.seng302.main.repository.InventoryRepository;
import org.seng302.main.repository.ProductImageRepository;
import org.seng302.main.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Inventory Service that communicates with Inventory Repo
 */

@Service
@Log4j2
public class InventoryService {

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    private ResponseFactory responseFactory = new ResponseFactory();

    /**
     * Creates a new product for a business
     *
     * @param user instance of the current user
     * @param itemJson information provided
     * @return int -1 if the id of the product in the inventory item does not exist or
     * int -2 if the current user is not a primary admin of the business and is not a DGAA
     * int -3 if the provided inventory item contains incorrect information
     * int 0 if successful
     */
    public int createInventoryItem(User user, JSONObject itemJson, long businessId) {
        // Attempts to get the product id sent in the JSON, returns a bad request if not a number
        try {
            if (itemJson.getAsNumber("productId") == null) {
                return -1;
            }
        } catch (NumberFormatException error) {
            return -1;
        }

        Long productId = itemJson.getAsNumber("productId").longValue();
        Product product = productRepository.findProductById(productId);

        if (product == null) {
            return -1;
        }
        InventoryItem newItem = getInventoryItem(itemJson);

        Business business = businessRepository.findBusinessById(businessId);

        if (!isAdminOfBusinessOrGAA(user, business)) {
            return -2;
        }
        if (!isValidInventoryItem(newItem, businessId).isEmpty()) {
            return -3;
        }

        // If no total price is provided but a price per item is provided
        // then set the total price to be price per item multiplied by quantity
        if (newItem.getPricePerItem() != null && newItem.getTotalPrice() == null) {
            Double totalPrice1 = newItem.getPricePerItem() * newItem.getQuantity();
            newItem.setTotalPrice(totalPrice1);
        }

        return 0;
    }

    /**
     * Create an InventoryItem with the given JSON and Product
     *
     * @param itemJson POST Json data
     * @return InventoryItem with defined attributes
     */
    public InventoryItem getInventoryItem(JSONObject itemJson) {
        Long productId = itemJson.getAsNumber("productId").longValue();
        Product product = productRepository.findProductById(productId);
        InventoryItem newItem = new InventoryItem();

        newItem.setProduct(product);
        newItem.setQuantity(itemJson.getAsNumber("quantity").intValue());
        if (itemJson.getAsString("pricePerItem").length() > 0) {
            newItem.setPricePerItem(Double.parseDouble(itemJson.getAsString("pricePerItem")));
        } else {
            newItem.setPricePerItem(0.0);
        }
        if (itemJson.getAsString("totalPrice").length() > 0) {
            newItem.setTotalPrice(Double.parseDouble(itemJson.getAsString("totalPrice")));
        } else {
            newItem.setTotalPrice(0.0);
        }

        if (itemJson.getAsString("manufactured") != null) {
            newItem.setManufactured(DateUtil.getInstance().dateFromString(itemJson.getAsString("manufactured")));
        } else {
            newItem.setManufactured(null);
        }

        if (itemJson.getAsString("sellBy") != null) {
            newItem.setSellBy(DateUtil.getInstance().dateFromString(itemJson.getAsString("sellBy")));
        } else {
            newItem.setSellBy(null);
        }

        if (itemJson.getAsString("bestBefore") != null) {
            newItem.setBestBefore(DateUtil.getInstance().dateFromString(itemJson.getAsString("bestBefore")));
        } else {
            newItem.setBestBefore(null);
        }

        if (itemJson.getAsString("expires") != null) {
            newItem.setExpires(DateUtil.getInstance().dateFromString(itemJson.getAsString("expires")));
        } else {
            newItem.setExpires(null);
        }
        return newItem;
    }

    /**
     * Persist item into the repository
     *
     * @param newInventoryItem item to save
     * @return the id of the newly created item
     */
    public Long saveItemInRepository(InventoryItem newInventoryItem) {
        InventoryItem savedItem = inventoryRepository.save(newInventoryItem);
        return savedItem.getId();
    }

    /**
     * Get all items from a businesses inventory paginated
     *
     * @param businessId Business id
     * @return JSON serialized business inventory
     */
    @Transactional
    public PaginationInfo<InventoryItemResponse> getInventoryItems(long businessId, int pageNumber, int pageSize, String sortBy) {
        Pageable pageRequest;
        Sort sortingAttributes = getSortType(sortBy);
        if (sortingAttributes != null) {
            pageRequest = PageRequest.of(pageNumber - 1, pageSize, sortingAttributes);
        } else {
            pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        }
        Page<InventoryItem> page =  inventoryRepository.findInventoryItemsByProductBusinessId(businessId, pageRequest);
        return new PaginationInfo<>(responseFactory.getInventoryItemResponses(page.getContent()), page.getTotalPages(), page.getTotalElements());
    }

    /**
     * Updates the inventory item that is passed with the provided business ID
     *
     * @param inventoryItemId id of item to change
     * @param businessId ID of the business
     * @param itemJson InventoryItem with updated values
     * @return int -1 if the product id doesn't match a valid product
     * -2 if the inventory item contains invalid details
     * -3 if the business is invalid
     * 0 if successfully updated
     */
    public int updateInventoryItem(long inventoryItemId, long businessId, JSONObject itemJson, User user) {

        // Validates that the product id that is sent in the body is a valid number.
        try {
            if (itemJson.getAsNumber("productId") == null) {
                return -1;
            }
        } catch (NumberFormatException error) {
            return -1;
        }

        Long productId = itemJson.getAsNumber("productId").longValue();
        Product product = productRepository.findProductById(productId);

        if (product == null) {
            return -1;
        }

        Business business = businessRepository.findBusinessById(businessId);
        InventoryItem newItem = getInventoryItem(itemJson);

        if (!isValidInventoryItem(newItem, businessId).equals("")) {
            return -2;
        }
        if (!isAdminOfBusinessOrGAA(user, business)) {
            return -3;
        }

        inventoryRepository.updateInventoryItem(
                inventoryItemId,
                newItem.getQuantity(),
                newItem.getPricePerItem(),
                newItem.getTotalPrice(),
                newItem.getManufactured(),
                newItem.getSellBy(),
                newItem.getBestBefore(),
                newItem.getExpires());
        return 0;
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
                sort = Sort.by(Sort.Order.asc("product.name"), Sort.Order.desc("id"));
                break;
            case "NAME_DESC":
                sort = Sort.by(Sort.Order.desc("product.name"), Sort.Order.desc("id"));
                break;
            case "QUANTITY_ASC":
                sort = Sort.by(Sort.Order.asc("quantity"), Sort.Order.desc("id"));
                break;
            case "QUANTITY_DESC":
                sort = Sort.by(Sort.Order.desc("quantity"), Sort.Order.desc("id"));
                break;
            case "INDIVIDUAL_PRICE_ASC":
                sort = Sort.by(Sort.Order.asc("pricePerItem"), Sort.Order.desc("id"));
                break;
            case "INDIVIDUAL_PRICE_DESC":
                sort = Sort.by(Sort.Order.desc("pricePerItem"), Sort.Order.desc("id"));
                break;
            case "TOTAL_PRICE_ASC":
                sort = Sort.by(Sort.Order.asc("totalPrice"), Sort.Order.desc("id"));
                break;
            case "TOTAL_PRICE_DESC":
                sort = Sort.by(Sort.Order.desc("totalPrice"), Sort.Order.desc("id"));
                break;
            case "MANUFACTURED_ASC":
                sort = Sort.by(Sort.Order.asc("manufactured"), Sort.Order.desc("id"));
                break;
            case "MANUFACTURED_DESC":
                sort = Sort.by(Sort.Order.desc( "manufactured"), Sort.Order.desc("id"));
                break;
            case "EXPIRY_ASC":
                sort = Sort.by(Sort.Order.asc("expires"), Sort.Order.desc("id"));
                break;
            case "EXPIRY_DESC":
                sort = Sort.by(Sort.Order.desc("expires"), Sort.Order.desc("id"));
                break;
            default:
                sort = null;
        }
        return sort;
    }

    /**
     * Validates that a given InventoryItem has correct values.
     *
     * @param item InventoryItem to be added to the database
     * @return A string representing the error message, the
     * message if empty if there are no errors found.
     */
    public String isValidInventoryItem(InventoryItem item, long businessId) {
        String message = "";

        LocalDate sellBy = item.getSellBy();
        LocalDate bestBy = item.getBestBefore();
        LocalDate expiry = item.getExpires();
        LocalDate manufactured = item.getManufactured();

        // Checks quantity and expiry date are mandatory fields (not null)
        if (item.getExpires() == null) {
            message += "Inventory items must have a specified expiry date.\n";
        }
        if (item.getQuantity() == null) {
            message += "Inventory quantity must be a positive, whole number.\n";
        }
        // If provided, total price cannot be less that the price per item
        if (item.getTotalPrice() != null && item.getTotalPrice() < item.getPricePerItem()) {
            message += "The total price of an item cannot be less than the price per item.\n";
        }
        // Quantity, price per item and total price cannot be negative
        if (item.getQuantity() < 0) {
            message += "Inventory items cannot have a negative quantity.\n";
        }
        if (item.getPricePerItem() < 0) {
            message += "Inventory items cannot have a negative price per item.\n";
        }
        if (item.getTotalPrice() < 0) {
            message += "Inventory items cannot have a negative total price.\n";
        }
        // Sell by date and expiry date must be in the future
        if (sellBy != null && sellBy.isBefore(LocalDate.now())) {
            message += "Sell by date cannot be in the past.\n";
        }
        if (expiry != null && expiry.isBefore(LocalDate.now())) {
            message += "Expiry date cannot be in the past.\n";
        }
        // If provided, manufactured date must be the earliest date
        if (manufactured != null &&
                ((expiry != null && manufactured.isAfter(expiry)) ||
                        (sellBy != null && manufactured.isAfter(sellBy)) ||
                        (bestBy != null && manufactured.isAfter(bestBy)))) {
            message += "Items cannot be manufactured after their best by, sell by, or expiry dates.\n";
        }
        // If provided, best before and sell by dates must be before the expiry date
        if (bestBy != null && expiry != null && bestBy.isAfter(expiry)) {
            message += "Items cannot have a best by date that is after their expiry date.\n";
        }
        if (sellBy != null && expiry != null && sellBy.isAfter(expiry)) {
            message += "Items cannot have a sell by date that is after their expiry date.\n";
        }
        // If provided, the manufactured date must be in the past
        if (manufactured != null && manufactured.isAfter(LocalDate.now())) {
            message += "Items cannot have a manufactured date that is in the future.\n";
        }
        if (item.getProduct().getBusiness().getId() != businessId) {
            message += "The product ID is invalid.\n";
        }

        return message.trim();
    }

    /**
     * Checks if the user is an admin of the business OR is a DGAA
     *
     * @param user instance of the user
     * @param business instance of the business
     * @return boolean if the user is an admin of the business OR is a DGAA
     */
    public boolean isAdminOfBusinessOrGAA(User user, Business business) {
        return (user != null && business.getAdministrators().contains(user)) ||
                Objects.requireNonNull(user).getUserRole().equals(AdminRoles.ROLE_DEFAULT_ADMIN.name()) ||
                business.getPrimaryAdministratorId().equals(user.getId());
    }

}
