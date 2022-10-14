package org.seng302.main.repository;

import org.seng302.main.models.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long>, JpaSpecificationExecutor<Sale> {
    /**
     * Get all Sales for a given business in descending sale date
     *
     * @param id of business
     * @param pageable page to get (number and size)
     * @return a page of Sale entities
     */
    Page<Sale> getAllByBusinessIdOrderBySaleDateDesc(Long id, Pageable pageable);

    /**
     * Returns a list of sales between and including the start date to the end date.
     *
     * @param businessId id of the business
     * @param startDate custom date input by the user for where to start the sales report
     * @param endDate custom date input by the user for where to end the sales report
     * @return a list of sales
     */
    List<Sale> findAllByBusinessIdAndSaleDateBetween(Long businessId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Returns the newest sale items of the given business.
     *
     * @param businessID Id of business
     * @return List of sales
     * */
    @Query("SELECT s FROM Sale s WHERE s.saleDate in (SELECT MAX(s.saleDate) FROM Sale s WHERE s.businessId = ?1)")
    List<Sale> getNewestSale(Long businessID);

    /**
     * Returns the oldest sale items of the given business.
     *
     * @param businessID Id of business
     * @return List of sales
     * */
    @Query("SELECT s FROM Sale s WHERE s.saleDate in (SELECT MIN(s.saleDate) FROM Sale s WHERE s.businessId = ?1)")
    List<Sale> getOldestSale(Long businessID);
}
