/**
 * createEditCardComponent.test.js
 *
 * Test createEditCardComponent vue component and functions
 */
import VueRouter from 'vue-router';
import VueLogger from 'vuejs-logger';
import logger from "../../../src/logger";
import Buefy from "buefy";
import {mount, createLocalVue} from "@vue/test-utils";
import api from "../../../src/api/api";
import CreateEditCardComponent from "../../../src/components/marketplace/CreateEditCardComponent";
import validateCardDetails from "../../../src/components/marketplace/validateCardDetails";

const localVue = createLocalVue();
localVue.use(VueRouter)
localVue.use(Buefy);

localVue.use(VueLogger, logger.options);
logger.inject(localVue.$log);

const router = new VueRouter();
localStorage.setItem("id", "10");
let mockKeywords = {data: [{name: '1'}, {name: '2'}]}
api.keywords.getKeywords = jest.fn().mockImplementation(async () => {return mockKeywords;});
validateCardDetails.validateCardDetails = jest.fn().mockImplementation(() => {return []});

let editValues = {
    id: 1,
    title: 'Edit title',
    description: 'Edit description',
    keywords: [{name: '1'}, {name: '2'}]
};

let testPayload = {
    creatorId: 10,
    section: "ForSale",
    title: 'Edit title',
    description: 'Edit description',
    keywords: ['1', '2'],
};

let wrapper = mount(CreateEditCardComponent, {
    localVue,
    router,
    stubs: ['router-link'],
    data() {
        return {
            creatorId: 1,
            title: 'Title',
            description: 'Description',
            keywords: ['1', '2']
        }
    },
    propsData: {
        section: "ForSale",
        editing: false,
        editValues: editValues,
    },
});

test('Values are initialised on created', () => {
   expect(wrapper.vm.$data.creatorId).toBe("10");
   expect(wrapper.vm.$data.cardHeader).toBe("Create For Sale Card");
   expect(api.keywords.getKeywords).toBeCalled();
   expect(wrapper.vm.$data.keywordsList).toEqual(mockKeywords.data.map(x => x.name))
});

test('Values are set when changed to edit', () => {
   wrapper.vm.keywords = [];
   wrapper.vm.setEditDetails();
   expect(wrapper.vm.title).toEqual(editValues.title);
   expect(wrapper.vm.description).toEqual(editValues.description);
   expect(wrapper.vm.keywords).toEqual(editValues.keywords.map(x => x.name));
});

test('Card is created (API called) when data is valid', async () => {
    api.cards.createNewCard = jest.fn().mockImplementation(async () => {});
    wrapper.vm.createCard();
    await wrapper.vm.$nextTick();
    expect(api.cards.createNewCard).toBeCalledWith(testPayload);
    expect(wrapper.emitted().refreshTable).toBeTruthy();
    expect(wrapper.emitted().close).toBeTruthy();
});

test('Card is edited (API called) on valid data', async () => {
    api.cards.updateCard = jest.fn().mockImplementation(async () => {});
    wrapper.vm.editCard();
    await wrapper.vm.$nextTick();
    expect(api.cards.updateCard).toBeCalledWith(editValues.id, testPayload);
    expect(wrapper.emitted().refreshTable).toBeTruthy();
    expect(wrapper.emitted().close).toBeTruthy();
});
