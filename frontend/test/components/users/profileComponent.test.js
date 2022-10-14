/**
 * profileComponent.test.js
 *
 * Test profile vue component and functions
 */
import VueRouter from 'vue-router';
import VueLogger from 'vuejs-logger';
import logger from "../../../src/logger";
import Buefy from "buefy";
import {createLocalVue, mount} from "@vue/test-utils";
import api from "../../../src/api/api";
import ProfileComponent from "../../../src/components/users/ProfileComponent";
import userProfile from "../../../src/components/users/userProfile";


const localVue = createLocalVue();
localVue.use(VueRouter)
localVue.use(Buefy);

localVue.use(VueLogger, logger.options);
logger.inject(localVue.$log);

let mockUser = {
    data: {
        "lastName":"Askey",
        "role":"ROLE_USER",
        "created":"[native Date Thu Mar 04 2021 13:00:00 GMT+1300 (New Zealand Daylight Time)]",
        "businessesAdministered":[{"primaryAdministratorId":1,"address":{"country":"New Zealand","streetName":"James Street","streetNumber":"5","city":"Kaiapoi","postcode":"8011","countryCurrency":null,"region":"Canterbury"},"created":"2021-02-04","name":"Lumbridge General Store","description":"Sells an assortment of friendly neighbourhood war criminals","id":1,"businessType":"Retail Trade","administrators":["Christian Askey"]},{"primaryAdministratorId":1,"address":{"country":"Herehere","streetName":"Here Street","streetNumber":"3","city":"Hereland","postcode":"8022","countryCurrency":null,"region":"Herefield"},"created":"2011-07-08","name":"Macs Donald","description":"Deals exclusively in Macaroni and Cheese","id":2,"businessType":"Retail Trade","administrators":["Christian Askey","Mary Mason"]}],
        "bio":"3rd year Computer Science student",
        "dateOfBirth":"[native Date Tue Jun 27 2000 12:00:00 GMT+1200 (New Zealand Standard Time)]",
        "firstName":"Christian",
        "phoneNumber":"+64 22 350 5775",
        "countryForCurrency":"Therethere",
        "nickname":"Rapskey",
        "middleName":"Julian",
        "id":1,
        "email":"cja128@uclive.ac.nz",
        "homeAddress":{"country":"Therethere","streetName":"There Street","streetNumber":"1","city":"Thereland","postcode":"8022","region":"Therefield"}
    }
};

userProfile.getUserImageLink = jest.fn().mockImplementation(() => {return  "https://bulma.io/images/placeholders/128x128.png";})
userProfile.formatFullAddress= jest.fn().mockImplementation(() => {return "Formatted Address"});
userProfile.dateDiff = jest.fn().mockImplementation(() => {return "Diff"});
api.currency.getCurrentCurrencyByCountry = jest.fn().mockImplementation(async () => {return "NZD";});
api.currency.queryCountryCurrency = jest.fn().mockImplementation(async () => {return "AUD";});
api.users.getUser = jest.fn().mockImplementation(async () => {return mockUser;});
api.users.updateUser = jest.fn().mockImplementation(async () => {});
api.users.updateCountryForCurrency = jest.fn().mockImplementation(async () => {});
localStorage.setItem("id", "10");

const router = new VueRouter();
let wrapper = mount(ProfileComponent, {
    localVue,
    router,
    stubs: ['router-link'],
    data() {
        return {
            editUser: mockUser,
            isShow: true
        }
    },
    propsData: {},
});



test('Profile image and minimum date is set on created', () => {
    let expectedDate = new Date(new Date().getFullYear() - 18, new Date().getMonth(), new Date().getDate());
    expect(wrapper.vm.$data.minDate).toStrictEqual(expectedDate);
    expect(userProfile.getUserImageLink).toHaveBeenCalled();
    // Since avatar.png returns an empty string (because it's mocked), the background image should be url().
    expect(wrapper.vm.$data.previewImage).toBe('');
});

test('User is retrieved and component data is set', () => {
    expect(api.users.getUser).toHaveBeenCalledWith("10");
    expect(wrapper.vm.$data.user).toEqual(mockUser.data);
    expect(wrapper.vm.$data.currency.code).toBe("NZD");
});

test('Edit is set to true when edit is clicked and false when cancel is clicked', async () => {
    const editButton = wrapper.find('#edit-button');
    await editButton.trigger('click');
    expect(wrapper.vm.$data.isEdit).toBeTruthy();
    const cancelButton = wrapper.find('#cancel-button');
    await cancelButton.trigger('click');
    expect(wrapper.vm.$data.isEdit).toBeFalsy();
});

test('Saving an edited user makes calls backend', async () => {
    await wrapper.find('#edit-button').trigger('click');
    let mockEditAddress = {
        country: 'EditCountry',
        region: 'EditRegion',
        city: 'EditCity',
        postcode: 'EditPostcode',
        streetNumber: 'EditStreetNum',
        streetName: 'EditStreetName',
    }
    wrapper.vm.$data.editCountry = mockEditAddress.country;
    wrapper.vm.$data.editRegion = mockEditAddress.region;
    wrapper.vm.$data.editCity = mockEditAddress.city;
    wrapper.vm.$data.editPostcode = mockEditAddress.postcode;
    wrapper.vm.$data.editStreetNum = mockEditAddress.streetNumber;
    wrapper.vm.$data.editStreetName = mockEditAddress.streetName;
    await wrapper.find('#save-button').trigger('click');
    expect(wrapper.vm.$data.editUser.homeAddress).toEqual(mockEditAddress);
    expect(api.currency.queryCountryCurrency).toBeCalledWith(mockEditAddress.country);
    expect(wrapper.vm.$data.currency).toBe("AUD");
    mockUser.data.homeAddress = mockEditAddress;
    expect(api.users.getUser).toBeCalled();
});

test('Clearing email and confirm email buttons', async () => {
    wrapper.vm.$data.conEmail = 'Something';
    // Not sure how to get icon-right of the b-input so this is the closest alternative
    wrapper.vm.clearEmailClick();
    expect(wrapper.vm.$data.editUser.email).toHaveLength(0);
});



