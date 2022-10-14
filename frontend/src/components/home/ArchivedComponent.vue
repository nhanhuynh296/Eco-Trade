<template>
  <div class="archived-component-container">
    <b-tabs position="right"
            type="is-boxed"
            :animated="false"
            multiline
            expanded
            class="home-notification-tabs"
            v-model="activeTab">

      <b-tab-item v-bind:label="tab.name" :key="tab.name" :value="tab.value" :icon="tab.icon" v-for="tab in getFilteredTabs">
        <div v-if="tab.value === activeTab && notifications.length >= 1">
          <transition name="slide-fade">
            <b-notification
                v-if="showUndo"
                type="is-dark"
                has-icon
                aria-close-label="Close notification"
                class="delete-notification mx-2"
                role="alert">
              Deleted notification '{{ deletedNotificationTitle }}'
              <b-button class="mr-4 py-4 undo-button" type="is-dark" @click="restoreArchived">
                Undo
              </b-button>
            </b-notification>
          </transition>

          <div class="feed-item" v-for="item in notifications" :key="item.id">
            <FeedItemComponent :notification="item" :notification-tag="item.tag" @removeArchived="removeArchivedFromList($event)"/>
          </div>
        </div>
        <div v-else>
          <p style="text-align: center; font-weight: bold; font-size: 150%">
            You have no archived notifications
          </p>
        </div>
      </b-tab-item>
    </b-tabs>
  </div>
</template>

<script>
import api from "@/api/api";
import logger from "@/logger";
import error from "@/components/errorPopup";
import FeedItemComponent from "@/components/home/FeedItemComponent";

export default {
  name: "ArchivedComponent",
  props: ['userId'],
  components: {FeedItemComponent},

  data() {
    return {
      notifications: [],
      tabs: [
        { name: "Archived", icon: "archive-arrow-down-outline", value: "ARCHIVED", show: true }],
      activeTab: "ARCHIVED",

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
      const request = {};
      request.notificationCategory = "ARCHIVED";
      api.notifications
          .getNotification(request)
          .then(res => {
            this.notifications = res.data
          })
          .catch(err => {
            logger.getLogger().warn(err);
            error.showPopupFromServer(this, [err], false);
          });
    },

    /**
     * Remove a archived notification from a supplied list of archived notifications
     *
     * @param notification object
     */
    removeArchivedFromList(event) {
      for (let i = 0; i < this.notifications.length; i++) {
        if (this.notifications[i].id === event.notification.id) {
          this.deletedNotificationTitle = event.notification.fullTitle;
          this.cachedNotification = this.notifications[i];
          this.cachedNotificationPosition = i;
          this.notifications.splice(i, 1);
          this.showUndo = true;
          break;
        }
      }
      this.deletedNotificationId = event.cacheId;
    },

    /**
     * Restores the most recently deleted archived notification stored in this.deletedNotificationId
     */
    async restoreArchived() {
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
  },


  computed: {
    getFilteredTabs() {
      return this.tabs.filter(x => x.show);
    }
  }
}
</script>
