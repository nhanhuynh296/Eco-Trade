<template>
    <div v-if="ready" class="tile is-ancestor card-popup-main-container">
      <div class="tile is-vertical card-popup-container has-background-white">
        <div class="panel is-primary">
          <button type="button" style="float:right; margin-right: 2%; margin-top: 1%" class="delete is-large" v-on:click="onExit">Close</button>
          <p class="panel-heading"> {{ details.title }}</p>
        </div>

        <div class="tile">
          <!--          left-->
          <div class="tile is-vertical is-parent is-8">
            <div class="tile is-child panel is-info">
              <div class="title panel-heading title is-5"> Description</div>
              <div class="card-description panel-block">{{ details.description }}</div>
            </div>

            <div class="tile is-child panel is-success">
              <p class="panel-heading title is-5">Question</p>
              <div class="modal-card-foot1 cm-card-footer">
                <div class="full-width">
                  <SendMessageComponent :card-id="cardId" :recipient-id="details.creator.id"/>
                </div>
              </div>
            </div>
          </div>
          <!--          right-->
          <div class="tile is-vertical is-parent">

            <div class="tile is-child panel is-info">
              <div class="panel-heading">
                <span class="">Details</span>
              </div>

              <p class="detail-buttons panel-block">
                <b-tooltip :label="`Extend card to ${getExtendedDate()}`" type="is-success" v-if="show && dateDiff < 2">
                  <b-button icon-left="restore-alert" type="is-small is-success" v-if="show && dateDiff < 2"
                            class="card-popup-date-button full-width" label="Extend Card"
                            @click="renewCard">
                  </b-button>
                </b-tooltip>
                <b-button icon-left="alert-circle" type="is-small is-danger" v-if="show"
                          class="card-popup-date-button" label="Delete Card"
                          @click="deleteCard">
                </b-button>
              </p>

              <div class="panel-block detail-content">
                <div>
                  <b-icon icon="map-marker" size="is-small"></b-icon>
                  {{
                    `${details.creator.homeAddress.country}${(details.creator.homeAddress.region) ? ', ' + details.creator.homeAddress.region: ''}${(details.creator.homeAddress.city) ? ', ' + details.creator.homeAddress.city : ''}`
                  }}
                </div>

                <div>
                  <b-icon icon="calendar-plus" size="is-small"></b-icon>
                  Created: {{ new Date(details.created).toLocaleDateString('en-GB') }}
                </div>

                <div v-if="show">
                  <div v-if="dateDiff > 1">
                    <span>
                      <b-icon icon="calendar-remove" size="is-small"></b-icon>
                      Closes: {{ new Date(details.displayPeriodEnd).toLocaleDateString('en-GB') || 'N/A' }}
                    </span>
                  </div>

                  <div class="card-popup-date" v-if="dateDiff < 2">
                    <div class="card-popup-date-text">
                      <p>
                        <b-icon icon="calendar-remove" size="is-small"></b-icon>
                        Closes: {{ new Date(details.displayPeriodEnd).toLocaleDateString('en-GB') || 'N/A' }}
                      </p>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="tile is-child panel is-warning">
              <p class="panel-heading title is-5">Keywords</p>
              <b-taglist class="panel-block">
                <b-tag v-for="item in keywordList" :key="item.name"
                >
                  <b-icon icon="tag" size="is-small"></b-icon>
                  {{ item.name }}
                </b-tag>
              </b-taglist>

            </div>
            <div class="tile is-child panel is-dark">
              <p class="panel-heading title is-5">Seller Information</p>
              <div class="card-creator-wrapper">
                <b-image class="profile-thumbnail" alt="Profile image" rounded
                         :src="getUserThumbnailLink(details.id, details.creator.primaryImageId)"
                         @error="getAltImage" style="cursor: pointer; height:64px; width:64px"/>
                <div class="card-creator">
                  <div class="card-creator-name" v-on:click="routeToProfile()">
                    <p class="user-name-span">{{ details.creator.firstName }} {{ details.creator.lastName }}</p>
                    <b-icon icon="launch" size="is-small"/>
                  </div>
                  <p><small>Member since {{ new Date(details.creator.created).toLocaleDateString('en-GB') }}</small></p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
</template>

<script>
import api from "../../api/api";
import logger from "../../logger";
import errorPopup from "../../components/errorPopup";
import SendMessageComponent from "../../components/marketplace/SendMessageComponent";
import userProfile from "../users/userProfile";

export default {
  name: "CardPopUpComponent",
  components: {SendMessageComponent},
  props: ['cardId'],

  data() {
    return {
      keywordList: [],
      details: null,
      ready: false,
      dateDiff: 0,
      cardCreatorId: null,
      show: false,
    }
  },

  mounted() {
    this.getDetails();
  },

  methods: {
    /**
     * Get the specific card details from backend API
     */
    getDetails() {
      api.cards.getCard(this.cardId)
          .then(res => {
            this.details = res.data;
            this.keywordList = res.data.keywords;
            this.ready = true;

            this.dateDiff = Math.ceil(Math.abs(new Date(this.details.displayPeriodEnd) - new Date()) / (1000 * 60 * 60 * 24));

            this.cardCreatorId = this.details.creator.id;

            let userId = parseInt(localStorage.getItem('id'));
            let userRole = localStorage.getItem('user-role');

            this.show = userId === this.cardCreatorId || userRole === "ROLE_DEFAULT_ADMIN" || userRole === "ROLE_ADMIN";

          }).catch(err => {
            logger.getLogger().warn(err);
            errorPopup.showPopup(this, ['That card no longer exists']);
            this.$emit("refreshTable");
            this.$emit("close");
      });
    },

    /**
     * Delete the current card
     */
    deleteCard() {
      api.cards.deleteCard(this.cardId)
          .then(() => {
            this.$emit("refreshTable");
            this.$emit("close");
          })
          .catch(err => {
            logger.getLogger().error(err);
            errorPopup.showPopup(this, ['Unable to delete card']);
          });
    },

    /**
     * Renew the current card
     */
    renewCard() {
      api.cards.renewCard(this.cardId)
          .then(() => {
            this.$emit("refreshTable");
            this.$emit("close");
            errorPopup.showPopup(this, [`Card '${this.details.title}' renewed successfully.<br> New closing date: ${this.getExtendedDate()}`])
          })
          .catch(err => {
            logger.getLogger().error(err);
            errorPopup.showPopup(this, ['Unable to renew card']);
          });
    },


    /**
     * Route to the card creator's profile
     */
    routeToProfile() {
      this.$emit('close');
      this.$router.push({name: 'userProfile', params: {userId: this.details.creator.id}})
    },

    /**
     * Close the modal
     */
    onExit() {
      this.$emit('close');
    },

    /**
     * Takes the primary id of a profile image and returns a link to an existing image.
     * If there is no image (primary id is null) a default image is sent.
     */
    getUserThumbnailLink(userId, primaryId) {
      return userProfile.getUserImageLink(userId, primaryId, true);
    },

    /**
     * If there's an error this function is called and sets the
     * src of the target html event to the default image.
     *
     * @param event
     */
    getAltImage(event) {
      event.target.src = "https://bulma.io/images/placeholders/128x128.png";
    },

    /**
     * Get date two weeks from now in dd/mm/yyyy format
     */
    getExtendedDate() {
      let date = new Date();
      date.setDate(date.getDate() + 14);
      return `${date.getUTCDate()}/${date.getMonth() + 1}/${date.getFullYear()}`;
    }
  }
}
</script>

<style scoped>

.detail-content {
  overflow: auto;
  flex-direction: column;
  flex-wrap: wrap;
  align-items: flex-start;
}

.detail-buttons {
  flex-flow: row wrap;
  justify-content: space-evenly;
}

.detail-buttons > * {
  flex: 1 auto;
}

</style>
