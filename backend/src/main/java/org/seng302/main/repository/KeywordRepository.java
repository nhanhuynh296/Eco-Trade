package org.seng302.main.repository;

import org.seng302.main.models.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long>, JpaSpecificationExecutor<Keyword> {

    /**
     * Retrieves keyword by id
     *
     * @param id of the keyword
     * @return Keyword with the matching id
     */
    Keyword findKeywordById(Long id);

    /**
     * Checks if a keyword exists by name
     *
     * @param name of the keyword
     * @return true if keyword exists.
     */
    boolean existsKeywordByName(String name);

    /**
     * Find keywords based on name
     *
     * @param name Name of keyword
     * @return List of matching results
     */
    List<Keyword> findByNameIgnoreCaseContaining(String name);

    /**
     * Find a specific keyword by its name
     * @param name to find keyword by
     * @return List of matching result
     */
    Keyword findByName(String name);

    /**
     * Delete keyword by its id
     * @param id - of the keyword
     */
    void deleteById(Long id);

    /**
     * Get list of keywords that have a name in the given list
     * @param names to check
     * @return list of keywords
     */
    List<Keyword> findDistinctByNameIn(List<String> names);

}
