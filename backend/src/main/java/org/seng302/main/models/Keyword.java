package org.seng302.main.models;

import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * The class Keyword. Creates a table of the same name with attributes as columns.
 *
 * Represents a single 'keyword' within the system - similar to a hashtag.
 */
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "keyword_name")
    private String name;

    @Column(name = "keyword_created")
    private LocalDate created;

    @ManyToMany(mappedBy = "keywords")
    private List<Card> cards = new ArrayList<>();

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    /**
     * Keyword constructor
     *
     * @param name of the keyword
     * @param created date of the keyword
     */
    public Keyword(
            String name,
            LocalDate created
    ) {
        this.name = name;
        this.created = created;
    }

    // Getters

    /**
     * Retrieves the keyword's id
     *
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * Retrieves the keyword's name
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the keyword's created date
     *
     * @return created
     */
    public LocalDate getCreated() {
        return created;
    }

    // Setters

    /**
     * Sets keyword's id
     *
     * @param newId newId
     */
    public void setId(Long newId) {
        this.id = newId;
    }

    /**
     * Sets keyword's name
     *
     * @param newName newName
     */
    public void setName(String newName) {
        this.name = newName;
    }

    /**
     * Sets keyword's created date
     *
     * @param newDate newDate
     */
    public void setCreated(LocalDate newDate) {
        this.created = newDate;
    }

    /**
     * Gets all the cards with the keyword
     *
     * @return List of Card instances
     */
    public List<Card> getCards() {
        return cards;
    }
}
