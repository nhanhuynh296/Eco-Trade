package org.seng302.main.repository.specificationHelper;

import org.seng302.main.dto.request.AddressRequest;
import org.seng302.main.models.Card;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * CardSpecification class
 *
 * Creates a card specification based on keywords
 */
public class CardSpecification {

    // Globals that without cause code smells
    static final String HOMEADDRESS = "homeAddress";
    static final String CREATOR = "creator";

    /**
     * Add a private constructor to hide the implicit public one.
     */
    private CardSpecification() {}

    /**
     *  Creates a specification for cards that are in the given section
     *
     * @param section grouping name
     */
    private static Specification<Card> hasSection(String section) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("section"), section);
    }

    /**
     *  Creates a specification for cards that have any or all of the given keywords
     *
     * @param keywords List of keyword names
     * @param operator AND/OR, indicates whether all the keywords must match or any of them
     * @return specification for card search
     */
    private static Specification<Card> hasKeywords(List<String> keywords, Operator operator) {
        final List<Predicate> predicates = new ArrayList<>();
        return ((root, criteriaQuery, criteriaBuilder) -> {

            criteriaQuery.distinct(true);

            for (String keyword : keywords) {
                Predicate predicate = criteriaBuilder.equal(root.join("keywords").get("name"),  keyword);
                predicates.add(predicate);
            }

            if (operator == Operator.AND) {
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            } else {
                return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
            }
        });

    }

    /**
     * Creates a specification for cards that are in the given country
     * @param country name
     */
    private static Specification<Card> hasCountry(String country) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join(CREATOR).join(HOMEADDRESS).get("country"),  country);
    }

    /**
     * Creates a specification for cards that are in the given region
     * @param region name
     */
    private static Specification<Card> hasRegion(String region) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join(CREATOR).join(HOMEADDRESS).get("region"),  region);
    }

    /**
     * Creates a specification for cards that are in the given city
     * @param city name
     */
    private static Specification<Card> hasCity(String city) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join(CREATOR).join(HOMEADDRESS).get("city"),  city);
    }

    /**
     *  Creates a specification for cards that match all the given filter options
     *
     * @param keywords list of keywords to filter cards by
     * @param section section of cards to filter
     * @param type and/or determines whether all keywords must match or any
     * @return specification for card search
     */
    public static Specification<Card> matchesAllFilters(List<String> keywords, String section, String type, AddressRequest address) {
        Specification<Card> specification = Specification.where(hasSection(section));
        if (!keywords.isEmpty()) {
            if ("or".equalsIgnoreCase(type)) {
                specification = specification.and(hasKeywords(keywords, Operator.OR));
            } else if ("and".equalsIgnoreCase(type)) {
                specification = specification.and(hasKeywords(keywords, Operator.AND));
            }
        }

        if (!address.getCountry().isEmpty()) {
            specification = specification.and(hasCountry(address.getCountry()));
        }

        if (!address.getRegion().isEmpty()) {
            specification = specification.and(hasRegion(address.getRegion()));
        }

        if (!address.getCity().isEmpty()) {
            specification = specification.and(hasCity(address.getCity()));
        }

        return specification;
    }
}
