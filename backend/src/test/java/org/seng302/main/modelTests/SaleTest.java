package org.seng302.main.modelTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.models.Product;
import org.seng302.main.models.Sale;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Sale model test
 */
class SaleTest {

    private Sale sale;
    private Long businessId = 1L;
    private Product product = new Product();
    private LocalDate saleDate = LocalDate.now();
    private LocalDate listingDate = LocalDate.now();
    private Integer quantity = 1;
    private Long numLikes = 1L;
    private Double soldFor = 1.0;

    /**
     * Before each test a new instance of Sale is set
     */
    @BeforeEach
    public void init() {
        sale = new Sale(businessId, product, saleDate, listingDate, quantity, numLikes, soldFor);
    }

    /**
     * Tests getters for sale entity
     */
    @Test
    void testGetters() {
        assertEquals(businessId, sale.getBusinessId());
        assertEquals(product, sale.getProduct());
        assertEquals(saleDate, sale.getSaleDate());
        assertEquals(listingDate, sale.getListingDate());
        assertEquals(quantity, sale.getQuantity());
        assertEquals(numLikes, sale.getNumLikes());
        assertEquals(soldFor, sale.getSoldFor());
    }

    /**
     * Tests setters for sale entity
     */
    @Test
    void testSetters() {
        sale.setBusinessId(2L);
        sale.setProduct(null);
        sale.setSaleDate(null);
        sale.setListingDate(null);
        sale.setQuantity(null);
        sale.setSoldFor(null);

        assertEquals(Long.valueOf(2), sale.getBusinessId());
        assertNull(sale.getProduct());
        assertNull(sale.getSaleDate());
        assertNull(sale.getListingDate());
        assertNull(sale.getQuantity());
        assertNull(sale.getSoldFor());
    }

}
