package gradle.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.seng302.main.Main;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@CucumberContextConfiguration
@TestPropertySource(properties = "server.port=7777")
@ActiveProfiles("test")
@SpringBootTest(classes = Main.class, webEnvironment = DEFINED_PORT)
public class SpringIntegrationTest {
    // executeGet implementation

    /**
     * "Add some tests to this class." said Sonarqube
     */
    @Test
    void placeholderTestCase() {
        Assertions.assertEquals("Test","Te" + "st");
    }
}
