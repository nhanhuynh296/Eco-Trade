package org.seng302.main.models;

import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * The class Listing. Creates a table of the same name with attributes as columns.
 */
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class Listing {

    @Id // this field (attribute) is the table primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "inventory_id", nullable = false)
    private InventoryItem inventoryItem;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "more_info")
    private String moreInfo;

    @Column(name = "created")
    private LocalDate created;

    @Column(name = "closes")
    private LocalDate closes;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id")
    private Set<User> likes = new HashSet<>();

    /**
     * Listing constructor
     *
     * @param inventoryItem id of corresponding inventory item
     * @param quantity Must be greater than zero, and less than or equal to the quantity of
     *                    the corresponding inventory entry
     * @param price Price of the listing
     * @param moreInfo An optional field for further information about the listing
     * @param created Date and time when the listing was created
     * @param closes Date and time when the listing closes (defaults to expiry date of
     *                    related inventory item
     */
    public Listing(
            InventoryItem inventoryItem,
            Integer quantity,
            Double price,
            String moreInfo,
            LocalDate created,
            LocalDate closes
    ) {
        this.inventoryItem = inventoryItem;
        this.quantity = quantity;
        this.price = price;
        this.moreInfo = moreInfo;
        this.created = created;
        this.closes = closes;
    }

    /**
     * Add a users like to this listing
     *
     * @param user user object
     */
    public void addLikedUser(User user) {
        this.likes.add(user);
        user.getLikedListings().add(this);
    }

    /**
     * Remove a users like from this listing
     *
     * @param user user object
     */
    public void removeLikedUser(User user) {
        this.likes.remove(user);
        user.getLikedListings().remove(this);
    }

    /**
     * Remove all likes from this listing and associated listings from the user
     */
    public void removeAllLikedUsers() {
        for (User user : this.likes) {
            user.getLikedListings().remove(this);
        }
        this.likes.clear();
    }

    /**
     * Get total amount of likes this listing has
     * @return Total amount of likes
     */
    public long getTotalLikes() {
        return this.likes.size();
    }

    /**
     * Returns list of users who liked this listing
     *
     * @return Set of users
     */
    public Set<User> getLikedUsers() {
        return this.likes;
    }

    /**
     * Returns whether or not the user has liked this listing
     *
     * @param user User object
     * @return True or false
     */
    public boolean hasUserLikedListing(User user) {
        return this.likes.contains(user);
    }

    // Getters ---------------------------------------------------------------------------------------------------------

    /**
     * @return id - unique (within the business) identifier for this listing - autogenerated
     */
    public Long getId() {
        return id;
    }

    /**
     * @return inventoryItem - id of corresponding inventory item
     */
    public InventoryItem getInventoryItem() {
        return inventoryItem;
    }

    /**
     * @return quantity - Must be greater than zero, and less than or equal to the quantity of the
     * corresponding inventory entry
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * @return price - Price of the listing
     */
    public Double getPrice() {
        return price;
    }

    /**
     * @return moreInfo - An optional field for further information about the listing
     */
    public String getMoreInfo() {
        return moreInfo;
    }

    /**
     * @return created - Date and time when the listing was created
     */
    public LocalDate getCreated() {
        return created;
    }

    /**
     * @return closes - Date and time when the listing closes (defaults to expiry date of related
     * inventory item
     */
    public LocalDate getCloses() {
        return closes;
    }

    // Setters ---------------------------------------------------------------------------------------------------------

    /**
     * @param inventoryItem linked with inventory
     */
    public void setInventoryItem(InventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
    }

    /**
     * @param quantity - Must be greater than zero, and less than or equal to the quantity of the
     *                 corresponding inventory entry
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * @param price - Price of the listing (in native currency)
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * @param moreInfo - An optional field for further information about the listing
     */
    public void setMoreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
    }

    /**
     * @param closes - Date and time when the listing closes
     */
    public void setCloses(LocalDate closes) {
        this.closes = closes;
    }

    /**
     * @param created - Date and time the listing was created
     */
    public void setCreated(LocalDate created) {
        this.created = created;
    }
}
