/**
 * createInventory.test.js
 *
 * Test create inventory vue component and functions
 */
import VueRouter from 'vue-router';
import VueLogger from 'vuejs-logger';
import logger from "../../src/logger";
import Buefy from "buefy";
import CreateInventoryComponent from "../../src/components/businesses/inventory/CreateInventoryComponent";
import {mount, createLocalVue} from "@vue/test-utils";
import inventoryHelper from "../../src/components/businesses/inventory/inventoryHelper";
import api from "../../src/api/api";

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


let wrapper = mount(CreateInventoryComponent, {
    localVue,
    router,

    stubs: ['router-link'],
    data() {
        return {
            name: null,
            productId: 1,
            quantity: 1,
            pricePerItem: null,
            totalPrice: null,
            manufactured: null,
            sellBy: null,
            bestBefore: null,
            expires: new Date(),
            businessId: 1,
            errors: [],

            //What to show in the select box
            catalogItemStrings: [],
            //Map string to ID
            catalogItems: {}
        }
    },
});

let createButton = wrapper.findAll('button').at(2);

/**
 * Test to check that error is thrown when required inventory fields are missing
 */
test("Test error is thrown when required fields are missing", async () => {
    await createButton.trigger("click");
    expect(wrapper.vm.$data.errors.length).toBeGreaterThan(0);
    expect(wrapper.vm.$data.errors).toContain("Missing required fields!");
});

/**
 * Test to check that error is thrown expired date is invalid
 */
test("Test that error is thrown when expired date is invalid", async () => {
    //Mock this function
    inventoryHelper.convertStringsToIds = () => { return 1; }

    wrapper.vm.$data.quantity = 5;
    wrapper.vm.$data.expires = new Date(new Date().setFullYear(2020));
    await createButton.trigger("click");
    expect(wrapper.vm.$data.errors.length).toBeGreaterThan(0);
    expect(wrapper.vm.$data.errors).toContain("Expire date must be in the future");
});

/**
 * Test for api call is made when valid data is submitted
 */
test("Test for api call is made when valid data is submitted", async () => {
    inventoryHelper.convertStringsToIds = () => { return 1; }
    api.inventory.addToInventory = jest.fn()
                                        .mockImplementation(async () => { return 1; });

    const today = new Date()

    wrapper.vm.$data.quantity = 5;
    wrapper.vm.$data.expires = new Date(today.getFullYear() , today.getMonth(), today.getDate() + 1);
    await createButton.trigger("click");
    expect(wrapper.vm.$data.errors.length).toBe(0);
    expect(api.inventory.addToInventory).toHaveBeenCalled();
});

/**
 * Test returning correct date
 */
test("Test getting correct date from component", async () => {
    const today = new Date();
    const newDate = wrapper.vm.returnCorrectDate(today);
    expect(newDate.getTime()).toBeGreaterThan(today.getTime());
});
