package org.seng302.main.dto.responsefactory;

import org.seng302.main.dto.response.SaleResponse;
import org.seng302.main.models.Sale;

import java.util.ArrayList;
import java.util.List;

/**
 * Sale Response factory builder class
 */
public class SaleResponses {

    /**
     * Add explicit private constructor
     */
    private SaleResponses() {}

    /**
     * Creates a single response for sale
     *
     * @param sale instance of Sale
     * @return sale response
     */
    public static SaleResponse getResponse(Sale sale) {
        SaleResponse saleResponse = new SaleResponse();

        saleResponse.setId(sale.getSaleId());
        saleResponse.setBusinessId(sale.getBusinessId());
        saleResponse.setProduct(ProductResponses.getResponse(sale.getProduct()));
        saleResponse.setSaleDate(sale.getSaleDate());
        saleResponse.setListingDate(sale.getListingDate());
        saleResponse.setQuantity(sale.getQuantity());
        saleResponse.setNumLikes(sale.getNumLikes());
        saleResponse.setSoldFor(sale.getSoldFor());
        saleResponse.setBusiness(BusinessResponses.getPartialResponse(sale.getProduct().getBusiness()));

        return saleResponse;
    }

    /**
     * Creates a list of sale responses
     *
     * @param sales List of Sale instances
     * @return List of sale responses
     */
    public static List<SaleResponse> getAllResponses(List<Sale> sales) {
        List<SaleResponse> saleResponses = new ArrayList<>();

        for (Sale sale: sales) {
            saleResponses.add(getResponse(sale));
        }

        return saleResponses;
    }

}
