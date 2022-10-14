package gradle.cucumber.images;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.Cookie;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.seng302.main.models.Address;
import org.seng302.main.models.User;
import org.seng302.main.models.UserImage;
import org.seng302.main.repository.ProductRepository;
import org.seng302.main.repository.UserImageRepository;
import org.seng302.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;
import java.time.LocalDate;

import static io.restassured.RestAssured.given;

public class UserImagesStepDefinitions {
    @Value("${server.port}")
    private int PORT;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserImageRepository userImageRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Value("${wasteless.images.rootdir}")
    private String imageRootDir;

    @Value("${wasteless.images.userdir}")
    private String userImageDir;

    RequestSpecification request;
    String userSession;

    User user;
    UserImage image;
    File goodFile = new File("./src/test/resources/media/test_image.jpg");
    File badFile = new File("./src/test/resources/media/not_an_image.txt");
    long imageId;

    @Before
    public void init() {
        RestAssured.port = PORT;
        userRepository.deleteAll();
        userImageRepository.deleteAll();

        user = new User("Totally", "Real", "Name", "TRN", "Hello There",
                "abcdefgh@uclive.ac.nz", LocalDate.now(), "",
                new Address("", "", "", "", "", ""),
                LocalDate.now(), "ROLE_USER", passwordEncoder.encode("password123"), null, null);

        image = new UserImage("filename", "thumbnailName", user);
        request = given().log().uri();

    }

    @Given("I am logged in as a user with an existing image")
    public void iAmLoggedInAsAUserWithAnExistingImage() {
        user = userRepository.save(user);

        request.header("Content-Type", "application/json");
        Response response = request.body("{ \"email\":\"" + user.getEmail() + "\", \"password\":\"" + "password123" + "\"}")
                .post("/login");
        userSession = response.getHeader("Set-Cookie").substring(11);

        image = userImageRepository.save(image);
    }

    @When("I make the image the primary image")
    public void iMakeTheImageThePrimaryImage() {
        Response response = request
                .cookie(new Cookie.Builder("JSESSIONID", userSession).build())
                .put(String.format("/users/images/%d/makeprimary", image.getId()))
                .andReturn();

        Assert.assertEquals(200, response.getStatusCode());
    }

    @Then("The primary image id is set to the image id")
    public void theProductsPrimaryImageIdIsSetToTheImagesId() {
        Long primaryImageId = userRepository.findUserById(user.getId()).getPrimaryImageId();
        Assertions.assertEquals(image.getId(), primaryImageId);
    }

    //    AC5/AC7 - Posting an image ---------------------------------------------------

    @Given("I am logged in as a user with no images")
    public void i_am_logged_in_as_a_user_with_no_images() {
        user = userRepository.save(user);
        request.header("Content-Type", "application/json");
        Response response = request.body("{ \"email\":\"" + user.getEmail() + "\", \"password\":\"" + "password123" + "\"}")
                .post("/login");
        userSession = response.getHeader("Set-Cookie").substring(11);
    }

    @When("I upload an image to my profile")
    public void i_upload_an_image_to_my_profile() {
        Response response = request
                .header("Content-Type", "multipart/form-data")
                .multiPart("file", goodFile, "image/png")
                .cookie(new Cookie.Builder("JSESSIONID", userSession).build())
                .post(String.format("/users/%d/images/", user.getId()));
        String resp = response.asString();
        JsonPath js = new JsonPath(resp);
        imageId = Long.parseLong(js.getString("imageId"));
    }

    @Then("The image is saved and added my profile")
    public void the_image_is_saved_and_added_my_profile() {
        Assertions.assertTrue(
                new File(String.format("./%s/%s/%d_%d_test_image.jpg", imageRootDir, userImageDir, imageId, user.getId()))
                        .exists()
        );
    }

    @Then("a thumbnail of the image is created for me")
    public void a_thumbnail_of_the_image_is_created_for_me() {
        userImageRepository.findImagesByUserId(imageId);
        Assertions.assertTrue(
                new File(String.format("./%s/%s/%d_%d_thumbnail_test_image.jpg", imageRootDir, userImageDir, imageId, user.getId()))
                        .exists()
        );
    }
}
