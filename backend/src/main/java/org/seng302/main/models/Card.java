package org.seng302.main.models;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The class Card. Creates a table of the same name with attributes as columns.
 */
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "card_creator")
    private User creator;

    @Column(name = "card_section")
    private String section;

    @Column(name = "card_date_created")
    private LocalDateTime created;

    @Column(name = "card_end_period")
    private LocalDateTime displayPeriodEnd;

    @Column(name = "card_title")
    private String title;

    @Column(name = "card_description")
    private String description;

    @ManyToMany
    @JoinTable(name = "card_keywords",
            joinColumns = @JoinColumn(name = "card_id"),
            inverseJoinColumns = @JoinColumn(name = "keyword_id"))
    private List<Keyword> keywords = new ArrayList<>();

    // A card may need many notification like expire and deleted
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Notification> notification = new ArrayList<>();

    // Check if a card have been added to notification database, will be set to false when renewed
    @Column(name = "notified", columnDefinition = "boolean default false")
    private boolean notified;

    /**
     * Card constructor
     *
     * @param creator instance of User who created the card
     * @param section of the card
     * @param created date of the card
     * @param displayPeriodEnd date of the card
     * @param title of the card
     * @param description of the card
     * @param keywords of the card
     */
    public Card(
            User creator,
            String section,
            LocalDateTime created,
            LocalDateTime displayPeriodEnd,
            String title,
            String description,
            List<Keyword> keywords
    ) {
        this.creator = creator;
        this.section = section;
        this.created = created;
        this.displayPeriodEnd = displayPeriodEnd;
        this.title = title;
        this.description = description;
        this.keywords = keywords;
    }

    // Getters

    /**
     * Retrieves the card's id
     *
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * Retrieves the card's creator
     *
     * @return creator
     */
    public User getCreator() {
        return creator;
    }

    /**
     * Retrieves the card's creator's id
     *
     * @return creator id
     */
    public Long getCreatorId() {
        return creator.getId();
    }

    /**
     * Retrieves the card's section
     *
     * @return section
     */
    public String getSection() {
        return section;
    }

    /**
     * Retrieves the card's created date
     *
     * @return created
     */
    public LocalDateTime getCreated() {
        return created;
    }

    /**
     * Retrieves the card's display period end date
     *
     * @return display period end date
     */
    public LocalDateTime getDisplayPeriodEnd() {
        return displayPeriodEnd;
    }

    /**
     * Retrieves the card's title
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Retrieves the card's description
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retrieves the card's keywords
     *
     * @return keywords
     */
    public List<Keyword> getKeywords() {
        return keywords;
    }

    public List<Notification> getNotification() {
        return notification;
    }

    public boolean isNotified() {
        return notified;
    }

    // Setters

    /**
     * Sets card's id
     *
     * @param newId newId
     */
    public void setId(Long newId) {
        this.id = newId;
    }

    /**
     * Sets card's creator
     *
     * @param newCreator newCreator
     */
    public void setCreator(User newCreator) {
        this.creator = newCreator;
    }

    /**
     * Sets card's section
     *
     * @param newSection newSection
     */
    public void setSection(String newSection) {
        this.section = newSection;
    }

    /**
     * Sets card's created date
     *
     * @param newCreated newCreated
     */
    public void setCreated(LocalDateTime newCreated) {
        this.created = newCreated;
    }

    /**
     * Sets card's display period end date
     *
     * @param newDisplayPeriodEnd newDisplayPeriodEnd
     */
    public void setDisplayPeriodEnd(LocalDateTime newDisplayPeriodEnd) {
        this.displayPeriodEnd = newDisplayPeriodEnd;
    }

    /**
     * Sets card's title
     *
     * @param newTitle newTitle
     */
    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    /**
     * Sets card's description
     *
     * @param newDescription newDescription
     */
    public void setDescription(String newDescription) {
        this.description = newDescription;
    }

    /**
     * Sets card's keywords
     *
     * @param newKeywords newKeywords
     */
    public void setKeywords(List<Keyword> newKeywords) {
        this.keywords = newKeywords;
    }

    /**
     * Set notified
     *
     * @param notified notified
     */
    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    /**
     * Creates a new notification for this card
     *
     * @param notification notification
     */
    public void addNotification(Notification notification) {
        this.notification.add(notification);
        this.notified = true;
    }

    /**
     * Removes a notification for this card
     *
     * @param notification to remove from list
     */
    public void removeNotification(Notification notification) {
        this.notification.remove(notification);
    }

}
