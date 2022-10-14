/**
 * listingsSearchResultComponent.test.js
 *
 * Test card vue component and functions
 */
import VueRouter from 'vue-router';
import VueLogger from 'vuejs-logger';
import logger from "../../../src/logger";
import Buefy from "buefy";
import {mount, createLocalVue} from "@vue/test-utils";
import ListingsSearchResultComponent from "../../../src/components/businesses/listings/listingsSearch/ListingsSearchResultComponent";
import api from "../../../src/api/api";

const localVue = createLocalVue();
localVue.use(VueRouter)
localVue.use(Buefy);

//Make logger work
localVue.use(VueLogger, logger.options);

/**
 * Overwrite VueRouter push with one that ignores duplicated navigation
 */
const originalPush = VueRouter.prototype.push;
VueRouter.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err);
};

const router = new VueRouter({
    routes: [
        {
            path: "",
            name: "listingsSearch"
        }
    ]
});

let wrapper = mount(ListingsSearchResultComponent, {
    localVue,
    router,
    stubs: ['router-link'],
    propsData: {

    },
    data() {
        return {
            // Sorting Options
            selectedFilterOptions: [],
            sortByOptions: [
                {name: 'Newest listings', paramName: 'DATE_DESC'},
                {name: 'Oldest listings', paramName: 'DATE_ASC'},
                {name: 'Title (A-Z)', paramName: 'TITLE_AZ'},
                {name: 'Title (Z-A)', paramName: 'TITLE_ZA'},
            ],
            sortBy: 'DATE_DESC',
            sortByName: 'Newest listing',
            queryResults: [],

            // Filtering Options
            country: "",
            region: "",
            city: "",
            businessesName: null,
            businessType: [],
            priceRange: [0, 0],
            closingDate: null,

            // Search
            searchQuery: "",
            fullSearch: "?search=",

            listingsResultData: [],

            page: 1,
            perPage: 10,
            paginationInfo: {
                totalPages: 0,
                totalElements: 0
            },
            lowerCount: 0,
            upperCount: 0,
        }
    }
});

const clearFiltersBtn = wrapper.find('#listings-search-clear-filter-btn');
const applyFiltersBtn = wrapper.find('#listings-search-apply-filter-btn');
const searchListingsBtn = wrapper.find('#listings-search-btn');
api.listing.searchListings = jest.fn().mockImplementation(async () => {
    return {
        data: {
            "content": [],
            "totalPages": 1,
            "totalElements": 0,
        }
    };
});

/**
 * Searching for everything and searching for one product
 */
test('Clicking search button results in modifying the fullSearch variable', async () => {
    await searchListingsBtn.trigger("click");
    expect(wrapper.vm.$data.fullSearch).toStrictEqual('?search=');
    expect(api.listing.searchListings).toHaveBeenCalled();

    wrapper.vm.$data.searchQuery = 'product';
    await searchListingsBtn.trigger("click");
    expect(wrapper.vm.$data.fullSearch).toStrictEqual('?search=inventoryItem.product.name:*product*');
    expect(api.listing.searchListings).toHaveBeenCalled();
});

/**
 * Clicking on apply filters button adds all the filters to the search
 */
test('Testing applying filters to the search', async () => {
    wrapper.vm.$data.businessesName = "Business";
    wrapper.vm.$data.businessType = ["Trade"];
    wrapper.vm.$data.closingDate = new Date(1629723600000); // 2021-08-24
    wrapper.vm.$data.priceRange = [200, 300];
    wrapper.vm.$data.country = "New Zealand";
    wrapper.vm.$data.region = "Auckland";
    wrapper.vm.$data.city = "Auckland";

    await applyFiltersBtn.trigger("click");

    expect(wrapper.vm.$data.fullSearch).toStrictEqual('?search=inventoryItem.product.name:*product* AND inventoryItem.product.business.name:*Business* AND ( inventoryItem.product.business.businessType:"Trade") ' +
        'AND closes:2021-08-24 AND (price:200 OR price>200) AND (price:300 OR price<300) AND inventoryItem.product.business.address.country:"New Zealand" AND inventoryItem.product.business.address.region:"Auckland" ' +
        'AND inventoryItem.product.business.address.city:"Auckland"');
    expect(api.listing.searchListings).toHaveBeenCalled();
});

/**
 * Clicking on clear button clears all the filters
 */
test('After clicking clear filters button all filters are reset', async() => {
    wrapper.vm.$data.businessesName = "Business";
    wrapper.vm.$data.businessType = ["Trade"];
    wrapper.vm.$data.closingDate = new Date();
    wrapper.vm.$data.priceRange = [200, 300];
    wrapper.vm.$data.country = "New Zealand";
    wrapper.vm.$data.region = "Auckland";
    wrapper.vm.$data.city = "Auckland";

    await clearFiltersBtn.trigger("click");

    expect(wrapper.vm.$data.businessesName).toBe(null);
    expect(wrapper.vm.$data.businessType).toStrictEqual([]);
    expect(wrapper.vm.$data.closingDate).toBe(null);
    expect(wrapper.vm.$data.priceRange).toStrictEqual([0, 0]);
    expect(wrapper.vm.$data.country).toStrictEqual("");
    expect(wrapper.vm.$data.region).toStrictEqual("");
    expect(wrapper.vm.$data.city).toStrictEqual("");
    expect(api.listing.searchListings).toHaveBeenCalled();
});

/**
 * Testing the getListFromParams method
 */
test('Testing the getListFromParams method with business and price range', () => {
    // Business
    let result = wrapper.vm.getListFromParams("Trade,Trade2", "business");
    expect(result).toStrictEqual(["Trade", "Trade2"]);

    // Price Range
    result = wrapper.vm.getListFromParams("2,50", "price");
    expect(result).toStrictEqual([2, 50]);
});

/**
 * Test date formatter function
 */
test('Test date formatter function', () => {
    let result = wrapper.vm.formatter(new Date('01-01-2020'));
    expect(new Date(result)).toStrictEqual(new Date('01-01-2020'));
});

/**
 * Testing method returnCorrectDate
 */
test('Testing function returnCorrectDate', () => {
    let result = wrapper.vm.returnCorrectDate(new Date('01-01-2020'));
    expect(result.toISOString().substring(0,10)).toStrictEqual("2020-01-01");
});

/**
 * Test sortListings method
 */
test('Test sortListings method to change the sortBy and sortByName variables', async () => {
    await wrapper.vm.sortListings(wrapper.vm.$data.sortByOptions[3]);

    expect(wrapper.vm.$data.sortBy).toStrictEqual('TITLE_ZA');
    expect(wrapper.vm.$data.sortByName).toStrictEqual('Title (Z-A)');
    expect(api.listing.searchListings).toHaveBeenCalled();
});
