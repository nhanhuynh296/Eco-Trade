package org.seng302.main.serviceTests;

import com.sipios.springsearch.SearchCriteria;
import com.sipios.springsearch.SpecificationImpl;
import com.sipios.springsearch.anotation.SearchSpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.dto.response.PaginationInfo;
import org.seng302.main.dto.response.UserResponse;
import org.seng302.main.models.Address;
import org.seng302.main.models.User;
import org.seng302.main.repository.UserRepository;
import org.seng302.main.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.transaction.Transactional;
import java.lang.annotation.Annotation;
import java.time.LocalDate;

/**
 * Test user services
 */
@SpringBootTest
class UserServiceTests {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    public void init() {
        userRepository.deleteAll();

        Address nzAddress = new Address("3/24", "Ilam Road", "Christchurch",
                "Canterbury", "New Zealand", "90210");

        Address usAddress = new Address("327", "Fairground Street", "Mount Sterling",
                "Illinois", "United States", "62353");

        User testUser = new User("John", "", "Johnson", "Johnny", "empty Bio", "email@here.com.co",
                LocalDate.of(1970, 1, 1), "0200 9020", nzAddress, LocalDate.now(), "ROLE_USER",
                passwordEncoder.encode("pass"), null, null);

        User testUser2 = new User("albert", "", "Einstein", "Einy", "empty Bio", "email@here.com.co",
                LocalDate.of(1970, 1, 1), "0200 9020", usAddress, LocalDate.now(), "ROLE_USER",
                passwordEncoder.encode("pass"), null, null);

        userRepository.save(testUser);
        userRepository.save(testUser2);
    }

    /**
     * Test getting paginated user info for all users
     */
    @Test
    @Transactional
    void testPaginatedUserSomeUsers() {
        PaginationInfo<UserResponse> paginationInfo = userService.userSearch(null, null, 0, 10, "FIRST_DESC");
        Assertions.assertTrue(paginationInfo.getPaginationElements().size() > 0);
    }

    /**
     * Test getting paginated user info for page with no results
     */
    @Test
    void testPaginatedUserNoUsers() {
        PaginationInfo<UserResponse> paginationInfo = userService.userSearch(null, null,1000, 1000, "FIRST_DESC");
        Assertions.assertEquals(0, paginationInfo.getPaginationElements().size());
    }

    /**
     * Test getting paginated page count
     */
    @Test
    @Transactional
    void testPaginatedInfoTotalPagesCount() {
        PaginationInfo<UserResponse> paginationInfo = userService.userSearch(null, null, 0, 1, "FIRST_DESC");
        Assertions.assertTrue(paginationInfo.getTotalPages() > 0);
    }

    /**
     * Test getting paginated user info for second page of users
     */
    @Test
    @Transactional
    void testPaginatedUserSecondPage() {
        PaginationInfo<UserResponse> paginationInfo = userService.userSearch(null, null,1, 1, "FIRST_DESC");
        Assertions.assertEquals(1, paginationInfo.getPaginationElements().size());
    }

    /**
     * Test searching for a users for a given query
     */
    @Test
    @Transactional
    void testUserSearch() {
        SearchCriteria searchCriteria = new SearchCriteria("firstName", ":", "", "John", "");

        SearchSpec annotation = new SearchSpec() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return SearchSpec.class;
            }

            @Override
            public boolean caseSensitiveFlag() {
                return false;
            }

            @Override
            public String searchParam() {
                return null;
            }
        };

        Specification<User> specs = new SpecificationImpl<>(searchCriteria, annotation);
        PaginationInfo<UserResponse> results = userService.userSearch(specs, null, 0, 10, "FIRST_DESC");

        Assertions.assertNotEquals(0, results.getTotalElements());
        Assertions.assertEquals("John", results.getPaginationElements().get(0).getFirstName());
    }

    @Test
    @Transactional
    void testUserSearchSortIgnoresCase() {
        PaginationInfo<UserResponse> paginationInfo = userService.userSearch(null, null,0, 10, "FIRST_ASC");
        Assertions.assertEquals( "albert", paginationInfo.getPaginationElements().get(0).getFirstName());
    }

    @Test
    @Transactional
    void testLastNameSort() {
        PaginationInfo<UserResponse> paginationInfo = userService.userSearch(null, null,0, 10, "LAST_ASC");
        Assertions.assertEquals( "Einstein", paginationInfo.getPaginationElements().get(0).getLastName());
    }

    @Test
    @Transactional
    void testNickNameSort() {
        PaginationInfo<UserResponse> paginationInfo = userService.userSearch(null, null,0, 10, "NICK_DESC");
        Assertions.assertEquals( "Johnny", paginationInfo.getPaginationElements().get(0).getNickname());
    }

    @Test
    @Transactional
    void testCountrySort() {
        PaginationInfo<UserResponse> paginationInfo = userService.userSearch(null, null,0, 10, "COUNT_DESC");
        Assertions.assertEquals( "United States", paginationInfo.getPaginationElements().get(0).getHomeAddress().getCountry());
    }

    @Test
    @Transactional
    void testCitySort() {
        PaginationInfo<UserResponse> paginationInfo = userService.userSearch(null, null,0, 10, "CITY_ASC");
        Assertions.assertEquals( "Christchurch", paginationInfo.getPaginationElements().get(0).getHomeAddress().getCity());
    }

}
