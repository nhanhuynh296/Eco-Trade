/**
 * cardPopUpComponent.test.js
 *
 * Test cardPopUpComponent vue component and functions
 */
import VueRouter from 'vue-router';
import VueLogger from 'vuejs-logger';
import logger from "../../../src/logger";
import Buefy from "buefy";
import {mount, createLocalVue} from "@vue/test-utils";
import api from "../../../src/api/api";
import CardPopUpComponent from "../../../src/components/marketplace/CardPopUpComponent";


const localVue = createLocalVue();
localVue.use(VueRouter)
localVue.use(Buefy);

localVue.use(VueLogger, logger.options);
logger.inject(localVue.$log);

const router = new VueRouter();

let mockCardDetails = {
    "id":7,
    "creator":{"id":5,"firstName":"Brodan","lastName":"Smith","middleName":"","nickname":"Smithy","bio":"N/A","email":"Temporary@email.com","dateOfBirth":"2000-06-27","phoneNumber":"+64 22 350 5775","homeAddress":{"id":6,"streetNumber":"5","streetName":"James Street","city":"Kaiapoi","region":"Canterbury","country":"New Zealand","postcode":"8011","currency":null,"validAddress":true},"created":"2021-03-04","role":"ROLE_USER","businessAdministrated":[]},
    "section":"ForSale",
    "created":"2021-07-30T00:00:00",
    "displayPeriodEnd":"2022-08-27T00:00:00",
    "title":"Subaru",
    "description":"Card for sale 7",
    "keywords":[]
};

localStorage.setItem("id", mockCardDetails.creator.id);

api.cards.getCard = jest.fn().mockImplementation(async () => {return {data:mockCardDetails}});
api.cards.deleteCard = jest.fn().mockImplementation(async() => {});
api.cards.renewCard = jest.fn().mockImplementation(async() => {});

let wrapper = mount(CardPopUpComponent, {
    localVue,
    router,
    stubs: ['router-link'],
    propsData: {
        cardId: mockCardDetails.id
    },
});

test('Card details are retrieved and stored', () => {
   expect(api.cards.getCard).toBeCalledWith(mockCardDetails.id);
   expect(wrapper.vm.details).toEqual(mockCardDetails);
   expect(wrapper.vm.ready).toBeTruthy();
   expect(wrapper.vm.show).toBeTruthy();
   expect(wrapper.vm.cardCreatorId).toBe(mockCardDetails.creator.id);
});

test('Card is deleted (API called)', async () => {
    wrapper.vm.$parent.init = jest.fn();
    wrapper.vm.$parent.notifySuccess = jest.fn();
    wrapper.vm.$buefy.toast.open = jest.fn();
    delete window.location;
    window.location = {reload: jest.fn()};
    await wrapper.vm.deleteCard();

    expect(api.cards.deleteCard).toBeCalledWith(mockCardDetails.id);
});


test('Card is renewed (API called)', async () => {
    wrapper.vm.$parent.init = jest.fn();
    wrapper.vm.$parent.notifySuccess = jest.fn();
    wrapper.vm.$buefy.toast.open = jest.fn();
    delete window.location;
    window.location = {reload: jest.fn()};
    await wrapper.vm.renewCard();

    expect(api.cards.renewCard).toBeCalledWith(mockCardDetails.id);
});
