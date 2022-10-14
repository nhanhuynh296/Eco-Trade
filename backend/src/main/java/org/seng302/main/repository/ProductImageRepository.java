package org.seng302.main.repository;

import org.seng302.main.models.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    /**
     * Find image by image id
     *
     * @param id of the image
     * @return Image model
     */
    @Query("SELECT img FROM ProductImage AS img WHERE img.imageId = ?1")
    ProductImage findImageByImageId(long id);

    /**
     * Find images from database by product_id
     *
     * @param productId Id of business
     * @return List of images that have the product ID
     */
    @Query("SELECT img FROM ProductImage AS img WHERE img.product.id = ?1")
    List<ProductImage> findImagesByProductId(Long productId);

    /**
     * Counts the number of listings for a specific product.
     * Does so by joining the inventory and listing table and using
     * the inventory's foreign product key.
     *
     * @param productID ID of a product
     * @return the total number of inventory items that have listings
     */
    @Query("SELECT COUNT(inv) FROM InventoryItem AS inv JOIN Listing AS lis ON inv.id = lis.inventoryItem.id WHERE inv.product.id = ?1")
    int getNumberOfListings(long productID);

}
