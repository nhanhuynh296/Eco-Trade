package gradle.cucumber.users;

import gradle.cucumber.SpringIntegrationTest;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.seng302.main.models.Address;
import org.seng302.main.models.User;
import org.seng302.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CreateUserStepDefinitions extends SpringIntegrationTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    BCryptPasswordEncoder encoder;

    DateTimeFormatter formatter;
    User savedUser;
    Long userId;
    String userFirstName;
    String userNickname;
    String userMiddleName;
    String userLastName;
    LocalDate userCreated;
    String userBio;
    String userEmail;
    LocalDate userDOB;
    String userPhone;
    Address userAddress;
    String userRole;
    String userPassword;


    @Before
    public void setup() {
        userRepository.deleteAll();
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }


    @Given("I have not registered with the email {string} before")
    public void iHaveNotRegisteredWithTheEmailBefore(String email) {
        User user = userRepository.findUserByEmail(email);
        Assertions.assertNull(user);
    }

    @When("I register with name {string}, bio {string}, email {string}, dob {string}, phone {string}, address {string}, password {string}")
    public void iRegisterWithNameBioEmailDobPhoneAddressPassword(
            String name,
            String bio,
            String email,
            String dob,
            String phone,
            String address,
            String password) {

        String[] splitAddress = address.split(", ");
        Address homeAddress = stringAddressToEntity(splitAddress);
        String[] splitName = name.split(" ");
        userFirstName = splitName[0];
        userNickname = splitName[1];
        userMiddleName = splitName[2];
        userLastName = splitName[3];
        userCreated = LocalDate.now();
        userBio = bio;
        userEmail = email;
        userDOB = LocalDate.parse(dob, formatter);
        userPhone = phone;
        userAddress = homeAddress;
        userRole = "ROLE_USER";
        userPassword = encoder.encode(password);

        User newUser = new User(
                userFirstName,
                userMiddleName,
                userLastName,
                userNickname,
                userBio,
                userEmail,
                userDOB,
                userPhone,
                userAddress,
                userCreated,
                userRole,
                userPassword,
                null,
                null
        );
        savedUser = userRepository.save(newUser);
        userId = savedUser.getId();
        Assertions.assertNotNull(savedUser);
        Assertions.assertNotNull(userId);
    }


    @Then("The user is created with correct name, bio, email, dob, phone, address and password")
    public void theUserIsCreatedWithCorrectInformation() {
        Assertions.assertEquals(savedUser.getId(), userId);
        Assertions.assertEquals(savedUser.getFirstName(), userFirstName);
        Assertions.assertEquals(savedUser.getNickname(), userNickname);
        Assertions.assertEquals(savedUser.getMiddleName(), userMiddleName);
        Assertions.assertEquals(savedUser.getLastName(), userLastName);
        Assertions.assertEquals(savedUser.getCreated(), userCreated);
        Assertions.assertEquals(savedUser.getBio(), userBio);
        Assertions.assertEquals(savedUser.getEmail(), userEmail);
        Assertions.assertEquals(savedUser.getDateOfBirth(), userDOB);
        Assertions.assertEquals(savedUser.getPhoneNumber(), userPhone);
        Assertions.assertEquals(savedUser.getHomeAddress(), userAddress);
        Assertions.assertEquals(savedUser.getUserRole(), userRole);
        Assertions.assertEquals(savedUser.getPassword(), userPassword);
    }

    /**
     * Function that splits string address input into valid address entity
     * Address in feature file required to have exactly 5 comma separated value
     */
    public static Address stringAddressToEntity(String[] address) {
        // will need to change as it is inefficient, should allow for varying address lengths/values
        return new Address(address[0], address[1], address[2], address[3], address[4], "");
    }
}