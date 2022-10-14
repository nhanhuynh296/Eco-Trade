package org.seng302.main.models;

import com.fasterxml.jackson.annotation.*;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

/**
 * The class Product. Creates a table of the same name with attributes as columns.
 */
@JsonIdentityInfo(generator= ObjectIdGenerators.IntSequenceGenerator.class, property="id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "business", nullable = false)
    private Long businessId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @Column(name = "name", nullable = false) // map camelcase name (java) to snake case (SQL)
    private String name;

    @Column(name = "description") // map camelcase name (java) to snake case (SQL)
    private String description;

    @Column(name = "manufacturer") // map camelcase name (java) to snake case (SQL)
    private String manufacturer;

    @Column(name = "recommended_retail_price")
    private Double recommendedRetailPrice;

    @Column(name = "primary_image_id")
    private Long primaryImageId;

    private LocalDate created;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<ProductImage> images;

    /**
     * Product constructor
     *
     * @param businessId id of the business
     * @param business the business creating product
     * @param name Name
     * @param description Description
     * @param manufacturer Manufacturer
     * @param recommendedRetailPrice RRP
     * @param created Created On
     */
    public Product(
            Long businessId,
            Business business,
            String name,
            String description,
            String manufacturer,
            Double recommendedRetailPrice,
            LocalDate created
    ) {
        this.name = name;
        this.description = description;
        this.manufacturer = manufacturer;
        this.recommendedRetailPrice = recommendedRetailPrice;
        this.created = created;

        this.businessId = businessId;
        this.business = business;
    }

    /**
     * Sets created
     *
     * @param created the created
     */
    public void setCreated(LocalDate created) {
        this.created = created;
    }

    /**
     * Gets the id of the product
     *
     * @return id of product
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the name of the product
     *
     * @return name of product
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the product
     *
     * @return description of the product
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the recommended retail price of the product
     *
     * @return RRP of product
     */
    public Double getRecommendedRetailPrice() {
        return recommendedRetailPrice;
    }

    /**
     * Sets the recommended retail price of the product
     *
     * @param recommendedRetailPrice of product
     */
    public void setRecommendedRetailPrice(Double recommendedRetailPrice) {
        this.recommendedRetailPrice = recommendedRetailPrice;
    }

    /**
     * Gets the primary id of image for this product
     *
     * @return id of primary image
     */
    public Long getPrimaryImageId() {
        return primaryImageId;
    }

    /**
     * Sets the primary image id of the product
     *
     * @param primaryImageId of product
     */
    public void setPrimaryImageId(Long primaryImageId) {
        this.primaryImageId = primaryImageId;
    }

    /**
     * Sets the businessId of the product
     *
     * @param businessId of business
     */
    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    /**
     * Gets the businessId of the product
     *
     * @return Long business id
     */
    public Long getBusinessId() {
        return this.businessId;
    }

    /**
     * Gets the manufacturer of the product
     *
     * @return manufacturer of product
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Gets date product created on
     *
     * @return created date
     */
    public LocalDate getCreated() {
        return created;
    }

    /**
     * Sets the name of the product
     *
     * @param name new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the description of the product
     *
     * @param description new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the manufacturer of the product
     *
     * @param manufacturer new manufacturer
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * Sets the ID of the product
     *
     * @param id to be set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Sets the business of the product
     *
     * @param business The business
     */
    public void setBusiness(Business business) {
        this.business = business;
    }

    /**
     * Get business ID of the product
     *
     * @return business ID
     */
    public Business getBusiness() {
        return business;
    }

    /**
     * Gets product images
     *
     * @return List<ProductImage> list of product images
     */
    public List<ProductImage> getImages() {
        return images;
    }

    /**
     * Sets new product images
     *
     * @param images list of ProductImage
     */
    public void setImages(List<ProductImage> images) {
        this.images = images;
    }
}
