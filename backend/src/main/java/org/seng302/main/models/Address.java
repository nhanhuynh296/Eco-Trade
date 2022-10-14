package org.seng302.main.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.NoArgsConstructor;
import net.minidev.json.JSONObject;

import javax.persistence.*;

/**
 * Entity representing some physical address.
 */
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class Address {

    @Id
    @Column(name = "ad_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "street_number")
    private String streetNumber;

    @Column(name = "street_name")
    private String streetName;

    @Column
    private String city;

    @Column
    private String region;

    @Column
    private String country;

    @Column
    private String postcode;

    /**
     * Address constructor
     *
     * @param streetNumber address street number
     * @param streetName address street name
     * @param city address city
     * @param region address region
     * @param country address country
     * @param postcode address postcode
     */
    public Address(String streetNumber, String streetName, String city, String region, String country, String postcode) {
        this.streetNumber = streetNumber;
        this.streetName = streetName;
        this.city = city;
        this.region = region;
        this.country = country;
        this.postcode = postcode;
    }

// Getters ---------------------------------------------------------------------------------------------------------

    /**
     * Get id
     *
     * @return id of this address
     */
    public long getId() {
        return id;
    }

    /**
     * Gets street number
     *
     * @return street number
     */
    public String getStreetNumber() {
        return streetNumber;
    }

    /**
     * Gets street name
     *
     * @return street name
     */
    public String getStreetName() {
        return streetName;
    }

    /**
     * Gets city
     *
     * @return city
     */
    public String getCity() {
        return city;
    }

    /**
     * Gets region
     *
     * @return region
     */
    public String getRegion() {
        return region;
    }

    /**
     * Gets country
     *
     * @return country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Gets postcode
     *
     * @return postcode
     */
    public String getPostcode() {
        return postcode;
    }


    // Setters ---------------------------------------------------------------------------------------------------------

    /**
     * Sets address id
     *
     * @param id the address id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Sets address number
     *
     * @param number number
     */
    public void setStreetNumber(String number) {
        this.streetNumber = number;
    }

    /**
     * Sets address name
     *
     * @param name name
     */
    public void setStreetName(String name) {
        this.streetName = name;
    }

    /**
     * Sets address city
     *
     * @param city city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Sets address region
     *
     * @param region region
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * Sets address country
     *
     * @param country country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Sets address postcode
     *
     * @param postcode postcode
     */
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }


    /**
     * Test if address is valid, only country is required for valid address
     *
     * @return true if address country is not blank or null
     */
    public boolean isValidAddress() {
        return this.getCountry() != null && !this.getCountry().strip().equals("");
    }

    /**
     * Representation of address entity in json form
     *
     * @return JSON object of address
     */

    public JSONObject toJSON() {
        if (this.getCountry() == null) {
            return new JSONObject();
        }
        JSONObject entity = new JSONObject();
        entity.put("streetNumber", this.getStreetNumber());
        entity.put("streetName", this.getStreetName());
        entity.put("city", this.getCity());
        entity.put("region", this.getRegion());
        entity.put("country", this.getCountry());
        entity.put("postcode", this.getPostcode());
        return entity;
    }

    @Override
    public String toString() {
        String addressString = "";

        if (this.getStreetNumber() != null) {
            addressString += this.getStreetNumber() + ", ";
        }

        if (this.getStreetName() != null) {
            addressString += this.getStreetName() + ", ";
        }

        if (this.getCity() != null) {
            addressString += this.getCity() + ", ";
        }

        if (this.getRegion() != null) {
            addressString += this.getRegion() + ", ";
        }

        if (this.getCountry() != null) {
            addressString += this.getCountry() + ", ";
        }

        if (this.getPostcode() != null) {
            addressString += this.getPostcode() + ", ";
        }

        if (addressString.length() > 0) {
            addressString = addressString.substring(0, addressString.length() - 2);
        }

        return addressString;
    }

}
