<template>
  <div v-bind:class="{starred: notification.category === 'STARRED', read: notification.category === 'READ', unread: notification.category === 'UNREAD', archived: notification.category === 'ARCHIVED'}">
    <div class="feed-item-component-container">
      <div v-if="sender !== null" class="item-header">
        <h1 id="notification-container" v-on:click="onClick(notification)" :class="color">
          {{ getFullTitle() }}
          <b-icon icon="launch" class="ml-1" v-if="clickable"></b-icon>
        </h1>

        <b-tooltip label="Star (pin to top)" type="is-info">
          <div>
            <div class="feed-item-star" v-on:click="starFeedItem(notification)" :key="updateTag" v-if="notification.category !== 'ARCHIVED'">
              <b-icon icon="star-outline" type="is-info" v-if="notification.category !== 'STARRED'"/>
              <b-icon icon="star" type="is-warning" v-else/>
            </div>
          </div>
        </b-tooltip>

        <b-tooltip label="Mark as read" type="is-info" v-if="notification.category === 'UNREAD'">
          <div v-if="notification.category === 'UNREAD'">
            <div class="feed-item-read" @click="updateNotificationCategory(notification, 'READ')">
              <b-icon icon="email" type="is-info" />
            </div>
          </div>
        </b-tooltip>

        <b-tooltip label="Mark as unread" type="is-info" v-if="notification.category === 'READ'">
          <div v-if="notification.category === 'READ'">
            <div class="feed-item-read" @click="updateNotificationCategory(notification, 'UNREAD')">
              <b-icon icon="email-open-outline" type="is-info"/>
            </div>
          </div>
        </b-tooltip>

        <div>
          <div class="control mx-4" v-show="notification.tag" :key="updateTag">
            <b-tag :type="getTypeFromTag(notification.tag)" attached closable @close="addTag(notification, null)" @click.native.stop="">
              {{ notification.tag }}
            </b-tag>
          </div>
        </div>

        <div v-if="notification && notification.category !== 'ARCHIVED'" style="margin-left: auto; margin-right: 0">
          <b-dropdown type="is-danger" @click.native.stop position="is-bottom-left">
            <template #trigger>
              <b-button icon-right="menu-down" type="is-danger" />
            </template>

            <b-dropdown-item @click.native.stop="deleteKeyword(notification.keywordId)" id="delete-keyword-btn" v-if="notification.keywordId">
              <b-icon
                  icon="delete"
                  size="is-small"
              />
              Delete Keyword
            </b-dropdown-item>

            <b-dropdown-item
                v-if="notification.category !== 'ARCHIVED'"
                @click.native.stop="updateNotificationCategory(notification, 'ARCHIVED')" id="archive-notification-btn">
              <b-icon
                  icon="archive-arrow-up"
                  size="is-small"
              />
              Archive
            </b-dropdown-item>

            <b-dropdown-item @click.native.stop="deleteNotificationFromPanel(notification)" id="delete-notification-btn">
              <b-icon
                  icon="close"
                  size="is-small"
              />
              Delete
            </b-dropdown-item>

            <hr class="dropdown-divider">

            <b-dropdown-item style="padding: 0.375rem 0 0.375rem 1rem">
              <b-dropdown  position="is-bottom-left" :triggers="['hover']" class="full-width">
                <template #trigger>
                  <b-dropdown-item paddingless>
                    <b-icon icon="tag" size="is-small"></b-icon>
                    Tag As
                    <b-icon icon="menu-down" size="is-small"></b-icon>
                  </b-dropdown-item>
                </template>

                <b-dropdown-item @click="addTag(notification,'Requires Attention')">
                  Requires Attention
                </b-dropdown-item>
                <b-dropdown-item @click="addTag(notification, 'High Priority')">
                  High Priority
                </b-dropdown-item>
                <b-dropdown-item @click="addTag(notification, 'Medium Priority')">
                  Medium Priority
                </b-dropdown-item>
                <b-dropdown-item @click="addTag(notification,'Low Priority')">
                  Low Priority
                </b-dropdown-item>
                <b-dropdown-item @click="addTag(notification,'Interesting')">
                  Interesting
                </b-dropdown-item>
              </b-dropdown>
            </b-dropdown-item>
          </b-dropdown>
        </div>
        <div v-else  style="margin-left: auto">
          <b-icon
              icon="delete-forever"
              @click.native.stop="deleteNotificationFromPanel(notification)"
              size="is-medium"
              type="is-danger"
              style="cursor: pointer;"
          ></b-icon>
        </div>
      </div>
      <b-field v-if="notification.message" :label="notification.type === 'COMMENT_RECEIVED' ||
      notification.type === 'GENERAL' ? 'Message' : 'Details'" label-position="on-border">
        <b-input class="notification-message" type="textarea" v-model="message" rows="3" readonly is-small/>
      </b-field>

      <div v-if="notification.type==='COMMENT_RECEIVED'">
        <SendMessageComponent v-if="more" :recipient-id="sender.id" :card-id="notification.cardId" :fromFeed="true"/>
        <div class="item-footer">
          <b-button :type="more ? 'is-danger' : 'is-info'" @click="more = !more">
            {{ more ? "Close" : "Reply" }}
          </b-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import error from "../../components/errorPopup";
import SendMessageComponent from "../../components/marketplace/SendMessageComponent";
import api from "../../api/api";
import logger from "../../logger";
import ListingPopUpComponent from "../../components/businesses/listings/listingsSearch/ListingPopUpComponent";

export default {
  name: "FeedItemComponent",
  props: ['notification'],
  data() {
    return {
      sender: null,
      more: false,
      message: "",
      updateTag: false,
      color: "",
      clickable: null,

      // Define a method for each card type
      notificationMethods: {
        "EXPIRING": this.redirectToCard,
        "LIKED": this.openListingModal,
        "COMMENT_RECEIVED": this.redirectToCard,
        "UNLIKED": this.openListingModal
      },

      isStarred: false,
    }
  },

  components: {
    SendMessageComponent
  },

  created() {
    this.onInitialize();
  },

  methods: {

    /**
     * Ran on creation of the component.
     * If the notification has a senderId then we send a request
     * to the server to get the senders details.
     */
    onInitialize() {
      this.getColourAndClickableFromType(this.notification);
      if (this.notification.type === 'COMMENT_RECEIVED' || this.notification.type === 'GENERAL') {
        this.message = this.formatMessage(this.notification.message)
      } else if (this.notification.type === "BOUGHT") {
        this.formatBoughtMessage(this.notification).then(res => {this.message = res});
      } else {
        this.message = this.notification.message;
      }

      if (this.notification.senderId !== undefined) {
        api.users.getUser(this.notification.senderId)
            .then(res => {
              this.sender = res.data;
            })
            .catch(err => {
              logger.getLogger().warn(err);
              error.showPopupFromServer(this, [err], false);
            });
      } else {
        this.sender = "No sender"
      }
    },


    /**
     * Makes certain parts of the purchase confirmation bold
     *
     * @param notification The message being formatted.
     */
    async formatBoughtMessage(notification) {

      let businessCurrency = await api.currency.getCurrentBusinessCurrency(notification.businessId);

      return notification.message + `\nPrice: ${businessCurrency.symbol}${notification.price} ${businessCurrency.code}`;
    },

    /**
     * Formats the notification message to get rid of the redundant part where it repeats
     * Who the user is that is sending the message.
     */
    formatMessage(message) {
      let splitMessage = message.split('\n');
      let result = "";
      if (splitMessage.length > 1) {
        for (let i = 1; i < splitMessage.length; i++) {
          result += splitMessage[i];
          if (i < splitMessage.length - 1) {
            result += '\n';
          }
        }
        return result
      }
      return message
    },

    /**
     * Gets the class to use for the title and clickable depending on the type of card
     *
     * @param notification the notification
     */
    getColourAndClickableFromType(notification) {
      switch (notification.type) {
        case 'CARD_DELETED':
          this.color = 'feed-title-deleted';
          break;
        case 'EXPIRING':
          this.color = 'feed-title-expiring';
          this.clickable = true;
          break;
        case 'STARRED':
          this.color = 'feed-title-starred';
          break;
        case 'KEYWORD_ADDED':
          this.color = 'feed-title-keyword';
          break;
        case 'LIKED':
          if (notification.message.endsWith('has sold!')) {
            this.color = 'feed-title-liked-sold';
          } else {
            this.color = 'feed-title-liked';
            this.clickable = true;
          }
          break;
        case 'BOUGHT':
          this.color = 'feed-title-bought';
          break;
        case 'COMMENT_RECEIVED':
          this.color = 'feed-title-comment-received';
          this.clickable = true;
          break;
        case 'UNLIKED':
          this.color = 'feed-title-unliked';
          this.clickable = true;
          break;
        default:
          this.color = 'feed-title-general';
      }
    },

    /**
     * Called when a feed item is clicked
     */
    onClick(notification) {
      if (this.notificationMethods[notification.type]) {
        this.notificationMethods[notification.type](notification);
      }
    },

    /**
     * Add a tag to a notification
     *
     * @param notification The notification object
     * @param type the type of tag to add, string
     */
    addTag(notification, type) {
      api.notifications.updateTag(notification.id, type)
          .then(() => {
            //On success update this notifications tag so we dynamically add it without having to query the backend, null = tag removed
            this.$emit("addTag", notification, type);
            this.updateTag = !this.updateTag;
          })
          .catch(err => {
            logger.getLogger().warn(err);
            error.showPopup(this, ["Unable to update tag for this notification."]);
          });
    },

    /**
     * Redirects the user to the card section of the profile page when clicking on an expiry warning notification
     * but only if it is an expiring card.
     *
     * @param notification the notification relating to the expiring card
     */
    redirectToCard(notification) {
      this.$router.push({name: `profile`, query: { tab: "2", "cardId": notification.cardId }});
    },

    /**
     * Open a modal that shows further details about the listing
     * @param notification information of listing to show
     */
    openListingModal(notification) {
      if (notification.listingId) {
        this.$buefy.modal.open({
          parent: this,
          component: ListingPopUpComponent,
          props: { listingId: notification.listingId, actingAs: localStorage.getItem('user-type') },
          trapFocus: true,
        });
      }
    },

    /**
     * Delete the users notification
     *
     * @param notification the notification to delete
     */
    deleteNotificationFromPanel(notification) {
      api.notifications.deleteNotification(notification.id)
          .then((res) => {
            notification.fullTitle = this.getFullTitle();
            if (notification.category === "ARCHIVED") {
              this.$emit("removeArchived", {notification: notification, cacheId: res.data.id})
            } else {
              this.$emit("removeNotification", {notification: notification, cacheId: res.data.id});
            }
          })
          .catch(err => {
            logger.getLogger().error(err);
            error.showPopup(this, ["Unable to delete notification."]);
          })
    },

    /**
     * Get title from notification type, uses javascript conditional (same as an if then else)
     *
     * @param notification the notification that we are returning the type of
     */
    getTitle(notification) {
      const types = {
        "CARD_DELETED": "Card Deleted",
        "EXPIRING": "Card About To Expire",
        "STARRED": "Card Starred",
        "KEYWORD_ADDED": "New Keyword Added",
        "LIKED": "Listing Like Status",
        "BOUGHT": "Listing Purchased",
        "COMMENT_RECEIVED": `Message from ${this.sender.firstName} ${this.sender.lastName}`
      }

      if (notification.type in types) {
        return types[notification.type];
      }
      return "Notification";
    },

    /**
     * Get colour type attribute from a notifications tag name
     *
     * @param tag Tag name as a string
     */
    getTypeFromTag(tag) {
      const styles = {
        "High Priority": "is-warning",
        "Medium Priority": "is-link",
        "Low Priority": "is-primary",
        "Interesting": "is-info",
        "Requires Attention": "is-danger",
      }

      if (tag in styles) {
        return styles[tag];
      }

      return "is-dark";
    },

    /**
     * Returns full title (title + date created) of this notification
     * @return string of full title
     */
    getFullTitle() {
      return `${this.getTitle(this.notification)} - ${new Date(this.notification.created).toLocaleDateString('en-GB')}`;
    },

    /**
     * Delete keyword, no need to call API to delete corresponding
     * notification on the backend as this is handled automatically.
     *
     * @param id ID of keyword
     */
    deleteKeyword(id) {
      api.keywords.deleteKeyword(id)
          .then(() => {
            this.$emit("removeNotification", this.notification);
          })
          .catch(err => {
            logger.getLogger().error(err);
            error.showPopup(this, ["Unable to delete keyword."]);
          });
    },

    /**
     * Stars a specific notification
     *
     * @param notification a specific notification object
     */
    starFeedItem(notification) {
      let category = notification.category === "STARRED" ? "READ" : "STARRED";
      api.notifications.updateCategory(notification.id, category)
          .then(() => {
            this.$emit("updateCategory", this.notification, category);
            this.updateTag = !this.updateTag;
          })
          .catch(err => {
            logger.getLogger().error(err);
            error.showPopup(this, ["Unable to update this notification"]);
          });
    },

    updateNotificationCategory(notification, category) {
      api.notifications.updateCategory(notification.id, category)
          .then(() => {
            if (category === 'ARCHIVED') {
              this.$emit("removeNotification", {notification: notification, action: 'archiving'});
              this.$buefy.toast.open({
                duration: 5000,
                message: `You have successfully archived the notification`,
                type: 'is-success',
              })
            }
            this.$emit("updateCategory", this.notification, category);
          })
          .catch(err => {
            logger.getLogger().error(err);
            error.showPopup(this, ["Unable to update this notification"]);
          });
    }
  }
}
</script>

