package gradle.cucumber.images;

import gradle.cucumber.SpringIntegrationTest;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.Cookie;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.seng302.main.models.*;
import org.seng302.main.repository.BusinessRepository;
import org.seng302.main.repository.ProductImageRepository;
import org.seng302.main.repository.ProductRepository;
import org.seng302.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;
import java.time.LocalDate;

public class ImageStepDefinitions extends SpringIntegrationTest {

    @Value("${server.port}")
    private int PORT;

    @Value("${wasteless.images.rootdir}")
    private String imageRootDir;

    @Value("${wasteless.images.productdir}")
    private String productImageDir;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BusinessRepository businessRepository;

    @Autowired
    ProductImageRepository productImageRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    // Address for business
    private Address validAddress = new Address("3/24", "Ilam Road", "Christchurch",
            "Canterbury", "New Zealand", "90210");

    private final LocalDate localDate = LocalDate.now();

    User user;
    Business business;
    Product product;

    RequestSpecification request;
    Response response;
    String userSession;

    Long imageId;

    ProductImage previousPrimaryImage;
    ProductImage newPrimaryImage;

    @Before
    public void init() {
        RestAssured.port = PORT;
        userRepository.deleteAll();

        imageId = null;

        user = new User("Totally", "Real", "Name", "TRN", "Hello There",
                "abcdefgh@uclive.ac.nz", LocalDate.now(), "",
                new Address("", "", "", "", "", ""),
                LocalDate.now(), "ROLE_USER", passwordEncoder.encode("password123"), null, null);
    }

    @Given("I am logged in as a user, with a business that contains a product called {string}")
    public void iAmLoggedInAsAUserWithABusinessThatContainsAProductCalled(String productName) {
        user = userRepository.save(user);

        // Login
        request = RestAssured.given().log().uri();
        request.header("Content-Type", "application/json");
        response = request.body("{ \"email\":\"" + user.getEmail() + "\", \"password\":\"" + "password123" + "\"}")
                .post("/login");
        userSession = response.getHeader("Set-Cookie").substring(11);

        //Create business
        business = businessRepository.save(
                new Business(user.getId(), "Varrock Grand Exchange", "Description1", validAddress, "Accomodation", localDate)
        );

        //Create product
        product = productRepository.save(
                new Product(business.getId(), business, productName, "Description", "TestCo.", 12d, LocalDate.now())
        );

        Assertions.assertNotNull(businessRepository.findBusinessById(business.getId()));
        Assertions.assertNotNull(productRepository.findProductById(product.getId()));
    }

    @When("I upload an image for the product")
    public void iUploadAnImageForTheProduct() {
        Response response = request
                .header(new Header("content-type", "multipart/form-data"))
                .multiPart("file", new File("./src/test/resources/media/test_image.jpg"))
                .cookie(new Cookie.Builder("JSESSIONID", userSession).build())
                .post(String.format("/businesses/%d/products/%d/images", business.getId(), product.getId()))
                .andReturn();

        Assert.assertEquals(200, response.getStatusCode());
    }

    @Then("The image is saved and added to the product")
    public void theImageIsSavedAndAddedToTheProduct() {
        imageId = productImageRepository.findImagesByProductId(product.getId()).get(0).getId();
        Assertions.assertTrue(
                new File(
                        String.format("./%s/%s/%d_%d_%d_test_image.jpg",
                                imageRootDir, productImageDir, imageId, business.getId(), product.getId()))
                        .exists()
        );
    }

    @And("That product has no images associated with it")
    public void thatProductHasNoImagesAssociatedWithIt() {
        Assertions.assertTrue(productImageRepository.findImagesByProductId(product.getId()).isEmpty());
    }

    @And("The products primary image id is set to the images id")
    public void theProductsPrimaryImageIdIsSetToTheImagesId() {
        Long primaryImageId = productRepository.findProductById(product.getId()).getPrimaryImageId();
        Assertions.assertEquals(imageId, primaryImageId);
    }


    // U16 - AC4 //

    @And("That product has images associated with it")
    public void thatProductHasImagesAssociatedWithIt() {
        request
                .header(new Header("content-type", "multipart/form-data"))
                .multiPart("file", new File("./src/test/resources/media/test_image.jpg"))
                .cookie(new Cookie.Builder("JSESSIONID", userSession).build())
                .post(String.format("/businesses/%d/products/%d/images", business.getId(), product.getId()))
                .andReturn();


        imageId = productImageRepository.findImagesByProductId(product.getId()).get(0).getId();

        Assertions.assertTrue(
                new File(
                        String.format("./%s/%s/%d_%d_%d_test_image.jpg",
                                imageRootDir, productImageDir, imageId, business.getId(), product.getId()))
                        .exists()
        );
    }

    @When("I delete an image from the product")
    public void iDeleteAnImageFromTheProduct() {
        Response response = request
                .cookie(new Cookie.Builder("JSESSIONID", userSession).build())
                .delete(String.format("/businesses/%d/products/%d/images/%d", business.getId(), product.getId(), imageId))
                .andReturn();

        Assertions.assertEquals(200, response.getStatusCode());
    }

    @Then("The image is deleted successfully")
    public void theImageIsDeletedSuccessfully() {
        Assertions.assertFalse(
                new File(
                        String.format("./%s/%s/%d_%d_%d_test_image.jpg",
                                imageRootDir, productImageDir, imageId, business.getId(), product.getId()))
                        .exists()
        );
        Assertions.assertNull(productImageRepository.findImageByImageId(imageId));
    }

    @And("That product has a primary and non primary image associated with it")
    public void thatProductHasAPrimaryAndNonPrimaryImageAssociatedWithIt() {
        //Upload two images
        Response response = request
                .header(new Header("content-type", "multipart/form-data"))
                .multiPart("file", new File("./src/test/resources/media/test_image.jpg"))
                .cookie(new Cookie.Builder("JSESSIONID", userSession).build())
                .post(String.format("/businesses/%d/products/%d/images", business.getId(), product.getId()))
                .andReturn();

        Assert.assertEquals(200, response.getStatusCode());

        response = request
                .header(new Header("content-type", "multipart/form-data"))
                .multiPart("file", new File("./src/test/resources/media/test_image.jpg"))
                .cookie(new Cookie.Builder("JSESSIONID", userSession).build())
                .post(String.format("/businesses/%d/products/%d/images", business.getId(), product.getId()))
                .andReturn();

        Assert.assertEquals(200, response.getStatusCode());

        product = productRepository.findProductById(product.getId());

        //Assert we have two images
        Assertions.assertEquals(2, productImageRepository.findImagesByProductId(product.getId()).size());

        //Assert one of them is the primary one
        boolean hasPrimaryImage = false;
        for (ProductImage image : productImageRepository.findImagesByProductId(product.getId())) {
            if (product.getPrimaryImageId().equals(image.getId())) {
                hasPrimaryImage = true;
                previousPrimaryImage = image;
                break;
            }
        }

        Assertions.assertTrue(hasPrimaryImage);
    }

    @When("I make the non primary a primary image")
    public void iMakeTheNonPrimaryAPrimaryImage() {
        //Find image that isn't primary
        for (ProductImage image : productImageRepository.findImagesByProductId(product.getId())) {
            if (!product.getPrimaryImageId().equals(image.getId())) {
                Response response = request
                        .cookie(new Cookie.Builder("JSESSIONID", userSession).build())
                        .put(String.format("/businesses/%d/products/%d/images/%d/makeprimary", business.getId(), product.getId(), image.getId()))
                        .andReturn();
                Assertions.assertEquals(200, response.getStatusCode());

                newPrimaryImage = image;
                break;
            }
        }
    }

    @Then("The image is set as the primary image and the previous one is unset")
    public void theImageIsSetAsThePrimaryImageAndThePreviousOneIsUnset() {
        Assertions.assertNotEquals(previousPrimaryImage, newPrimaryImage);
    }
}
