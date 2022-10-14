package org.seng302.main.repository;

import org.seng302.main.models.InventoryItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {

    /**
     * Get all inventory items from a specific business
     *
     * @param businessId ID of the business
     * @return List of inventory items
     */
    @Query("SELECT inv FROM InventoryItem AS inv WHERE inv.product.businessId = ?1")
    List<InventoryItem> getAllFromBusinessInventory(long businessId);

    /**
     * Get all inventory items from a specific business with pagination
     *
     * @param businessId ID of the business
     * @param pageable   Page object
     * @return Page<InventoryItem> - List of InventoryItem and pagination info
     */
    @Query("SELECT i FROM InventoryItem i WHERE i.product.businessId = ?1")
    Page<InventoryItem> findInventoryItemsByProductBusinessId(Long businessId, Pageable pageable);

    /**
     * Query that retrieves the inventory with the specified inventory
     *
     * @param inventoryId of selected product
     * @return A inventory or null if not found
     */
    InventoryItem findInventoryItemById(Long inventoryId);

    @Query("SELECT inv FROM InventoryItem AS inv WHERE inv.product.id = ?1")
    List<InventoryItem> getInventoryItemsByProductId(Long productId);

    /**
     * Updates the provided inventory item
     *
     * @param id           ID of the inventory item
     * @param quantity     of the inventory item
     * @param pricePerItem of the inventory item
     * @param totalPrice   of the inventory item
     * @param manufactured date of the inventory item
     * @param sellBy       date of the inventory item
     * @param bestBefore   date of the inventory item
     * @param expires      date of the inventory item
     */
    @Transactional
    @Modifying(flushAutomatically = true)
    @Query(
            "UPDATE InventoryItem i SET i.quantity =:quantity, i.pricePerItem = :pricePerItem, i.totalPrice = :totalPrice, "
                    +
                    "i.manufactured = :manufactured, i.sellBy = :sellBy, i.bestBefore = :bestBefore, i.expires = :expires "
                    +
                    "WHERE i.id = :id")
    void updateInventoryItem(@Param("id") long id, @Param("quantity") Integer quantity, @Param("pricePerItem") Double pricePerItem,
                             @Param("totalPrice") Double totalPrice, @Param("manufactured") LocalDate manufactured,
                             @Param("sellBy") LocalDate sellBy, @Param("bestBefore") LocalDate bestBefore,
                             @Param("expires") LocalDate expires);

}
