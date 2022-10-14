package org.seng302.main.models;

import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class Sale {

    @Id // this field (attribute) is the table primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    @Column(name = "sale_id")
    private Long id;

    @Column(name = "business_id", nullable = false)
    private Long businessId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "product", nullable = false)
    private Product product;

    @Column(name = "sale_date")
    private LocalDate saleDate;

    @Column(name = "listing_date")
    private LocalDate listingDate;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "number_of_likes")
    private Long numLikes;

    @Column(name = "sold_for")
    private Double soldFor;

    /**
     * Sale constructor
     *
     * @param businessId id of the business
     * @param product instance of Product
     * @param saleDate date of the sale
     * @param listingDate date of the listing
     * @param quantity total amount of sold product
     * @param numLikes number of likes from the users
     */
    public Sale(Long businessId,
                Product product,
                LocalDate saleDate,
                LocalDate listingDate,
                Integer quantity,
                Long numLikes,
                Double soldFor) {
        this.businessId = businessId;
        this.product = product;
        this.saleDate = saleDate;
        this.listingDate = listingDate;
        this.quantity = quantity;
        this.numLikes = numLikes;
        this.soldFor = soldFor;
    }

    /**
     * Gets the sale id
     *
     * @return Long sale id
     */
    public Long getSaleId() {
        return id;
    }

    /**
     * Gets the business id
     *
     * @return Long business id
     */
    public Long getBusinessId() {
        return businessId;
    }

    /**
     * Sets business id to a new id
     *
     * @param businessId Long
     */
    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    /**
     * Gets the product
     *
     * @return Product product
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Sets product to a new product
     *
     * @param product Product
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * Gets the sale date
     *
     * @return LocalDate sale date
     */
    public LocalDate getSaleDate() {
        return saleDate;
    }

    /**
     * Sets sale date to a new date
     *
     * @param saleDate LocalDate
     */
    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
    }

    /**
     * Gets the listing date
     *
     * @return LocalDate listing date
     */
    public LocalDate getListingDate() {
        return listingDate;
    }

    /**
     * Sets listing date to a new date
     *
     * @param listingDate LocalDate
     */
    public void setListingDate(LocalDate listingDate) {
        this.listingDate = listingDate;
    }

    /**
     * Gets the quantity
     *
     * @return Integer quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Sets quantity to a new value
     *
     * @param quantity Integer
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets number of likes
     *
     * @return Integer number of likes
     */
    public long getNumLikes() {
        return numLikes;
    }

    /**
     * Sets number of likes to a new value
     *
     * @param numLikes Integer
     */
    public void setNumLikes(long numLikes) {
        this.numLikes = numLikes;
    }

    /**
     * Gets sold for price
     *
     * @return Double sold for price
     */
    public Double getSoldFor() {
        return soldFor;
    }

    /**
     * Sets sold for price to a new value
     *
     * @param soldFor Double
     */
    public void setSoldFor(Double soldFor) {
        this.soldFor = soldFor;
    }

}
