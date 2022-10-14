import VueRouter from 'vue-router';
import VueLogger from 'vuejs-logger';
import logger from "../../../src/logger";
import Buefy from "buefy";
import {createLocalVue, mount} from "@vue/test-utils";
import CreateEditCardComponent from "../../../src/components/marketplace/CreateEditCardComponent";
import ValidateCardDetails from "../../../src/components/marketplace/validateCardDetails";
import api from "../../../src/api/api";

/**
 * validateCardDetails.test.js
 *
 * Test create card
 */

const localVue = createLocalVue();
localVue.use(VueRouter)
localVue.use(Buefy);

//Make logger work
localVue.use(VueLogger, logger.options);
logger.inject(localVue.$log);

const router = new VueRouter();

let wrapper = mount(CreateEditCardComponent, {
    localVue,
    router,

    stubs: ['router-link'],
    propsData: {
        "section": "ForSale"
    },

    data() {
        return {
            title: 'TestTitle',
            description: 'Hmmm',
            keywords: ['this', 'is', 'fun'],
            keywordsList: [],
            valid: false,
        }
    },
});

beforeAll(() => {
    wrapper.vm.$data.creatorId = "1";
})

test("Test card title is created using prop", () => {
    expect(wrapper.text()).toContain('Create For Sale Card');
});

test("Test creating a new card", async () => {
    api.cards.createNewCard = jest.fn().mockImplementation(async () => {
        return {
            data: {
                cardId: 1
            }
        };
    });

    wrapper.vm.createCard();
    let payload = {
        creatorId: parseInt(wrapper.vm.$data.creatorId),
        section: wrapper.vm.$props.section.replace(' ', ''),
        title: wrapper.vm.$data.title,
        description: wrapper.vm.$data.description,
        keywords: wrapper.vm.$data.keywords,
    }
    expect(api.cards.createNewCard)
        .toBeCalledWith(payload);
});

/**
 * Test error is returned on invalid section
 */
test("Test create card validation - section", () => {
    let section = 'INVALID';
    let title = 'Title';
    const expectedErrors = ['Please choose one of the available sections!'];

    expect(ValidateCardDetails.validateCardDetails(section, title, false)).toStrictEqual(expectedErrors);
});

/**
 * Test error is returned on invalid Title
 */
test("Test create card validation - title", () => {
    let section = 'ForSale';
    let title = '';
    const expectedErrors = ['Please provide a title!'];

    expect(ValidateCardDetails.validateCardDetails(section, title, false)).toStrictEqual(expectedErrors);
});

/**
 * Test error is not returned on invalid section and editing is true
 */
test("Test edit card validation - section", () => {
    let section = 'INVALID';
    let title = 'Title';
    const expectedErrors = [];

    expect(ValidateCardDetails.validateCardDetails(section, title, true)).toStrictEqual(expectedErrors);
});

/**
 * Test error is returned on invalid Title and editing is true
 */
test("Test edit card validation - title", () => {
    let section = 'ForSale';
    let title = '';
    const expectedErrors = ['Please provide a title!'];

    expect(ValidateCardDetails.validateCardDetails(section, title, true)).toStrictEqual(expectedErrors);
});
