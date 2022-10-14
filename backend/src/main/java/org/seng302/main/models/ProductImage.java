package org.seng302.main.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

/**
 * The class Image. Creates a table of the same name with attributes as columns.
 */
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    private Product product;

    @Column(name = "filename") // map camelcase name (java) to snake case (SQL)
    private String filename;

    @Column(name = "thumbnail_filename") // map camelcase name (java) to snake case (SQL)
    private String thumbnailFilename;

    /**
     * Constructor for image object
     *
     * @param filename Image filename
     * @param thumbnailFilename Image thumbnail filename (Smaller version of image)
     */
    public ProductImage(String filename, String thumbnailFilename) {
        this.filename = filename;
        this.thumbnailFilename = thumbnailFilename;
    }

    /**
     * Set image filename
     *
     * @param filename New filename
     */
    public void setImageFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Set image thumbnail filename
     *
     * @param filename New filename
     */
    public void setImageThumbnail(String filename) {
        this.thumbnailFilename = filename;
    }

    /**
     * Set Image id
     *
     * @param id of the image
     */
    public void setImageId(Long id) {
        this.imageId = id;
    }

    /**
     * Set product id
     *
     * @param newProduct that owns the image
     */
    public void setProduct(Product newProduct) {
        this.product = newProduct;
    }

    /**
     * Get product
     *
     * @return the product that owns this image
     */
    public Product getProduct() {
        return this.product;
    }

    /**
     * Get product id
     *
     * @return id of the product
     */
    public Long getProductId() {
        return this.product.getId();
    }

    /**
     * Get image filename
     *
     * @return image filename
     */
    public String getFilename() {
        return this.filename;
    }

    /**
     * Get image thumbnail filename
     *
     * @return image thumbnail filename
     */
    public String getThumbnailFilename() {
        return this.thumbnailFilename;
    }

    /**
     * Get Image id
     *
     * @return id of the image
     */
    public Long getId() {
        return this.imageId;
    }

}
