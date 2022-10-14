/**
 * expandableTextComponent.test.js
 *
 * Test expanding text component
 */
import VueRouter from 'vue-router';
import Buefy from "buefy";
import ExpandableTextComponent from "../../../src/components/home/ExpandableTextComponent";
import {mount, createLocalVue} from "@vue/test-utils";

const localVue = createLocalVue();
localVue.use(VueRouter)
localVue.use(Buefy);

let wrapper = mount(ExpandableTextComponent, {
    localVue,

    propsData: {
        text: "Hello World, this needs to be more than 60 characters to render the button properly",
    },

    data() {
        return {
            showMore: false
        }
    },
});

let showMoreButton = wrapper.findAll('button').at(0);

/**
 * Test to check that getting a one word substring works correctly
 */
test("Test getting word substring", () => {
    const result = wrapper.vm.getSubstring("Hello World", 0, 1);
    expect(result).toEqual("Hello");
});

/**
 * Test to check that getting full match substring works correctly
 */
test("Test getting full word substring", () => {
    const result = wrapper.vm.getSubstring("Hello World", 0);
    expect(result).toEqual("Hello World");
});

/**
 * Test to check that getting a one word substring with offset works correctly
 */
test("Test getting a one word substring with offset", () => {
    const result = wrapper.vm.getSubstring("Hello World", 1);
    expect(result).toEqual("World");
});

/**
 * Test show more button works as intended
 */
test("Test show more button", async () => {
    await showMoreButton.trigger("click");
    expect(wrapper.vm.$data.showMore).toBeTruthy();
    await showMoreButton.trigger("click");
    expect(wrapper.vm.$data.showMore).toBeFalsy();
});
