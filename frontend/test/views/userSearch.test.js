/**
 * userSearch.test.js
 *
 * Test user search vue view and functions
 */
import VueRouter from 'vue-router';
import VueLogger from 'vuejs-logger';
import logger from "../../src/logger";
import Buefy from "buefy";
import {createLocalVue, mount} from "@vue/test-utils";
import api from "../../src/api/api";
import UserSearch from "../../src/views/UserSearch";

const localVue = createLocalVue();
localVue.use(VueRouter)
localVue.use(Buefy);

localVue.use(VueLogger, logger.options);
logger.inject(localVue.$log);

const searchResultData = {
    data: {
        paginationElements: [
            {lastName:"Askey",
                role:"ROLE_USER",
                created:"[native Date Thu Mar 04 2021 13:00:00 GMT+1300 (New Zealand Daylight Time)]",
                businessesAdministered:[{primaryAdministratorId:1,address:{country:"New Zealand",streetName:"James Street",streetNumber:"5",city:"Kaiapoi",postcode:"8011",countryCurrency:null,region:"Canterbury"},
                    created:"2021-02-04",name:"Lumbridge General Store",description:"Sells an assortment of friendly neighbourhood war criminals",id:1,businessType:"Retail Trade",administrators:["Christian Askey"]},
                    {primaryAdministratorId:1,address:{country:"Herehere",streetName:"Here Street",streetNumber:"3",city:"Hereland",postcode:"8022",countryCurrency:null,region:"Herefield"},
                        created:"2011-07-08",name:"Macs Donald",description:"Deals exclusively in Macaroni and Cheese",id:2,businessType:"Retail Trade",administrators:["Christian Askey","Mary Mason"]}],
                bio:"3rd year Computer Science student",
                dateOfBirth:"[native Date Tue Jun 27 2000 12:00:00 GMT+1200 (New Zealand Standard Time)]",
                firstName:"Christian",
                phoneNumber:"+64 22 350 5775",
                countryForCurrency:"Therethere",
                nickname:"Rapskey",
                middleName:"Julian",
                id:1,
                email:"cja128@uclive.ac.nz",
                homeAddress:{country:"Therethere",streetName:"There Street",streetNumber:"1",city:"Thereland",postcode:"8022",region:"Therefield"}}
        ],
        totalPages: 1,
        totalElements: 2,
    }
}

const mockUser = {
    data: {
            lastName:"Askey",
            role:"ROLE_USER",
            created:"[native Date Thu Mar 04 2021 13:00:00 GMT+1300 (New Zealand Daylight Time)]",
            businessesAdministered:[{primaryAdministratorId:1,address:{country:"New Zealand",streetName:"James Street",streetNumber:"5",city:"Kaiapoi",postcode:"8011",countryCurrency:null,region:"Canterbury"},
                created:"2021-02-04",name:"Lumbridge General Store",description:"Sells an assortment of friendly neighbourhood war criminals",id:1,businessType:"Retail Trade",administrators:["Christian Askey"]},
                {primaryAdministratorId:1,address:{country:"Herehere",streetName:"Here Street",streetNumber:"3",city:"Hereland",postcode:"8022",countryCurrency:null,region:"Herefield"},
                    created:"2011-07-08",name:"Macs Donald",description:"Deals exclusively in Macaroni and Cheese",id:2,businessType:"Retail Trade",administrators:["Christian Askey","Mary Mason"]}],
            bio:"3rd year Computer Science student",
            dateOfBirth:"[native Date Tue Jun 27 2000 12:00:00 GMT+1200 (New Zealand Standard Time)]",
            firstName:"Christian",
            phoneNumber:"+64 22 350 5775",
            countryForCurrency:"Therethere",
            nickname:"Rapskey",
            middleName:"Julian",
            id:1,
            email:"cja128@uclive.ac.nz",
            homeAddress:{country:"Therethere",streetName:"There Street",streetNumber:"1",city:"Thereland",postcode:"8022",region:"Therefield"}
    }
};

localStorage.setItem("id", "-1");

api.users.searchUsers = jest.fn().mockImplementation(async () => { return searchResultData; });
api.users.getUser = jest.fn().mockImplementation(async () => { return mockUser; });
api.admin.makeAdmin = jest.fn().mockImplementation(async () => { return true; });
api.admin.revokeAdmin = jest.fn().mockImplementation(async () => { return true; })

const router = new VueRouter();
let wrapper = mount(UserSearch, {
    localVue,
    router,
    stubs: ['router-link'],
    data() {
        return {
            showDetailIcon: true,
            userResultData: [],
            page: 1,
            perPage: 10,
            paginationInfo: {
                totalPages: 0,
                totalElements: 0
            },
            //Is the logged in user an admin
            loggedInUserAdmin: false,
            lowerCount: 0,
            upperCount: 0,
        }
    },
    propsData: {},
});

/**
 * Search for nothing shows all users
 */
test("Searching for nothing results in all users displayed", async () => {
    await wrapper.vm.onPageChange(1);

    expect(wrapper.vm.$data.page).toEqual(1);
    expect(wrapper.vm.$data.userResultData).toBeTruthy();
    expect(wrapper.vm.$data.userResultData).toEqual(searchResultData.data.paginationElements);
});

/**
 * Logged in user is not an admin, make/revoke admin button not visible
 */
test("Searching for users does not show admin controls when not admin", async () => {
    mockUser.data.role = "ROLE_USER";
    await wrapper.vm.onPageChange(1);

    expect(wrapper.vm.isUserAnAdmin(mockUser.data.role)).toBeFalsy();
    wrapper.vm.$data.loggedInUserAdmin = false;

    await wrapper.vm.$nextTick();

    const adminButton = wrapper.find("#make-admin-button");
    expect(adminButton.exists()).toBeFalsy();
});

/**
 * Logged in user is an admin, make/revoke admin button should be visible
 */
test("Searching for users shows admin controls", async () => {
    mockUser.data.role = "ROLE_ADMIN";
    await wrapper.vm.onPageChange(1);

    expect(wrapper.vm.isUserAnAdmin(mockUser.data.role)).toBeTruthy();
    wrapper.vm.$data.loggedInUserAdmin = true;

    await wrapper.vm.$nextTick();

    const adminButton = wrapper.findAll("#make-admin-button");
    expect(adminButton.at(0)).toBeDefined();
});

/*
 * Test setting logged in user role
 */
test("Sets whether user is not admin properly", async () => {
    mockUser.data.role = "ROLE_USER";
    await wrapper.vm.setLoggedInUserRole();

    expect(wrapper.vm.$data.loggedInUserAdmin).toBeFalsy();
});

/*
 * Test setting logged in user role admin
 */
test("Sets whether user is admin properly", async () => {
    mockUser.data.role = "ROLE_ADMIN";
    await wrapper.vm.setLoggedInUserRole();

    expect(wrapper.vm.$data.loggedInUserAdmin).toBeTruthy();
});

/*
 * Test making another user an admin
 */
test("Makes another user an admin", async () => {
    mockUser.data.role = "ROLE_ADMIN";
    await wrapper.vm.onPageChange(1);

    expect(wrapper.vm.isUserAnAdmin(mockUser.data.role)).toBeTruthy();
    wrapper.vm.$data.loggedInUserAdmin = true;

    await wrapper.vm.$nextTick();

    const adminButton = wrapper.findAll("#make-admin-button").at(0);
    expect(adminButton.exists()).toBeTruthy();

    await adminButton.trigger("click");

    expect(wrapper.vm.$data.userResultData[0].role).toBe("ROLE_ADMIN");
});

/*
 * Test making another user a normal user
 */
test("Revoke users admin status", async () => {
    mockUser.data.role = "ROLE_ADMIN";
    await wrapper.vm.onPageChange(1);
    wrapper.vm.$data.userResultData[0].role = "ROLE_ADMIN";

    expect(wrapper.vm.isUserAnAdmin(mockUser.data.role)).toBeTruthy();
    wrapper.vm.$data.loggedInUserAdmin = true;

    await wrapper.vm.$nextTick();

    const adminButton = wrapper.findAll("#make-admin-button").at(0);
    expect(adminButton.exists()).toBeTruthy();

    await adminButton.trigger("click");

    expect(wrapper.vm.$data.userResultData[0].role).toBe("ROLE_DEFAULT");
});

/**
 * Check that the user thumbnail is shown in the user search results
 */
test("Shows the user thumbnail in the search results", async () => {
    const thumbnail = wrapper.find("#user-thumbnail");
    expect(thumbnail.exists()).toBeTruthy();
});
