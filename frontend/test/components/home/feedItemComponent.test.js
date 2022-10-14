/**
 * feedItemComponent.test.js
 *
 * Test feed item vue component and functions
 */
import VueRouter from 'vue-router';
import VueLogger from 'vuejs-logger';
import logger from "../../../src/logger";
import Buefy from "buefy";
import {createLocalVue, mount} from "@vue/test-utils";
import api from "../../../src/api/api";
import FeedItemComponent from "../../../src/components/home/FeedItemComponent";


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

const mockNotifications = {
    expiring: {
        "id":103,
        "recipientId":1,
        "senderId":2,
        "type":"EXPIRING",
        "message": "your card is expiring bro",
        "cardId":1,
        "created":"2021-08-12T00:00:00",
        "keywordId": 3,
    },

    likedListing: {
        "id":104,
        "recipientId":1,
        "type":"LIKED",
        "message": "",
        "listingId":2,
        "created":"2021-08-12T00:00:00",
    }
}

api.users.getUser = jest.fn().mockImplementation(async () => {return mockUser;});
api.notifications.deleteNotification = jest.fn().mockImplementation(async () => {return {data : {id: 1}}});
api.keywords.deleteKeyword = jest.fn().mockImplementation(async () => {});
api.notifications.updateTag = jest.fn().mockImplementation(async () => { return true; });
api.notifications.updateCategory = jest.fn().mockImplementation(async () => { return true; });
localStorage.setItem("id", "1");

const router = new VueRouter({
    routes: [
        {name: "profile", path:"/profile"}
    ]
});

/**
 * Stop any router issues throwing
 */
const originalPush = router.push;
router.push = function push(location) {
    return originalPush.call(this, location).catch(err => err);
}

let wrapper = mount(FeedItemComponent, {
    localVue,
    router,
    stubs: ['router-link', 'openListingModal'],
    data() {
        return {
            editUser: mockUser,
        }
    },
    propsData: {notification: mockNotifications.expiring},
});

test('Initialisation of data', () => {
   expect(wrapper.vm.message).toBe(mockNotifications.expiring.message);
   expect(api.users.getUser).toBeCalledWith(mockNotifications.expiring.senderId);
   expect(wrapper.vm.sender).toBe(mockUser.data);
});

/**
 * Tests getting colour from the type of notification
 */
test('getColourFromType method returns correct class', () => {
   wrapper.vm.getColourAndClickableFromType({type: 'CARD_DELETED'});
   expect(wrapper.vm.$data.color).toBe('feed-title-deleted');

   wrapper.vm.getColourAndClickableFromType({type: 'EXPIRING'});
   expect(wrapper.vm.$data.color).toBe('feed-title-expiring');

   wrapper.vm.getColourAndClickableFromType({type: 'STARRED'});
   expect(wrapper.vm.$data.color).toBe('feed-title-starred');

   wrapper.vm.getColourAndClickableFromType({type: 'KEYWORD_ADDED'});
   expect(wrapper.vm.$data.color).toBe('feed-title-keyword');

   wrapper.vm.getColourAndClickableFromType({type: 'GENERAL'});
   expect(wrapper.vm.$data.color).toBe('feed-title-general');
});

/**
 * Tests redirecting to a specific card after clicking on a card notification
 */
test('Clicking the notification redirects to card', async () => {
    const notificationBtn = wrapper.find('#notification-container');
    await notificationBtn.trigger('click');

    expect(wrapper.vm.$route.name).toBe("profile");
});

/**
 * Tests deleting a notification from the notifications menu
 */
test('Clicking delete notification sends API call', async () => {
    const deleteBtn = wrapper.find('#delete-notification-btn');
    await deleteBtn.trigger('click');

    expect(api.notifications.deleteNotification).toBeCalledWith(mockNotifications.expiring.id);
    expect(wrapper.emitted().removeNotification).toBeTruthy();
});

/**
 * Tests deleting a keyword through the notifications
 */
test('Clicking delete keyword sends API call', async () => {
    const deleteBtn = wrapper.find('#delete-keyword-btn');
    await deleteBtn.trigger('click');

    expect(api.keywords.deleteKeyword).toBeCalledWith(mockNotifications.expiring.keywordId);
    expect(wrapper.emitted().removeNotification).toBeTruthy();
});

/**
 * Tests getting the formatted title of a card notification
 */
test('Correct title is returned by getTitle', () => {
   let title = wrapper.vm.getTitle(mockNotifications.expiring);

   expect(title).toBe('Card About To Expire');
});

/**
 * Tests clicking on a listing liked notification and opens the listing
 */
test("Redirecting opening listing", async () => {
    await wrapper.setProps({ notification: mockNotifications.likedListing });

    wrapper.vm.$buefy.modal.open = jest.fn().mockImplementation(() => {});

    const clickableDiv = wrapper.find('#notification-container');
    await clickableDiv.trigger("click");

    expect(wrapper.vm.$buefy.modal.open).toHaveBeenCalled();
});

/**
 * Tests adding colours to the notification tags
 */
test("Adding the proper tag colour", () => {
    expect(wrapper.vm.getTypeFromTag("High Priority")).not.toBe("is-dark");
    expect(wrapper.vm.getTypeFromTag("Medium Priority")).not.toBe("is-dark");
    expect(wrapper.vm.getTypeFromTag("Low Priority")).not.toBe("is-dark");
    expect(wrapper.vm.getTypeFromTag("Interesting")).not.toBe("is-dark");
    expect(wrapper.vm.getTypeFromTag("Requires Attention")).not.toBe("is-dark");
    expect(wrapper.vm.getTypeFromTag("adwadsadawdas")).toBe("is-dark");
});

/**
 * Tests adding a tag to a notification
 */
test("Adding a tag to a notification to emit an event", async () => {
    mockNotifications.expiring.tag = "High Priority";
    await wrapper.vm.addTag(mockNotifications.expiring, "High Priority");
    expect(api.notifications.updateTag).toBeCalled();
    expect(wrapper.emitted().addTag).toBeTruthy();
})

/**
 * Tests starring a notification
 */
test("Starring a notification a notification to emit an event", async () => {
    mockNotifications.expiring.category = "READ";
    await wrapper.vm.starFeedItem(mockNotifications.expiring);
    expect(api.notifications.updateCategory).toBeCalled();
    expect(wrapper.emitted().updateCategory).toBeTruthy();
})

test("Update notification category and expect api to be called and event to be emitted", async () => {
    mockNotifications.expiring.category = "READ";
    wrapper.vm.$buefy.toast.open = jest.fn().mockImplementation(() => {});
    await wrapper.vm.updateNotificationCategory(mockNotifications.expiring, 'ARCHIVED');
    expect(api.notifications.updateCategory).toBeCalledWith(mockNotifications.expiring.id, 'ARCHIVED');
    expect(wrapper.vm.$buefy.toast.open).toBeCalledWith({
        duration: 5000,
        message: `You have successfully archived the notification`,
        type: 'is-success',
    });
    expect(wrapper.emitted().updateCategory).toBeTruthy();
})
