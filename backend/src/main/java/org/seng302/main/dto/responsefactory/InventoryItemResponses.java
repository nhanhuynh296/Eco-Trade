package org.seng302.main.dto.responsefactory;

import org.seng302.main.dto.response.InventoryItemResponse;
import org.seng302.main.models.InventoryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * InventoryItem Response factory builder class
 */
public class InventoryItemResponses {

    /**
     * Add explicit private constructor
     */
    private InventoryItemResponses() {}

    /**
     * Creates a single response for InventoryItem
     *
     * @param inventoryItem instance of InventoryItem
     * @return InventoryItem response
     */
    public static InventoryItemResponse getResponse(InventoryItem inventoryItem) {
        InventoryItemResponse inventoryItemResponse = new InventoryItemResponse();

        inventoryItemResponse.setId(inventoryItem.getId());
        inventoryItemResponse.setProduct(ProductResponses.getResponse(inventoryItem.getProduct()));
        inventoryItemResponse.setQuantity(inventoryItem.getQuantity());
        inventoryItemResponse.setPricePerItem(inventoryItem.getPricePerItem());
        inventoryItemResponse.setTotalPrice(inventoryItem.getTotalPrice());
        inventoryItemResponse.setManufactured(inventoryItem.getManufactured());
        inventoryItemResponse.setSellBy(inventoryItem.getSellBy());
        inventoryItemResponse.setBestBefore(inventoryItem.getBestBefore());
        inventoryItemResponse.setExpires(inventoryItem.getExpires());

        return inventoryItemResponse;
    }

    /**
     * Creates a list of InventoryItem responses
     *
     * @param inventoryItems List of InventoryItem instances
     * @return List of InventoryItem responses
     */
    public static List<InventoryItemResponse> getAllResponses(List<InventoryItem> inventoryItems) {
        List<InventoryItemResponse> inventoryItemResponses = new ArrayList<>();

        for (InventoryItem inventoryItem: inventoryItems) {
            inventoryItemResponses.add(getResponse(inventoryItem));
        }

        return inventoryItemResponses;
    }

}
