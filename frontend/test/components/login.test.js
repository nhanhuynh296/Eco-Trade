/**
 * login.test.js
 *
 * Test login vue component and functions
 */
import Buefy from "buefy";
import LoginComponent from "../../src/components/login/LoginComponent";
import {mount, createLocalVue} from "@vue/test-utils";
import api from "../../src/api/api";

let localVue = createLocalVue();
localVue.use(Buefy);

api.loginRegister.logoutUser = jest.fn();

let wrapper = mount(LoginComponent, {
    localVue,
    stubs: ['router-link'],
    data() {
        return {
            email: "fake.com",
            password: "",
            errors: []
        }
    }
});
let loginButton  = wrapper.find("button");

/**
 * Testing that login button does not allow a user to login with an invalid email address
 */
test("Test that the login button errors out on invalid email", async () => {
    await loginButton.trigger("click");
    expect(wrapper.vm.$data.errors.length).toBeGreaterThan(0);
    expect(wrapper.vm.$data.errors).toContain("Wrong email format");
});

/**
 * Test that login with a valid email doesn't error out
 */
test("Test that the login button accepts a valid email", async () => {
    wrapper.vm.$data.email = "test@test.com";
    await loginButton.trigger("click");
    expect(wrapper.vm.$data.errors.length).toBe(0);
});
