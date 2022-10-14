<template>
  <form action="">
    <div class="modal-card" style="margin: auto">
      <header class="modal-card-head panel-heading-green">
        <p class="modal-card-title card-create-title" id="create-card-header">{{ cardHeader }}</p>
        <button
            type="button"
            class="delete"
            @click="$emit('close')"/>
      </header>

      <section class="modal-card-body card-create-form" @submit.prevent="createCard">
        <div class="card-create-required-fields-key">
          Required fields are marked with *
        </div>

        <div class="card-create-info">
          <b-field label="Title*" label-position="on-border">
            <b-input maxlength="70" label = "Title*" v-model="title" required/>
          </b-field>

          <b-field label="Description" label-position="on-border">
            <b-input maxlength="200" type="textarea" v-model="description" placeholder="About the item..."/>
          </b-field>

          <b-field label="Add some keywords (type and then press enter)" label-position="on-border">
            <b-taginput
                v-model="keywords"
                ellipsis
                :data="keywordsList"
                @typing="getFilteredTags"
                autocomplete
                open-on-focus
                allow-new
                maxlength="10"
                icon="label"
                placeholder="Add a keyword..."
                aria-close-label="Delete this keyword">
            </b-taginput>
          </b-field>
        </div>
      </section>

      <footer class="modal-card-foot card-create-button">
        <b-button type="submit is-info" @click="$emit('close')">Cancel</b-button>
        <b-button type="submit is-success" @click="createCard" v-if="!editing">Add Item</b-button>
        <b-button type="submit is-success" @click="editCard" v-if="editing">Edit</b-button>
      </footer>
    </div>
  </form>
</template>

<script>
import validateCardDetails from "../../components/marketplace/validateCardDetails";
import errorPopup from "../../components/errorPopup";
import api from "../../api/api";
import logger from "../../logger";

export default {
  name: "CreateEditCardComponent",
  props: ["section", "editing", "editValues"],
  data() {
    return {
      creatorId: null,
      cardHeader: null,
      title: '',
      description: '',
      keywords: [],
      keywordsList: [],
      valid: false
    }
  },

  created() {
    this.creatorId = localStorage.getItem("id");
    this.setCardHeader();
    this.getFilteredTags();
  },

  methods: {

    /**
     * Sets the title and any existing information if the card is being edited
     */
    setCardHeader() {
      if (this.editing === true) {
        this.setEditDetails();
      } else {
        this.cardHeader = this.section === "ForSale" ? "For Sale" : this.section;
        this.cardHeader = "Create " + this.cardHeader + " Card";
      }
    },

    /**
     * Populates the text entries with the original information when editing a card
     */
    setEditDetails() {
      this.cardHeader = "Edit Card";
      this.title = this.editValues.title;
      this.description = this.editValues.description;
      for (let index in this.editValues.keywords) {
        this.keywords.push(this.editValues.keywords[index].name)
      }
    },

    /**
     * Validate and if successful send the payload to the API spec
     */
    createCard() {
      let errors = validateCardDetails.validateCardDetails(this.section, this.title, false);
      if (errors.length > 0) {
        errorPopup.showPopup(this, errors);
      } else {
        logger.getLogger().info(`Adding card to section: ${this.section}`);
        let payload = {
          creatorId: parseInt(this.creatorId),
          section: this.section.replace(' ', ''),
          title: this.title,
          description: this.description,
          keywords: this.keywords,
        }
        api.cards.createNewCard(payload)
            .then(() => {
              this.$emit('refreshTable');
              this.$emit('close');
            })
        .catch(error => {
          logger.getLogger().warn(error);
          errorPopup.showPopupFromServer(this, errors, false);
        })
      }
    },

    /**
     * Validate and if successful send the payload to the API spec
     */
    editCard() {
      let errors = validateCardDetails.validateCardDetails(this.section, this.title, true);
      if (errors.length > 0) {
        errorPopup.showPopup(this, errors);
      } else {
        logger.getLogger().info(`Editing card: ${this.editValues.id}`);
        let payload = {
          creatorId: parseInt(this.creatorId),
          section: this.section.replace(' ', ''),
          title: this.title,
          description: this.description,
          keywords: this.keywords,
        }
        api.cards.updateCard(this.editValues.id, payload)
          .then(() => {
            this.$emit('refreshTable');
            this.$emit('close');
          })
          .catch(error => {
            logger.getLogger().warn(error);
            errorPopup.showPopupFromServer(this, errors, false);
          })
      }
    },

    /**
     * Get keywords filtered by text
     * @param text to filer by
     */
    getFilteredTags(text) {
      api.keywords.getKeywords(text)
      .then(res => {
        this.keywordsList = res.data.map(x => x.name);
      })
    },

  }
}
</script>

<style scoped>
@import '../../../public/styles/marketplace.css';
</style>
