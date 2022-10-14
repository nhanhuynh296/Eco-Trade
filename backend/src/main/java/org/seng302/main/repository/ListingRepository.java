package org.seng302.main.repository;

import org.seng302.main.models.Listing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long>, JpaSpecificationExecutor<Listing>  {

    /**
     * Get all listing from a specific business
     *
     * @param businessId ID of the business
     * @return List of listing
     */
    @Query("SELECT ls FROM Listing AS ls WHERE ls.inventoryItem.product.businessId = :businessId")
    List<Listing> getAllFromBusinessInventory(Long businessId);

    /**
     * Get one listing using its id
     *
     * @param id of listing to get
     * @return Listing
     */
    Listing getListingById(long id);

    /**
     * Delete listing by it's id
     * @param id - of the listing
     */
    void deleteById(long id);

    /**
     * Get all listing from a specific business (with pagination). I am aware of the snake case but is the sub property expression)
     *
     * @param businessId ID of the business
     * @return List of listing
     * @see <a href="https://docs.spring.io/spring-data/jpa/docs/2.0.5.RELEASE/reference/html/#repositories.query-methods.query-property-expressions">https://docs.spring.io/spring-data/jpa/docs/2.0.5.RELEASE/reference/html/#repositories.query-methods.query-property-expressions</a>
     */
    Page<Listing> findByInventoryItem_Product_BusinessId(Long businessId, Pageable pageable);
}
