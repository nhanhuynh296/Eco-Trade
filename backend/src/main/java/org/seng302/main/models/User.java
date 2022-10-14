package org.seng302.main.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The class User. Creates a table of the same name with attributes as columns.
 */
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class User {
    @Id // this field (attribute) is the table primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    @Column(name = "user_id")
    private Long id;

    @Column(name = "first_name", nullable = false) // map camelcase name (java) to snake case (SQL)
    private String firstName;

    @Column(name = "middle_name") // map camelcase name (java) to snake case (SQL)
    private String middleName;

    @Column(name = "last_name", nullable = false) // map camelcase name (java) to snake case (SQL)
    private String lastName;

    private String nickname;

    private String bio;

    @Column(nullable = false)
    private String email;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "phone_number")
    private String phoneNumber;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "address_id", referencedColumnName = "ad_id")
    private Address homeAddress;

    private LocalDate created;

    @Column(name = "user_role")
    private String userRole;

    @Column(nullable = false)
    private String password;

    @Column(name = "session_ticket")
    private String sessionTicket;

    //Creates USER_BUSINESS table to help visualise the admins of business and the business a user is the admin of
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "administrators", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private List<Business> businessesAdministered;

    //For JPA only. This ensures that when a user is deleted, the mapped card is also deleted. Solves referential integrity
    //constraint violation as Card contains a foreign key to user.
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "creator")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Card> card;

    @Column(name = "primary_image_id")
    private Long primaryImageId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<UserImage> images;

    @Column(name = "country_for_currency")
    private String countryForCurrency;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "likes", cascade = CascadeType.ALL)
    private Set<Listing> likedListings = new HashSet<>();

    /**
     * User Constructor
     *
     * @param firstName user first name
     * @param middleName user middle name
     * @param lastName user last name
     * @param nickname the nickname
     * @param bio user last bio
     * @param email user email
     * @param dateOfBirth user date of birth
     * @param phoneNumber user phone number
     * @param homeAddress user home address
     * @param created the created
     * @param userRole the user role
     * @param password user password
     * @param sessionTicket login ticket used for user verification
     */
    public User(String firstName,
                String middleName,
                String lastName,
                String nickname,
                String bio,
                String email,
                LocalDate dateOfBirth,
                String phoneNumber,
                Address homeAddress,
                LocalDate created,
                String userRole,
                String password,
                String sessionTicket,
                String countryForCurrency) {

        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.bio = bio;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.homeAddress = homeAddress;
        this.created = created;
        this.userRole = userRole;
        this.password = password;
        this.sessionTicket = sessionTicket;
        this.countryForCurrency = countryForCurrency;
    }

    /**
     * On creation of the user the countryForCurrency is defaulted to the country of their home address
     */
    @PrePersist
    private void initializeCreatedAt() {
        this.countryForCurrency = this.homeAddress.getCountry();
    }

    /**
     * Gets the country for currency
     *
     * @return String of the country for currency
     */
    public String getCountryForCurrency() {
        return countryForCurrency;
    }

    /**
     * Sets the country for currency
     *
     * @param countryForCurrency String new country for currency
     */
    public void setCountryForCurrency(String countryForCurrency) {
        this.countryForCurrency = countryForCurrency;
    }

    /**
     * Gets user id.
     *
     * @return the user id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets user id.
     *
     * @param id the user id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets first name.
     *
     * @param firstName the first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets middle name.
     *
     * @return the middle name
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets middle name.
     *
     * @param middleName the middle name
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     * Gets last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets last name.
     *
     * @param lastName the last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets nickname.
     *
     * @return the nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Sets nickname.
     *
     * @param nickname the nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Gets bio.
     *
     * @return the bio
     */
    public String getBio() {
        return bio;
    }

    /**
     * Sets bio.
     *
     * @param bio the bio
     */
    public void setBio(String bio) {
        this.bio = bio;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets date of birth.
     *
     * @return the date of birth
     */
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets date of birth.
     *
     * @param dateOfBirth the date of birth
     */
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Gets phone number.
     *
     * @return the phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets phone number.
     *
     * @param phoneNumber the phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets home address.
     *
     * @return the home address
     */
    public Address getHomeAddress() {
        return homeAddress;
    }

    /**
     * Sets home address.
     *
     * @param homeAddress the home address
     */
    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    /**
     * Gets created.
     *
     * @return the created
     */
    public LocalDate getCreated() {
        return created;
    }

    /**
     * Sets created.
     *
     * @param created the created
     */
    public void setCreated(LocalDate created) {
        this.created = created;
    }

    /**
     * Gets user role.
     *
     * @return the user role
     */
    public String getUserRole() {
        return userRole;
    }

    /**
     * Sets user role.
     *
     * @param userRole the user role
     */
    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets password.
     *
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets session ticket.
     *
     * @return the session ticket
     */
    public String getSessionTicket() {
        return sessionTicket;
    }

    /**
     * Sets session ticket.
     *
     * @param sessionTicket the session ticket
     */
    public void setSessionTicket(String sessionTicket) {
        this.sessionTicket = sessionTicket;
    }

    /**
     * Gets businesses administered.
     *
     * @return the businesses administered
     */
    public List<Business> getBusinessesAdministered() {
        return businessesAdministered;
    }

    /**
     * Sets businesses administered.
     *
     * @param businessesAdministered the businesses administered
     */
    public void setBusinessesAdministered(List<Business> businessesAdministered) {
        this.businessesAdministered = businessesAdministered;
    }

    /**
     * Gets the primary id of image for this user
     *
     * @return id of primary image
     */
    public Long getPrimaryImageId() {
        return primaryImageId;
    }

    /**
     * Sets the primary image id for this user
     *
     * @param primaryImageId of product
     */
    public void setPrimaryImageId(Long primaryImageId) {
        this.primaryImageId = primaryImageId;
    }

    /**
     * Gets the user images
     *
     * @return List<UserImage> user images
     */
    public List<UserImage> getImages() {
        return images;
    }

    /**
     * Sets new user images
     *
     * @param images list of UserImage
     */
    public void setImages(List<UserImage> images) {
        this.images = images;
    }

    /**
     * Gets liked listings
     *
     * @return set containing the id's of the listings liked by this user
     */
    public Set<Listing> getLikedListings() { return likedListings;}

    /**
     * Sets liked listings
     *
     * @param likedListings - set of liked listings id's
     */
    public void setLikedListings(Set<Listing> likedListings) { this.likedListings = likedListings;}

}
