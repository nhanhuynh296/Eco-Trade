/**
 * card.test.js
 *
 * Test card vue component and functions
 */
import VueRouter from 'vue-router';
import VueLogger from 'vuejs-logger';
import logger from "../../src/logger";
import Buefy from "buefy";
import {mount, createLocalVue} from "@vue/test-utils";
import api from "../../src/api/api";
import CardComponent from "../../src/components/marketplace/CardComponent";

const localVue = createLocalVue();
localVue.use(VueRouter)
localVue.use(Buefy);

//Make logger work
localVue.use(VueLogger, logger.options);
logger.inject(localVue.$log);

const router = new VueRouter();

let wrapper = mount(CardComponent, {
    localVue,
    router,
    stubs: ['router-link'],
    propsData: {
        'cardDetails': {
            'id': 1,
            'creator': {
                'id': 1
            },
            'title': 'card',
            'description': 'card',
            'displayPeriodEnd': new Date()
        },
        'modifiable': true
    },
    data() {
        return {
            creatorId: 1,
            cardId: this.cardDetails.id,
        }
    }
});

const deleteButton = wrapper.find('#delete-btn');
const renewButton = wrapper.find('#renew-btn');

/**
 * Test the card deletion button calls the delete function
 */
test("Test to card deletion button calls the delete function", async () => {
    wrapper.vm.$parent.init = () => { return 1; }
    api.cards.deleteCard = jest.fn()
        .mockImplementation(async () => { return 1; });

    await deleteButton.trigger("click");
    expect(api.cards.deleteCard).toHaveBeenCalled();
});

/**
 * Test the card renewal button calls the renew function
 */
test("Test to card renewal button calls the renew function", async () => {
    wrapper.vm.$parent.init = () => { return 1; }
    api.cards.renewCard = jest.fn()
        .mockImplementation(async () => { return 1; });

    await renewButton.trigger("click");
    expect(api.cards.renewCard).toHaveBeenCalled();
});
