/**
 * feedComponent.test.js
 *
 * Test feed vue component works properly
 */
import VueRouter from 'vue-router';
import VueLogger from 'vuejs-logger';
import logger from "../../../src/logger";
import Buefy from "buefy";
import {createLocalVue, mount} from "@vue/test-utils";
import FeedComponent from "../../../src/components/home/FeedComponent";
import api from "../../../src/api/api";

const localVue = createLocalVue();
localVue.use(VueRouter)
localVue.use(Buefy);

localVue.use(VueLogger, logger.options);
logger.inject(localVue.$log);


const router = new VueRouter({});

/**
 * Stop any router issues throwing
 */
const originalPush = router.push;
router.push = function push(location) {
    return originalPush.call(this, location).catch(err => err);
}

/**
 * Stop created() running on a component
 */
const mergeCreatedStrategy = localVue.config.optionMergeStrategies.created;
localVue.config.optionMergeStrategies.created = (parent) => {
    return mergeCreatedStrategy(parent);
};

let wrapper = mount(FeedComponent, {
    localVue,
    router,
    stubs: ['router-link', 'openListingModal'],
    data() {
        return {
            notifications: [{id: 1}, {id: 2}],
            tabs: [
                { name: "All", icon: "bell", value: "all", show: true },
                { name: "Messages", icon: "bell", value: "COMMENT_RECEIVED", show: true},
                { name: "Expiring", icon: "book-open", value: "CARD_EXPIRING", show: true },
                { name: "Deleted", icon: "archive", value: "CARD_DELETED", show: true },
                { name: "Liked", icon: "clipboard-text-multiple", value: "LIKED", show: true },
                { name: "Keywords", icon: "clipboard-text-multiple", value: "KEYWORD_ADDED",
                    show: this.userRole === "ROLE_ADMIN" || this.userRole === "ROLE_DEFAULT_ADMIN" }
            ],
            activeTab: "all",

            userRole: "ROLE_ADMIN"
        }
    },
});

api.notifications.getNotification = jest.fn().mockImplementation(async () => { });
api.notifications.restoreNotification = jest.fn().mockImplementation(async () => { return {data: {id : 5}}  });

/**
 * Get all notifications with a tag
 */
test('Getting all notifications with a tag calls the API with a notification tag', () => {
    wrapper.vm.filterNotificationsByTag("abc");
    expect(api.notifications.getNotification).toBeCalledWith({notificationTag: "abc"});
});

/**
 * Get all notifications with a tag and a notification type
 */
test('Getting all notifications with a tag and type', () => {
    wrapper.vm.getNotificationFromTabAndFilter("expired", "INTERESTING");
    expect(api.notifications.getNotification).toBeCalledWith({notificationType: "expired", notificationTag: "INTERESTING"});

    wrapper.vm.getNotificationFromTabAndFilter("all", "INTERESTING");
    expect(api.notifications.getNotification).toBeCalledWith({notificationTag: "INTERESTING"});

    wrapper.vm.getNotificationFromTabAndFilter(null, null);
    expect(api.notifications.getNotification).toBeCalledWith({});
});

/**
 * Add a tag to a notification
 */
test('Adding notification tag', () => {
    expect(wrapper.vm.$data.notifications.find(x => x.id === 2).tag).toBeUndefined();
    wrapper.vm.addTagToNotification({id: 2}, "HI ADD ME");
    expect(wrapper.vm.$data.notifications.find(x => x.id === 2).tag).toBeDefined();
});

/**
 * Remove a notification by ID
 */
test('Removing notification', () => {
    expect(wrapper.vm.$data.notifications).toContainEqual({id: 1});
    wrapper.vm.removeNotificationFromList({notification: {id: 1}, cacheId: 1});
    expect(wrapper.vm.$data.notifications.length).not.toContain({id: 1});
});

/**
 * Restore a notification
 */
test('Restoring a notificaiton', async () => {
   expect(wrapper.vm.$data.cachedNotification).toStrictEqual({id: 1});
   await wrapper.vm.restoreNotification();
   expect(api.notifications.restoreNotification).toBeCalled();
   expect(wrapper.vm.$data.notifications).toContainEqual({id: 5});
});

/**
 * Add the star category by ID
 */
test('Adding starred category', () => {
    expect(wrapper.vm.$data.notifications.find(x => x.id === 2).category).toBeUndefined();
    wrapper.vm.updateNotificationCategory({id: 2}, "STARRED");
    expect(wrapper.vm.$data.notifications.find(x => x.id === 2).category).toBeDefined();
});
