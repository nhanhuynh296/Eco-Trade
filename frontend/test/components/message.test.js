/**
 * message.test.js
 *
 * Tests card message functions and component
 */
import VueRouter from 'vue-router';
import Buefy from "buefy";
import {mount, createLocalVue} from "@vue/test-utils";
import api from "../../src/api/api";
import SendMessageComponent from "../../src/components/marketplace/SendMessageComponent";

const localVue = createLocalVue();
localVue.use(VueRouter)
localVue.use(Buefy);

const router = new VueRouter();

let wrapper = mount(SendMessageComponent, {
    localVue,
    router,
    stubs: ['router-link'],
    propsData: {
        'cardId': 1,
        'recipientId': 1,
    },
    data() {
        return {
            sendMessageInput: "Hi, is this still for sale?"
        }
    }
});

const sendButton = wrapper.find('#send-btn');

/**
 * Tests the send message button calls the send message function
 */
test("Test to card send message buttons calls the send message function", async () => {
    wrapper.vm.$parent.init = () => { return 1; }
    api.notifications.postMessage= jest.fn().mockImplementation(async () => { return 1; });
    await sendButton.trigger("click");
    expect(api.notifications.postMessage).toHaveBeenCalled();
})
