/**
 * userCardsComponent.test.js
 *
 * Test user cards component
 */

import VueRouter from 'vue-router';
import VueLogger from 'vuejs-logger';
import logger from "../../../src/logger";
import Buefy from "buefy";
import {createLocalVue, mount} from "@vue/test-utils";
import UserCardsComponent from "../../../src/components/marketplace/UserCardsComponent";
import api from "../../../src/api/api";
import paginationInfo from "../../../src/components/paginationInfo";

const localVue = createLocalVue();
localVue.use(VueRouter)
localVue.use(Buefy);

//Make logger work
localVue.use(VueLogger, logger.options);
logger.inject(localVue.$log);

const router = new VueRouter();

let mockCards = {
    data: {
        "paginationElements":[
            {"id":1,"creator":{"id":1,"firstName":"Christian","lastName":"Askey","middleName":"Julian","nickname":"Rapskey","bio":"3rd year Computer Science student","email":"cja128@uclive.ac.nz","dateOfBirth":"2000-06-27","phoneNumber":"+64 22 350 5775","homeAddress":{"id":1,"streetNumber":"1","streetName":"There Street","city":"Thereland","region":"Therefield","country":"Therethere","postcode":"8022","validAddress":true},"created":"2021-03-04","role":"ROLE_USER","businessAdministrated":[{"id":1,"products":[{"id":1,"businessId":1,"business":1,"name":"Watties Baked Beans - 420g can","description":"Baked Beans as they should be.","manufacturer":"Heinz Wattie's Limited","recommendedRetailPrice":2.2,"primaryImageId":null,"created":"2020-07-14","images":[]},{"id":2,"businessId":1,"business":1,"name":"Indomie Instant Noodles","description":"Spicy BBQ noodles","manufacturer":"IndoFood","recommendedRetailPrice":3.5,"primaryImageId":null,"created":"2020-07-14","images":[]}],"primaryAdministratorId":1,"name":"Lumbridge General Store","description":"Sells an assortment of friendly neighbourhood war criminals","address":{"id":5,"streetNumber":"5","streetName":"James Street","city":"Kaiapoi","region":"Canterbury","country":"New Zealand","postcode":"8011","validAddress":true},"countryForCurrency":"New Zealand","businessType":"Retail Trade","created":"2021-02-04","administratorsNames":["Christian Askey"],"itemsBusinessCatalogue":[2,3]},{"id":2,"products":[],"primaryAdministratorId":1,"name":"Macs Donald","description":"Deals exclusively in Macaroni and Cheese","address":{"id":3,"streetNumber":"3","streetName":"Here Street","city":"Hereland","region":"Herefield","country":"Herehere","postcode":"8022","validAddress":true},"countryForCurrency":"Herehere","businessType":"Retail Trade","created":"2011-07-08","administratorsNames":["Christian Askey","Mary Mason"],"itemsBusinessCatalogue":[]}]},"section":"ForSale","created":"2021-07-30T00:00:00","displayPeriodEnd":"2022-08-27T00:00:00","title":"Subaru","description":"Card for sale 1","keywords":[{"id":1,"name":"Vehicle","created":"2021-08-15"},{"id":2,"name":"Car","created":"2021-08-15"},{"id":3,"name":"Auto","created":"2021-08-14"},{"id":4,"name":"Food","created":"2021-08-13"},{"id":5,"name":"Free","created":"2021-08-12"}]}],
        "totalPages":1,
        "totalElements":1
    }
};
api.cards.getUsersCards = jest.fn().mockImplementation(async () => {return mockCards;})
paginationInfo.getPageInfo = jest.fn().mockImplementation(() => {return [0, 1]});

let wrapper = mount(UserCardsComponent, {
    localVue,
    router,
    stubs: ['router-link'],
    propsData: {
        userId: 1,
        user: "User",
    },
    data() {
        return {
            current: 1,
        }
    }
});

test('Data is initialised correctly on create', () => {
    expect(wrapper.vm.cards).toEqual(mockCards.data.paginationElements);
    expect(wrapper.vm.paginationInfo).toEqual({totalPages: 1, totalElements: 1});
    expect(wrapper.vm.lowerCount).toBe(0);
    expect(wrapper.vm.upperCount).toBe(1);
});

test('Correct methods have been called on initialisation', () => {
    expect(api.cards.getUsersCards).toHaveBeenCalledWith(1, 1, 9);
    expect(paginationInfo.getPageInfo).toHaveBeenCalledWith(1, 9, 1);
})

test('isCardOwnerOrAdmin returns true when user is admin', () => {
    localStorage.setItem("role", "ROLE_ADMIN");
    expect(wrapper.vm.isCardOwnerOrAdmin(5)).toBeTruthy();

});

test('isCardOwnerOrAdmin returns false when user is not an admin', () => {
    localStorage.setItem("role", "Not an admin");
    localStorage.setItem("id", "1");
    expect(wrapper.vm.isCardOwnerOrAdmin(1)).toBeTruthy();
});

test('Backend is called when cards are sorted',  () => {
   wrapper.vm.sortCards({name: 'test name', paramName:'test param name'});
   expect(wrapper.vm.$data.sortByName).toEqual('test name');
   expect(wrapper.vm.sortBy).toEqual('test param name');
   expect(api.cards.getUsersCards).toHaveBeenCalledWith(1, 1, 9);
});
