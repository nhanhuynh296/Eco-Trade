package org.seng302.main.dto.responsefactory;

import org.seng302.main.dto.response.ListingResponse;
import org.seng302.main.models.Listing;
import org.seng302.main.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Listing Response factory builder class
 */
public class ListingResponses {

    /**
     * Add explicit private constructor
     */
    private ListingResponses() {}

    /**
     * Creates a single response for listing
     *
     * @param listing instance of Listing
     * @param setBusiness boolean for setting business
     * @return listing response
     */
    public static ListingResponse getResponse(Listing listing, boolean setBusiness) {
        ListingResponse listingResponse = new ListingResponse();

        listingResponse.setId(listing.getId());
        listingResponse.setQuantity(listing.getQuantity());
        listingResponse.setPrice(listing.getPrice());
        listingResponse.setMoreInfo(listing.getMoreInfo());
        listingResponse.setCreated(listing.getCreated());
        listingResponse.setCloses(listing.getCloses());
        listingResponse.setInventoryItem(InventoryItemResponses.getResponse(listing.getInventoryItem()));

        if (setBusiness) {
            listingResponse.setBusiness(BusinessResponses.getPartialResponse(listing.getInventoryItem().getProduct().getBusiness()));
        }

        List<Long> likes = new ArrayList<>();
        for (User user : listing.getLikedUsers()) {
            likes.add(user.getId());
        }
        listingResponse.setLikes(likes);

        return listingResponse;
    }

    /**
     * Creates a list of listing responses
     *
     * @param listings List of Listing instances
     * @param setBusiness boolean for setting business
     * @return List of listing responses
     */
    public static List<ListingResponse> getAllResponses(List<Listing> listings, boolean setBusiness) {
        List<ListingResponse> listingResponses = new ArrayList<>();

        for (Listing listing: listings) {
            listingResponses.add(getResponse(listing, setBusiness));
        }

        return listingResponses;
    }

}
