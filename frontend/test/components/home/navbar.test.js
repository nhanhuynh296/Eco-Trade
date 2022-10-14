/**
 * navbar.test.js
 *
 * Test navbar functionality
 */
import VueRouter from 'vue-router';
import Buefy from "buefy";
import NavBarComponent from "../../../src/components/home/NavBarComponent";
import {mount, createLocalVue} from "@vue/test-utils";
import api from "../../../src/api/api";
import navBar from "../../../src/components/home/navBar";

const localVue = createLocalVue();
localVue.use(VueRouter);
localVue.use(Buefy);

const router = new VueRouter({
    routes: [
        {name: "home", path:"/"},
        {name: "profile", path:"/profile"}
    ]
});

localStorage.setItem("id", "10");
localStorage.setItem("user-type", "user");
localStorage.setItem("business-id", "1");
let mockUser = {
    data: {
        userRole: "ROLE_USER",
        firstName: "Swapnil",
        businessesAdministered: [
            {
                id: 1,
                name: "Cool business",
            }
        ]
    },
}

let mockNotifications = {
    data: [
        {
            id: 1,
            type: "COMMENT_RECEIVED",
            message: "This is a \nnotification",
        },
        {
            id: 2,
            type: "CARD_EXPIRING",
            message: "This is a \nnotification",
        },
    ]
}


api.loginRegister.isLoggedIn = jest.fn().mockImplementation(() => { return true; });
api.loginRegister.logoutUser = jest.fn().mockImplementation();
api.users.getUser = jest.fn().mockImplementation(async () => { return mockUser; });
api.notifications.getNotification = jest.fn().mockImplementation(async () => {return mockNotifications;});
api.notifications.deleteNotification = jest.fn().mockImplementation(async () => {});

let wrapper;

wrapper = mount(NavBarComponent, {
    localVue,
    router,
    stubs: ['router-link'],
});

test("isLogged is set to true on mount", () => {
    expect(wrapper.vm.$data.isLogged).toEqual(true);
    expect(api.loginRegister.isLoggedIn).toHaveBeenCalled();
});

test("User data is obtained on mount", () => {
    let mockBusiness = mockUser.data.businessesAdministered[0];
    expect(api.users.getUser).toHaveBeenCalledWith("10");
    expect(wrapper.vm.$data.userName).toEqual(mockUser.data.firstName);
    expect(wrapper.vm.$data.businesses).toEqual([{id: mockBusiness.id, name: mockBusiness.name}]);
    expect(wrapper.vm.$data.welcomeString).toEqual(mockUser.data.firstName);
    expect(wrapper.vm.$data.businessCheck).toEqual(false);
});


test("setWelcomeString sets correct values when user is a default admin", () => {
    let role = "ROLE_DEFAULT_ADMIN";
    wrapper.vm.setWelcomeString(role, "Name");
    expect(wrapper.vm.$data.userName).toEqual("ADMIN");
    expect(wrapper.vm.$data.welcomeString).toEqual("ADMIN");
    expect(wrapper.vm.$data.dgaaCheck).toEqual(true);
    expect(wrapper.vm.$data.adminCheck).toEqual(true);
});


test("setWelcomeString sets correct values when user is an admin", () => {
    let role = "ROLE_ADMIN";
    wrapper.vm.setWelcomeString(role, "Name");
    expect(wrapper.vm.$data.userName).toEqual("Name");
    expect(wrapper.vm.$data.welcomeString).toEqual("Name");
    expect(wrapper.vm.$data.dgaaCheck).toEqual(false);
    expect(wrapper.vm.$data.adminCheck).toEqual(true);
});


test("setWelcomeString sets correct values when role is business", () => {
    let role = "BUSINESS";
    wrapper.vm.setWelcomeString(role, "Business");
    expect(wrapper.vm.$data.businessName).toEqual("Business");
    expect(wrapper.vm.$data.welcomeString).toEqual("Business");
    expect(wrapper.vm.$data.dgaaCheck).toEqual(false);
    expect(wrapper.vm.$data.adminCheck).toEqual(false);
    expect(wrapper.vm.$data.businessCheck).toEqual(true);
});


test("Notifications have been obtained from backend", () => {
    expect(api.notifications.getNotification).toHaveBeenCalled();
    expect(wrapper.vm.$data.notificationsList).toBe(mockNotifications.data);
});

test('Deleting a notification', () => {
    wrapper.vm.deleteNotificationFromPanel(mockNotifications.data[0].id);
    expect(wrapper.vm.notificationsList).toHaveLength(1);
    expect(api.notifications.deleteNotification).toBeCalledWith(mockNotifications.data[0].id);
});


test('Route is change to home if comment notification is clicked', () => {
    wrapper.vm.$refs.notificationDropdown = jest.mock();
    wrapper.vm.$refs.notificationDropdown.toggle = jest.fn()
    wrapper.vm.redirectToCard(mockNotifications.data[0]);
    expect(wrapper.vm.$route.name).toBe("home");
    expect(wrapper.vm.$refs.notificationDropdown.toggle).toBeCalled();
});


test('Route is change to profile if card notification is clicked', () => {
    wrapper.vm.$refs.notificationDropdown.toggle = jest.fn()
    wrapper.vm.$buefy.modal.open = jest.fn().mockImplementation();
    wrapper.vm.redirectToCard(mockNotifications.data[1]);
    expect(wrapper.vm.$route.name).toBe("profile");
    expect(wrapper.vm.$buefy.modal.open).toBeCalled();
    expect(wrapper.vm.$refs.notificationDropdown.toggle).toBeCalled();
});



test('Redirect method', () => {
    wrapper.vm.$router.push = jest.fn();
    wrapper.vm.redirect("profile");
    expect(wrapper.vm.$router.push).toBeCalledWith({name: "profile"})  ;
    wrapper.vm.redirect("home");
    expect(wrapper.vm.$router.push).toBeCalledWith({name: "home"})

});

test('Swapping to business works', () => {
    let mockBusiness = mockUser.data.businessesAdministered[0]
    wrapper.vm.actAsBusiness(mockBusiness);
    expect(localStorage.getItem("user-type")).toBe("business");
    expect(localStorage.getItem("business-id")).toBe(mockBusiness.id.toString());
    expect(wrapper.vm.$data.businessCheck).toBeTruthy();
    expect(wrapper.emitted().update).toBeTruthy();
});

test('Swapping to user works', () => {
    wrapper.vm.actAsUser();
    expect(localStorage.getItem("user-type")).toBe("user");
    expect(localStorage.getItem("business-id")).toBe("-1");
    expect(wrapper.vm.$data.businessId).toEqual(-1);
    expect(wrapper.emitted().update).toBeTruthy();
});

test('Test search type is user', () => {
    wrapper.vm.$router.push = jest.fn();

    wrapper.vm.$data.isUserSearch = true;
    wrapper.vm.$data.searchQuery = "user search";
    navBar.searchUsers = jest.fn().mockImplementation(() => {return "this is a response";});
    wrapper.vm.search();
    expect(navBar.searchUsers).toBeCalledWith("user search");
    expect(wrapper.vm.$router.push).toBeCalledWith({name: "userSearch", query: {page: "1",search: "this is a response", size: "10"}});

})

test('Test search type is business', () => {
    wrapper.vm.$router.push = jest.fn();

    wrapper.vm.$data.isUserSearch = false;
    wrapper.vm.$data.searchQuery = "business search";
    wrapper.vm.$data.businessSearchType = "Accommodation";
    navBar.searchBusinesses = jest.fn().mockImplementation(() => {return "this is a response";});
    wrapper.vm.search();
    expect(navBar.searchBusinesses).toBeCalledWith("business search", "Accommodation");
    expect(wrapper.vm.$router.push).toBeCalledWith({name: "businessSearch", query: {page: "1",search: "a response", size: "10"}});
});

// KEEP AT END
test('Local storage is cleared and user is routed to login upon logout', () => {
    wrapper.vm.logout();
    expect(wrapper.vm.$data.isLogged).toBeFalsy();
    expect(localStorage.getItem("id")).toBeFalsy();
    expect(api.loginRegister.logoutUser).toBeCalled();
});
