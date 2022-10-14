/**
 * marketSectionComponent.test.js
 *
 * Test market sections
 */

import VueRouter from 'vue-router';
import VueLogger from 'vuejs-logger';
import logger from "../../../src/logger";
import Buefy from "buefy";
import MarketSectionComponent from "../../../src/components/marketplace/MarketSectionComponent";
import {createLocalVue, mount} from "@vue/test-utils";
import countryList from "../../../src/api/photon/countries";

const localVue = createLocalVue();
localVue.use(VueRouter)
localVue.use(Buefy);

//Make logger work
localVue.use(VueLogger, logger.options);
logger.inject(localVue.$log);

const router = new VueRouter();

/**
 * Stop created() running on a component
 */
const mergeCreatedStrategy = localVue.config.optionMergeStrategies.created;
localVue.config.optionMergeStrategies.created = (parent) => {
    return mergeCreatedStrategy(parent);
};


let wrapper = mount(MarketSectionComponent, {
    localVue,
    router,
    stubs: ['router-link'],
    propsData: {
        "section": "For Sale"
    },

    data() {
        return {
            current: 1,
            perPage: 8,
            selectedFilterOptions: [],
            sortByOptions: [
                {name: 'Newest posts', paramName: 'DATE_DESC'},
                {name: 'Oldest posts', paramName: 'DATE_ASC'},
                {name: 'Title (A-Z)', paramName: 'TITLE_AZ'},
                {name: 'Title (Z-A)', paramName: 'TITLE_ZA'},
            ],
            sortBy: 'DATE_DESC',
            sortByName: 'Newest posts',
            cards: [],
            paginationInfo: {
                totalPages: 0,
                totalElements: 0
            },
            countryList,
            country: "",
            region: "",
            city: "",
            keywords: ['ABC'],
            keywordsList: ['Car'],
            isMatchAll: true,
            queryResults: [],
        }
    },
});

/**
 * Test to check that we are or aren't an admin
 */
test("Test getting admin status", async () => {
    localStorage.setItem("role", "ROLE_ADMIN");
    expect(wrapper.vm.isCardOwnerOrAdmin(null)).toBeTruthy();

    localStorage.setItem("role", "ROLE_DEFAULT_ADMIN");
    expect(wrapper.vm.isCardOwnerOrAdmin(null)).toBeTruthy();
});

/**
 * Test to check that we are or aren't the card owner
 */
test("Test getting card permission status", async () => {
    localStorage.setItem("role", "ROLE_USER");
    expect(wrapper.vm.isCardOwnerOrAdmin(null)).toBeFalsy();

    localStorage.setItem("id", "10");
    expect(wrapper.vm.isCardOwnerOrAdmin(10)).toBeTruthy();

    localStorage.setItem("id", "9");
    expect(wrapper.vm.isCardOwnerOrAdmin(10)).toBeFalsy();
});

