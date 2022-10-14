package org.seng302.main.models;


import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Business class. Creates a table of same name and attributes.
 */
@JsonIdentityInfo(generator= ObjectIdGenerators.IntSequenceGenerator.class, property="id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString
@Getter
@Setter
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class Business {

    @Id // this field (attribute) is the table primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    @Column(name = "business_id")
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private List<User> administrators = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "business")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Product> products;

    // The ID of the 'primary' administrator of the business. This is automatically given to the creator of the business.
    @Column(name = "primary_administrator_id")
    private Long primaryAdministratorId;

    @Column(nullable = false)
    private String name;

    private String description;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "address_id", referencedColumnName = "ad_id")
    private Address address;

    @Column(name = "country_for_currency")
    private String countryForCurrency;

    @Column(name = "business_type", nullable = false)
    private String businessType;

    private LocalDate created;

    /**
     * Business constructor.
     *
     * @param name name of business
     * @param primaryAdministratorId default to creator of business
     * @param description business description
     * @param address business address
     * @param businessType business type
     * @param created create local date
     */
    public Business(Long primaryAdministratorId, String name, String description, Address address, String businessType, LocalDate created) {
        this.primaryAdministratorId = primaryAdministratorId;
        this.name = name;
        this.description = description;
        this.address = address;
        this.businessType = businessType;
        this.created = created;
    }

    // Getters ---------------------------------------------------------------------------------------------------------

    /**
     * Gets business id
     *
     * @return id of business
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the names of all administrators for this business
     *
     * @return A list of strings with all admin names
     */
    public List<String> getAdministratorsNames() {
        List<String> adminNames = new ArrayList<>();
        for (User user : administrators) {
            adminNames.add(user.getFirstName() + ' ' + user.getLastName());
        }
        return adminNames;
    }

    /**
     * Gets the country for currency
     *
     * @return String country for currency
     */
    public String getCountryForCurrency() {
        if (countryForCurrency == null && this.getAddress() != null) {
            countryForCurrency = this.getAddress().getCountry();
        }
        return countryForCurrency;
    }

    /**
     * Gets list of administrators
     *
     * @return administrators list
     */
    public List<User> getAdministrators() {
        return administrators;
    }

    /**
     * Gets id of primary admin
     *
     * @return id of admin
     */
    public Long getPrimaryAdministratorId() {
        return primaryAdministratorId;
    }

    /**
     * Gets business name
     *
     * @return name of business
     */
    public String getName() {
        return name;
    }

    /**
     * Gets description of business
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets address of business
     *
     * @return address entity of business
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Gets business type
     *
     * @return type of business
     */
    public String getBusinessType() {
        return businessType;
    }

    /**
     * Gets date business account created on
     *
     * @return created date
     */
    public LocalDate getCreated() {
        return created;
    }

    /**
     * Gets the items in a businesses catalogue
     *
     * @return list of items in business catalogue
     */
    public List<Product> getItemsBusinessCatalogue() {
        return products;
    }

    // Setters ---------------------------------------------------------------------------------------------------------

    /**
     * Sets business id
     *
     * @param id new id of business
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Sets administrator list for business
     *
     * @param admin list of admins
     */
    public void setAdministrators(List<User> admin) {
        this.administrators = admin;
    }

    /**
     * Sets id of primary admin for business
     *
     * @param primaryId id of admin (creator)
     */
    public void setPrimaryAdministratorId(Long primaryId) {
        this.primaryAdministratorId = primaryId;
    }

    /**
     * Sets business name
     *
     * @param name new name of business
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets description of business
     *
     * @param description description of business
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets address of business
     *
     * @param address address of business
     */
    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     * Sets business type of business
     *
     * @param businessType new business type
     */
    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    /**
     * Sets created date of business
     *
     * @param created created date
     */
    public void setCreated(LocalDate created) {
        this.created = created;
    }

    /**
     * Adds an admin to a businesses list of administrators
     *
     * @param admin user to be added to admin list
     */
    public void addAdmin(User admin) {
        this.administrators.add(admin);
    }

    /**
     * Removes an admin from a business list of administrators
     *
     * @param admin user to be removed from admin list
     */
    public void removeAdmin(User admin) {
        this.administrators.remove(admin);
    }

    /**
     * Adds a product to the business catalogue
     *
     * @param product product to be added to catalogue
     */
    public void addItemToCatalogue(Product product) {
        if (this.products == null) {
            this.products = new ArrayList<>();
        }

        this.products.add(product);
    }

    /**
     * Removes an item from the business catalogue
     *
     * @param product to be removed from catalogue
     */
    public void removeItemFromCatalogue(Product product) {
        this.products.remove(product);
    }

}
