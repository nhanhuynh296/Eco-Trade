/**
 * userSearchComponent.test.js
 *
 * Test user search vue component and functions
 */
import VueRouter from 'vue-router';
import Buefy from "buefy";
import {createLocalVue, mount} from "@vue/test-utils";
import UserSearchComponent from "../../../../src/components/users/search/UserSearchComponent";

const localVue = createLocalVue();
localVue.use(VueRouter)
localVue.use(Buefy);

const router = new VueRouter();
let wrapper = mount(UserSearchComponent, {
    localVue,
    router,
    stubs: ['router-link'],
    data() {
        return {
            query: "Some Query",
        }
    },
    propsData: {},
});

const searchButton = wrapper.findAll("button").at(0);

test("Searching for nothing emits query", async () => {
    wrapper.vm.$data.query = "";
    searchButton.trigger('click');

    await wrapper.vm.$nextTick();

    expect(wrapper.emitted().search).toBeTruthy();
    expect(wrapper.emitted().search[0][0]).toBe('');
});

test("Searching for something emits query", async () => {
    searchButton.trigger('click');

    await wrapper.vm.$nextTick();

    expect(wrapper.emitted().search).toBeTruthy();
    expect(wrapper.emitted().search[0][0]).toBe(wrapper.vm.$data.query);
});
