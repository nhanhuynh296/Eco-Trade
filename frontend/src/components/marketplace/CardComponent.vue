<template>
  <div class="card market-card-container" style="background-color: #E7E9E4">
    <div class="card-btns" v-on:click.stop v-if="modifiable">
      <b-button id="edit-btn" class="edit-btn" @click="editCardModal" icon-left="pencil" type="is-info">
        Edit
      </b-button>
      <b-button id="delete-btn" class="delete-btn" @click="deleteCard" icon-left="delete" type="is-danger">
        Delete
      </b-button>
    </div>
    <br v-if="!modifiable">

    <div class="card-content pt-0">
      <div class="media-content">
        <p class="marketplace-sale-item-title">{{ cardDetails.title }}</p>
      </div>
      <hr style="margin-block: 0.2rem">
      <div class="content card-description">
        {{ cardDetails.description }}
      </div>
    </div>
    <div class="card-footer" style="bottom: 0; position: absolute; width: 100%">
      <b-button id="renew-btn" class="renew-btn" v-if="modifiable && dateDiff < 2" @click.stop="renewCard" icon-left="refresh" type="is-success">
        Extend
      </b-button>
    </div>
  </div>
</template>
<script>
import api from "../../../src/api/api";
import logger from "../../logger";
import errorPopup from "../../../src/components/errorPopup";
import CreateEditCardComponent from "../../../src/components/marketplace/CreateEditCardComponent";

export default {
  name: "CardComponent",
  props: ['cardDetails', 'modifiable'],

  data() {
    return {
      creatorId: this.cardDetails.creator.id,
      cardId: this.cardDetails.id,
      dateDiff: Math.ceil(Math.abs(new Date(this.cardDetails.displayPeriodEnd) - new Date()) / (1000 * 60 * 60 * 24)),
    }
  },

  methods: {
    /**
     * Delete the current card
     */
    deleteCard() {
      api.cards.deleteCard(this.cardId)
          .then(() => {
            this.init();
            this.notifySuccess('Card deleted successfully!')
          })
          .catch(err => {
            logger.getLogger().error(err);
            errorPopup.showPopup(this, ['Unable to delete card'])
          })
    },

    init() {
      this.$parent.init();
    },

    /**
     * Renew the current card
     */
    renewCard() {
      api.cards.renewCard(this.cardId)
          .then(() => {
            this.init();
            let newDate = new Date();
            newDate.setDate(newDate.getDate() + 14);
            let dateString = `${new Date(newDate).toLocaleDateString('en-GB')}`;
            this.notifySuccess(`Card '${this.cardDetails.title}' renewed successfully.<br> New closing date: ${dateString}`);
          })
          .catch(err => {
            logger.getLogger().error(err);
            errorPopup.showPopup(this, ['Unable to renew card'])
          })
    },

    /**
     * Edit the current card
     */
    editCardModal() {
      this.$buefy.modal.open({
        parent: this,
        component: CreateEditCardComponent,
        hasModalCard: true,
        props: {section: this.cardDetails.section, editing: true, editValues: this.cardDetails},
        customClass: 'custom-class custom-class-2',
        trapFocus: true,
        events: {
          'refreshTable': () => {
            this.init();
          }
        }
      });
      this.current = 1;
    },

    /**
     * Show toast displaying success
     */
    notifySuccess(message) {
      errorPopup.showPopup(this, [message])
    }
  }
}
</script>
