package org.seng302.main.repository;

import org.seng302.main.models.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserImageRepository extends JpaRepository<UserImage, Long> {

    /**
     * Find user image by image id
     *
     * @param id of the image
     * @return Image model
     */
    @Query("SELECT img FROM UserImage AS img WHERE img.imageId = ?1")
    UserImage findImageByImageId(long id);

    /**
     * Find images from database by user id.
     *
     * @param userId unique user to retrieve images for
     * @return List of images that belong to the user
     */
    @Query("SELECT img FROM UserImage AS img WHERE img.user.id = ?1")
    List<UserImage> findImagesByUserId(Long userId);

}
