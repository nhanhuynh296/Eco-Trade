package org.seng302.main.services;

import lombok.extern.log4j.Log4j2;
import org.seng302.main.helpers.ImageHelper;
import org.seng302.main.models.User;
import org.seng302.main.models.UserImage;
import org.seng302.main.repository.UserImageRepository;
import org.seng302.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.List;

/**
 * Service for dealing with images
 */
@Service
@Log4j2
public class UserImageService {

    @Autowired
    private UserImageRepository userImageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @Value("${wasteless.images.rootdir}")
    private String rootDir;

    @Value("${wasteless.images.userdir}")
    private String userImagesDir;

    @Value("${wasteless.images.thumbnail.scale}")
    private Integer thumbnailScale;

    /**
     * Validate a post request to a requestingUser's images
     *
     * @param requestedUserId User that is being updated
     * @param requestingUser User adding the image
     * @param file image to be added
     * @return -1 if the requesting user is not an admin and not the same as the user being updated
     * -2 if the requested user does not exist
     * -3 if the image file is not of the right tpe (ImageHelper.validFileTypes)
     */
    public int validateUserImageRequest(long requestedUserId, User requestingUser, MultipartFile file) {

        if (requestingUser.getId() != requestedUserId && !userService.isApplicationAdmin(requestingUser)) {
            return -1;
        }
        User requestedUser = userRepository.findUserById(requestedUserId);
        if (requestedUser == null) {
            return -2;
        }

        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename()).toLowerCase();
        if (!ImageHelper.validFiletypes.contains(extension)) {
            return -3;
        }
        return 0;
    }

    /**
     * Create and save a new user image
     *
     * @param userId user being updated
     * @param file new image file
     * @return the newly created image id
     */
    public long saveUserImage(long userId, MultipartFile file) throws IOException {
        UserImage image = new UserImage();
        User user = userRepository.findUserById(userId);
        image.setUser(user);
        image = userImageRepository.save(image);

        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());

        String filename = String.format("%d_%d_%s", image.getId(), userId,
                StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));

        String thumbnailFilename = String.format("%d_%d_thumbnail_%s", image.getId(), userId,
                StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));

        Path folderPath = Paths.get(rootDir);
        folderPath = folderPath.resolve(userImagesDir);

        imageService.saveImage(folderPath, filename, file);
        imageService.saveImageThumbnail(folderPath.resolve(filename), folderPath.resolve(thumbnailFilename), extension, thumbnailScale, thumbnailScale);

        //Set image filename
        image.setImageFilename(filename);
        //Set image thumbnail filename
        image.setImageThumbnail(thumbnailFilename);

        image = userImageRepository.save(image);

        // If the product has no current primary image, also set primary image id of the product to image id
        if (userRepository.findUserById(userId).getPrimaryImageId() == null) {
            userService.setPrimaryImage(userId, image.getId());
        }
        return image.getId();
    }

    /**
     * Try and delete an image from the specified path within the images rootdir,
     * if image already doesn't with filename, do nothing.
     *
     * @param path Image path within root directory
     * @param filename Name of the file to be deleted
     * @throws IOException IOException if cannot be deleted
     */
    public void deleteImageAndThumbnail(Path path, String filename) throws IOException {
        Path filePath = path.resolve(filename);
        Files.delete(filePath);
    }

    /**
     * Find image in the database by image ID
     *
     * @param imageId ID of the image
     * @return Image object or null
     */
    public UserImage getImageById(long imageId) {
        return userImageRepository.findImageByImageId(imageId);
    }

    /**
     * Checks if the image exists.
     *
     * @param imageId id of the image to find
     * @return 0 if it exists,
     * 1 otherwise
     */
    public int checkImageExists(Long imageId) {
        if (getImageById(imageId) == null) {
            return 1;
        }
        return 0;
    }

    /**
     * Tries to send a user image in the directory to the client.
     *
     * @param imageId id of the user image
     * @return 200 OK status and the requested image
     * OR 404 Not Found status if it doesn't exist
     */
    public ResponseEntity<byte[]> sendImageFromDirectory(long imageId, boolean isThumbnail) {
        try {
            UserImage image = getImageById(imageId);

            if (image == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            HttpHeaders headers = new HttpHeaders();

            Path folderPath = Paths.get(rootDir);
            String filePath;
            folderPath = folderPath.resolve(userImagesDir);

            if (!isThumbnail) {
                filePath = folderPath.resolve(image.getFilename()).toString();
            } else {
                filePath = folderPath.resolve(image.getThumbnailFilename()).toString();
            }

            File img = new File(filePath);
            byte[] media = Files.readAllBytes(img.toPath());
            headers.setCacheControl(CacheControl.noCache().getHeaderValue());
            return new ResponseEntity<>(media, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("Failed to get requested user image");
            log.warn(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
