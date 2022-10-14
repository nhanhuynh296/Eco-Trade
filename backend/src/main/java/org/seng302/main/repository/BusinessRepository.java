package org.seng302.main.repository;

import org.seng302.main.models.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long>, JpaSpecificationExecutor<Business> {

    /**
     * Find business from database by id - no password
     *
     * @param businessId ID of business
     * @return Business that meets the credential
     */
    Business findBusinessById(Long businessId);

    /**
     * Find business from database by user id - no password
     *
     * @param userId ID of the user
     * @return List of businesses that correspond to the user ID
     */
    List<Business> findBusinessesByPrimaryAdministratorId(Long userId);

    /**
     * Finds business from database using user id and returns business name
     *
     * @param userId Id of user
     * @return List of business names with that user id present (can be multiple)
     */
    @Query("SELECT b.name FROM Business b WHERE b.primaryAdministratorId = ?1")
    ArrayList<String> findBusinessNameByUserId(Long userId);

}
