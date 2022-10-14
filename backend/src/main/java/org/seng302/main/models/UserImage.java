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
public class UserImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    private User user;

    @Column(name = "filename") // map camelcase name (java) to snake case (SQL)
    private String filename;

    @Column(name = "thumbnail_filename") // map camelcase name (java) to snake case (SQL)
    private String thumbnailFilename;

    /**
     * Constructor for image object
     *
     * @param filename Image filename
     * @param thumbnailFilename Image thumbnail filename (Smaller version of image)
     * @param user to store
     */
    public UserImage(String filename, String thumbnailFilename, User user) {
        this.filename = filename;
        this.thumbnailFilename = thumbnailFilename;
        this.user = user;
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
     * Sets the user for this image
     *
     * @param user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Get user
     *
     * @return the user that owns this image
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Get user id
     *
     * @return id of the user who owns this image
     */
    public Long getUserId() {
        return this.user.getId();
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
