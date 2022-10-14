package org.seng302.main.serviceTests;

import com.sipios.springsearch.SearchCriteria;
import com.sipios.springsearch.SpecificationImpl;
import com.sipios.springsearch.anotation.SearchSpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.main.dto.response.BusinessResponse;
import org.seng302.main.dto.response.PaginationInfo;
import org.seng302.main.models.Address;
import org.seng302.main.models.Business;
import org.seng302.main.repository.BusinessRepository;
import org.seng302.main.services.BusinessService;
import org.seng302.main.services.BusinessTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Annotation;
import java.time.LocalDate;

/**
 * Test business services
 */
@ContextConfiguration
@SpringBootTest
class BusinessServiceTests {

    @Autowired
    private BusinessService businessService;

    @Autowired
    private BusinessRepository businessRepository;

    public Business testBusiness;

    /**
     * Initialize tests
     */
    @BeforeEach
    public void init() {
        Address address = new Address("1", "Nowhere", "Varrock", "Tirannwn", "Gilenor", "800");
        Business testBusiness = new Business(1L, "Some Business", "Description", address, BusinessTypeService.ACCOMMODATION.toString(), LocalDate.now());
        this.testBusiness = businessRepository.save(testBusiness);

        Address address2 = new Address("5", "Somewhere", "Over", "The", "Rainbow", "800");
        Business testBusiness2 = new Business(1L, "Grand Exchange", "Desc", address2, BusinessTypeService.RETAIL.toString(), LocalDate.now());
        businessRepository.save(testBusiness2);
    }

    /**
     * Test getting paginated business info for all businesses
     */
    @Test
    void testPaginatedBusinessSomeBusiness() {
        PaginationInfo<BusinessResponse> paginationInfo = businessService.businessSearch(null, 0, 10, "NAME_ASC");
        Assertions.assertTrue(paginationInfo.getPaginationElements().size() > 0);
    }

    /**
     * Test getting paginated business info for all businesses total elements match
     */
    @Test
    void testPaginatedBusinessAllBusinessesMatchTotalElements() {
        PaginationInfo<BusinessResponse> paginationInfo = businessService.businessSearch(null, 0, 100, "NAME_DESC");
        Assertions.assertTrue(paginationInfo.getPaginationElements().size() > 0);
    }

    /**
     * Test getting paginated business info for page with no results
     */
    @Test
    void testPaginatedBusinessNoBusinesses() {
        PaginationInfo<BusinessResponse> paginationInfo = businessService.businessSearch(null, 1000, 1000, "DATE_ASC");
        Assertions.assertEquals(0, paginationInfo.getPaginationElements().size());
    }

    /**
     * Test getting paginated page count
     */
    @Test
    void testPaginatedInfoTotalPagesCount() {
        PaginationInfo<BusinessResponse> paginationInfo = businessService.businessSearch(null, 0, 1, "DATE_DESC");
        Assertions.assertTrue(paginationInfo.getTotalPages() > 0);
    }

    /**
     * Test getting paginated business info for second page of businesses
     */
    @Test
    void testPaginatedBusinessSecondPage() {
        PaginationInfo<BusinessResponse> paginationInfo = businessService.businessSearch(null, 1, 1, "TYPE_ASC");
        Assertions.assertEquals(1, paginationInfo.getPaginationElements().size());
    }

    /**
     * Test searching for a businesses for a given query
     */
    @Test
    void testBusinessSearch() {
        SearchCriteria searchCriteria = new SearchCriteria("name", ":", "", "Some Business", "");

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

        Specification<Business> specs = new SpecificationImpl<>(searchCriteria, annotation);
        PaginationInfo<BusinessResponse> results = businessService.businessSearch(specs, 0, 10, "TYPE_DESC");

        Assertions.assertNotEquals(0, results.getTotalElements());
        Assertions.assertEquals("Some Business", results.getPaginationElements().get(0).getName());
    }

    @Test
    @Transactional
    void testGettingBusinessCountryForCurrency() {
        Assertions.assertEquals(this.testBusiness.getAddress().getCountry(), businessService.getBusinessCountry(testBusiness.getId()));
    }

}
