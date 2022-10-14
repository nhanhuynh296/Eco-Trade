package org.seng302.main.repository;

import org.seng302.main.models.Card;
import org.seng302.main.models.Keyword;
import org.seng302.main.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface CardRepository extends JpaRepository<Card, Long>, JpaSpecificationExecutor<Card> {

    /**
     * Gets all active cards from a given section
     *
     * @param section Card section to retrieve from
     * @return List of cards
     */
    Page<Card> findCardsBySectionAndDisplayPeriodEndAfter(String section, LocalDateTime now,
                                                          Pageable pageable);

    /**
     * Gets all cards from a given section
     *
     * @param section Card section to retrieve from
     * @param country Country of card creator to filter by
     * @return List of relevant cards
     */
    @Query("SELECT c " +
            "FROM Card c " +
            "JOIN User u ON c.creator = u.id " +
            "JOIN Address a ON u.homeAddress = a.id " +
            "WHERE c.section = ?1 " +
            "AND a.country = ?2 ")
    Page<Card> findAllBySectionAndCountry(String section, String country, Pageable pageable);

    /**
     * Gets all cards for a given user
     *
     * @param user User that created the cards
     * @return List of relevant cards
     */
    @Query("SELECT c " +
            "FROM Card c " +
            "WHERE c.creator = ?1")
    Page<Card> findCardsByCreator(User user, Pageable pageable);

    /**
     * Gets all cards from a given section
     *
     * @param section Card section to retrieve from
     * @param country Country of card creator to filter by
     * @param region  Region of card creator to filter by
     * @return List of relevant cards
     */
    @Query("SELECT c " +
            "FROM Card c " +
            "JOIN User u ON c.creator = u.id " +
            "JOIN Address a ON u.homeAddress = a.id " +
            "WHERE c.section = ?1 " +
            "AND a.country = ?2 " +
            "AND a.region = ?3 "
    )
    Page<Card> findAllBySectionAndCountryAndRegion(String section, String country, String region, Pageable pageable);

    /**
     * Gets all cards from a given section
     *
     * @param section Card section to retrieve from
     * @param country Country of card creator to filter by
     * @param region  Region of card creator to filter by
     * @param city    City of card creator to filter by
     * @return List of relevant cards
     */
    @Query("SELECT c " +
            "FROM Card c " +
            "JOIN User u ON c.creator = u.id " +
            "JOIN Address a ON u.homeAddress = a.id " +
            "WHERE c.section = ?1 " +
            "AND a.country = ?2 " +
            "AND a.region = ?3 " +
            "AND a.city = ?4"
    )
    Page<Card> findAllBySectionAndCountryAndRegionAndCity(String section, String country, String region, String city, Pageable pageable);

    /**
     * Retrieves a singular card by its unique id
     *
     * @param cardId unique id of the card
     * @return One card
     */
    Card getCardById(Long cardId);


    /**
     * Get a card that has expired and was not added to notification database
     *
     * @param time Display period end need to be before {@link LocalDateTime#now()}{@link
     *             LocalDateTime#plusDays(long 1)}
     * @return List of card
     */
    List<Card> findCardsByDisplayPeriodEndBeforeAndNotifiedFalse(LocalDateTime time);

    /**
     * Get all the cards the have expired
     */
    @Query("SELECT c FROM Card c WHERE c.displayPeriodEnd < ?1")
    List<Card> getExpiredCards(LocalDateTime time);

    List<Card> getCardsByKeywords(Keyword keyword);
}
