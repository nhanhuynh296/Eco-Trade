<template>
  <div class="feed-component-container">
    <b-tabs position="right"
            type="is-boxed"
            :animated="false"
            multiline
            expanded
            class="home-notification-tabs"
            v-model="activeTab">

      <b-tab-item v-bind:label="tab.name" :key="tab.name" :value="tab.value" :icon="tab.icon" v-for="tab in getFilteredTabs">
        <div v-if="tab.value === activeTab">
          <transition name="slide-fade">
            <b-notification
                v-if="showUndo"
                type="is-dark"
                has-icon
                @close="showUndo = false"
                aria-close-label="Close notification"
                class="delete-notification mx-2"
                role="alert">
              Deleted notification '{{ deletedNotificationTitle }}'
              <b-button class="mr-4 py-4 undo-button" type="is-dark" @click="restoreNotification">
                Undo
              </b-button>
            </b-notification>
          </transition>
          <feed-tag-selector-filter @filterTag="filterNotificationsByTag" class="mx-2 my-4"/>
          <div class="feed-item" v-for="item in notifications" :key="item.id">
            <FeedItemComponent :notification="item" @removeNotification="removeNotificationFromList($event)" @addTag="addTagToNotification" @updateCategory="updateNotificationCategory"/>
          </div>
        </div>
      </b-tab-item>
    </b-tabs>
  </div>
</template>

<script>
import FeedItemComponent from "./FeedItemComponent";
import api from "../../../src/api/api";
import logger from "../../../src/logger";
import error from "../../../src/components/errorPopup";
import FeedTagSelectorFilter from "../../../src/components/home/FeedTagSelectorFilter";

export default {
  name: "FeedComponent",
  title: 'Home',
  props: ['userId'],
  components: {FeedTagSelectorFilter, FeedItemComponent},

  data() {
    return {
      notifications: [],
      typeFilter: null,
      tabs: [
        { name: "All", icon: "bell", value: "all", show: true },
        { name: "Messages", icon: "message", value: "COMMENT_RECEIVED", show: true},
        { name: "Purchases", icon: "cash-multiple", value: "BOUGHT", show: true},
        { name: "Expiring", icon: "book-open", value: "CARD_EXPIRING", show: true },
        { name: "Deleted Cards", icon: "delete", value: "CARD_DELETED", show: true },
        { name: "Liked", icon: "thumb-up", value: "LIKED", show: true },
        { name: "Keywords", icon: "clipboard-text-multiple", value: "KEYWORD_ADDED",
          show: localStorage.getItem("role") === "ROLE_ADMIN" || localStorage.getItem("role") === "ROLE_DEFAULT_ADMIN" }
      ],
      activeTab: "all",
      deletedNotificationId: null,
      cachedNotification: null,
      cachedNotificationPosition: null,
      deletedNotificationTitle: "",
      showUndo: false,
    }
  },

  /**
   * Initialise the catalogue table
   **/
  created() {
    this.onInitialize();
  },

  methods: {

    /**
     * On initialize of this component gets the notifications and splits them based on their type.
     */
    onInitialize() {
      api.notifications
        .getNotification({})
        .then(res => {
          this.notifications = res.data;
        })
        .catch(err => {
          logger.getLogger().warn(err);
          error.showPopupFromServer(this, [err], false);
        });
    },

    /**
     * Filter notifications based on tag
     * @param filterTag Tag to filter by
     */
    filterNotificationsByTag(filterTag) {
      this.getNotificationFromTabAndFilter(this.activeTab, filterTag);
    },

    /**
     * Get notification based on specific tab and specified filter option
     *
     * @param tab Open tab
     * @param filter Tag filter
     */
    getNotificationFromTabAndFilter(tab, filter) {
      const request = {}
      if (tab && tab !== 'all') {
        request.notificationType = tab;
      }

      if (filter) {
        request.notificationTag = filter;
      }

      api.notifications
        .getNotification(request)
        .then(res => {
          this.notifications = res.data;
        })
        .catch(err => {
          logger.getLogger().warn(err);
          error.showPopupFromServer(this, [err], false);
        });
    },


    /**
     * Remove a notification from a supplied list of notifications. If we are archiving the notification, remove it from the
     * home feed notification, but do not display the delete notification undo button.
     *
     * @param event object (contains notification and deleted notification id)
     */
    removeNotificationFromList(event) {
      for (let i = 0; i < this.notifications.length; i++) {
        if (this.notifications[i].id === event.notification.id) {
          if (event.action !== 'archiving') {
            this.deletedNotificationTitle = event.notification.fullTitle;
            this.cachedNotification = this.notifications[i];
            this.cachedNotificationPosition = i;
            this.notifications.splice(i, 1);
            this.showUndo = true;
            break;
          } else {
            this.deletedNotificationTitle = event.notification.fullTitle;
            this.notifications.splice(i, 1);
            break;
          }

        }
      }
      this.deletedNotificationId = event.cacheId;
    },

    /**
     * Restores the most recently deleted notification stored in this.deletedNotificationId
     */
    async restoreNotification() {
      await api.notifications.restoreNotification(this.deletedNotificationId)
      .then((res) => {
        this.showUndo = false;
        this.cachedNotification.id = res.data.id;
        let index = this.cachedNotificationPosition;
        this.notifications.splice(index, 0, this.cachedNotification);

      })
      .catch(err => {
        logger.getLogger().warn(err);
        error.showPopupFromServer(this, [err], false);
      });
    },

    /**
     * Add tag to a notification
     *
     * @param notification Notification object
     * @param type Type of tag to add
     */
    addTagToNotification(notification, type) {
      this.notifications.find(x => x.id === notification.id).tag = type;
    },

    /**
     * Updates the notification to have the category
     *
     * @param notification notification object
     * @param category type of the notification category
     */
    updateNotificationCategory(notification, category) {
      let foundNotification = this.notifications.find(x => x.id === notification.id)
      if (foundNotification) {
        foundNotification.category = category;
      }
    }
  },

  watch: {
    activeTab() {
      this.getNotificationFromTabAndFilter(this.activeTab, null);
    }
  },

  computed: {
    getFilteredTabs() {
      return this.tabs.filter(x => x.show);
    }
  }
}
</script>
