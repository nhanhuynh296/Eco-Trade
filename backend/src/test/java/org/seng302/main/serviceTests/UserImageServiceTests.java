package org.seng302.main.serviceTests;

import io.restassured.internal.util.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.models.Address;
import org.seng302.main.models.User;
import org.seng302.main.models.UserImage;
import org.seng302.main.repository.UserImageRepository;
import org.seng302.main.repository.UserRepository;
import org.seng302.main.services.UserImageService;
import org.seng302.main.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;

/**
 * Tests for the ImageService class
 */
@SpringBootTest
class UserImageServiceTests {
    @Autowired
    UserImageRepository userImageRepository;

    @Autowired
    UserImageService userImageService;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Value("${wasteless.images.rootdir}")
    private String imageRootDir;

    @Value("${wasteless.images.userdir}")
    private String userImageDir;

    File goodFile = new File("./src/test/resources/media/test_image.jpg");
    File badFile = new File("./src/test/resources/media/not_an_image.txt");

    private User user = new User("Juice", "", "Boy", "JB", "", "jb@juice.com",
            LocalDate.now(), "", new Address("", "", "", "", "", ""), LocalDate.now(), "ROLE_USER",
            "password123", "123", null);

    private UserImage testImageOne;

    @BeforeEach
    public void init() {
        userImageRepository.deleteAll();
        userRepository.deleteAll();

        user = userRepository.save(user);

        testImageOne = new UserImage("filename", "thumnailname", user);
        testImageOne = userImageRepository.save(testImageOne);
    }

    /**
     * Tests that an image exists in the repository
     */
    @Test
    void testImageExists() {
        int result = userImageService.checkImageExists(testImageOne.getId());
        Assertions.assertEquals(0, result);
    }

    /**
     * Tests that an image does not exist in the repository
     */
    @Test
    void testNoImageExists() {
        int result = userImageService.checkImageExists(testImageOne.getId()+100);
        Assertions.assertEquals(1, result);
    }

    /**
     * Given that the requesting user is not an admin and not the requested user, assert that the validation returns -1
     */
    @Test
    void testValidateImageRequestInvalidUser() {
        long result = userImageService.validateUserImageRequest(user.getId() + 1, user, null);
        Assertions.assertEquals(-1, result);
    }

    /**
     * If the requested user does not exist return -2
     */
    @Test
    void testValidateImageRequestUserNotFound() {
        user.setUserRole("ROLE_ADMIN");
        long result = userImageService.validateUserImageRequest(user.getId() + 1, user, null);
        Assertions.assertEquals(-2, result);
    }

    /**
     * Given the file extension is invalid (txt) return -3
     * @throws IOException
     */
    @Test
    void testValidateImageRequestInvalidFile() throws IOException {
        user.setUserRole("ROLE_ADMIN");
        MockMultipartFile mockFile = new MockMultipartFile("file", badFile.getName(), "text/txt",
                IOUtils.toByteArray(new FileInputStream(badFile))
        );

        long res = userImageService.validateUserImageRequest(user.getId(), user, mockFile);

        Assertions.assertEquals(-3, res);
    }

    /**
     * Given valid data, return 0
     * @throws IOException
     */
    @Test
    void testValidateImageRequestValidInput() throws IOException {
        user.setUserRole("ROLE_ADMIN");
        MockMultipartFile mockFile = new MockMultipartFile("file", goodFile.getName(), "image/png",
                IOUtils.toByteArray(new FileInputStream(goodFile))
        );

        long res = userImageService.validateUserImageRequest(user.getId(), user, mockFile);

        Assertions.assertEquals(0, res);
    }

    /**
     * Test we can upload an image to a user
     */
    @Test
    void testSaveImage() throws IOException {
        long imageId = userImageService.saveUserImage(user.getId(),
                new MockMultipartFile("file", goodFile.getName(), "image/png",
                        IOUtils.toByteArray(new FileInputStream(goodFile)))
        );

        Assertions.assertTrue(imageId > 0);
        Assertions.assertTrue(
                new File(String.format("./%s/%s/%d_%d_test_image.jpg",imageRootDir, userImageDir, imageId, user.getId()))
                        .exists()
        );
    }

    /**
     * Test we can upload an image to a user and it saves a thumbnail
     */
    @Test
    void testSaveThumbnail() throws IOException {

        long imageId = userImageService.saveUserImage(user.getId(),
                new MockMultipartFile("file", goodFile.getName(), "image/png",
                        IOUtils.toByteArray(new FileInputStream(goodFile)))
        );

        Assertions.assertTrue(imageId > 0);
        Assertions.assertTrue(
                new File(String.format("./%s/%s/%d_%d_thumbnail_test_image.jpg",imageRootDir, userImageDir, imageId, user.getId()))
                        .exists()
        );
    }
}
