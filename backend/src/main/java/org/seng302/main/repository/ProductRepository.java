package org.seng302.main.repository;

import org.seng302.main.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Query that retrives all the products with the specified business ID with pagination
     *
     * @param businessId ID of the selected business
     * @return Page<Product> List of products with pagination info
     */
    Page<Product> findProductsByBusinessId(Long businessId, Pageable pageable);

    /**
     * Query that retrieves the product with the specified productId
     *
     * @param productId Id of selected product
     * @return A product or null if not found
     */
    Product findProductById(Long productId);

    /**
     * Query that modifies a product in a business catalogue
     *
     * @param businessId ID of the associated business
     * @param productId  ID of the product being updated
     */
    @Transactional
    @Modifying(flushAutomatically = true)
    @Query("UPDATE Product p " +
            "SET p.description = :description, p.name = :name, p.manufacturer = :manufacturer, p.recommendedRetailPrice = :price " +
            "WHERE p.businessId = :businessId AND p.id = :productId")
    void updateProductInBusiness(@Param("businessId") Long businessId,
                                 @Param("productId") Long productId,
                                 @Param("description") String description,
                                 @Param("name") String name,
                                 @Param("manufacturer") String manufacturer,
                                 @Param("price") Double price);

    @Transactional
    @Modifying(flushAutomatically = true)
    @Query("UPDATE Product p " +
            "SET p.primaryImageId = :primaryImageId " +
            "WHERE p.id = :productId")
    void updatePrimaryProductImage(@Param("primaryImageId") Long primaryImageId,
                                   @Param("productId") Long productId);
}
