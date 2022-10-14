<template>
  <div class="tile is-ancestor market-section-container">
    <div class="tile">
      <div class="tile is-parent is-vertical is-3">
        <div class="tile is-child filter-container">
          <b-button class="filter-container-item is-success" label="Add"
                    @click="createCardModal(section)" icon-left="clipboard-plus"/>
          <b-dropdown expanded>
            <template #trigger="{ active }">
              <b-button
                  type="is-primary"
                  :icon-right="active ? 'menu-up' : 'menu-down'"
                  class="sort-dropdown-button"
              >
                {{ sortByName }}
              </b-button>
            </template>

            <b-dropdown-item class="filter-container-item" v-for="item in sortByOptions" :key="item.paramName"
                             @click.native="sortCards(item)">
              {{ item.name }}
            </b-dropdown-item>
          </b-dropdown>

          <hr>

          <div class="keyword-search-container">
            <p class="for-sale-filter-title">Keywords</p>

            <b-field>
              <b-taginput
                  type="search"
                  v-model="keywords"
                  ellipsis
                  :data="keywordsList"
                  @typing="getFilteredTags"
                  open-on-focus
                  autocomplete
                  ref="taginput"
                  :has-counter="false"
                  icon="filter"
                  placeholder="Search"
                  class="search-keyword-input"
              >
                <template #selected="props">
                  <b-tag
                      v-for="(tag, index) in props.tags"
                      :key="index"
                      type="is-primary"
                      ellipsis
                      closable
                      @close="$refs.taginput.removeTag(index, $event)">
                    {{ tag }}
                  </b-tag>
                </template>
              </b-taginput>
              <b-button type="is-danger" icon-right="close-circle" v-on:click="clearSearch"/>
            </b-field>

            <div class="keyword-toggle-wrapper filter-container-item">
              <p id="match-any">Any</p>
              <b-switch
                  v-model="isMatchAll"
                  :rounded="true"
                  type="is-primary"
                  passive-type="is-primary">
              </b-switch>
              <p id="match-any">All</p>
            </div>

            <hr>

            <p class="for-sale-filter-title">Location</p>
            <b-field label="Country" label-position="on-border" >
              <AutofillInputComponent ref="countryInput" type="text" v-model="country" :input-list-data=countryList do-filter="true"/>
            </b-field>

            <b-field label="Region" label-position="on-border">
              <AutofillInputComponent type="text" v-model="region"
                                      ref="regionInput"
                                      :input-list-data=queryResults
                                      @input="queryAutofillRegions()"
                                      @focus="queryAutofillRegions()"
                                      do-filter="true"/>
            </b-field>

            <b-field label="City/Town" label-position="on-border">
              <AutofillInputComponent type="text" v-model="city"
                                      ref="cityInput"
                                      :input-list-data=queryResults
                                      @input="queryAutofillCities()"
                                      @focus="queryAutofillCities()"
                                      do-filter="true"
              />
            </b-field>
            <b-button class="button location-clear-btn" type="is-danger" label="Clear" icon-left="close" @click="clearAllFields()"></b-button>
            <hr>
            <b-button class="marketplace-sale-apply-filter" type="is-primary" @click="filterCards(1)">Search</b-button>

          </div>
        </div>
      </div>

      <div class="tile is-parent is-vertical">
        <div class="tile is-child">
          <div class="market-cards-container">
            <div v-bind:key="card.name"
                 v-for="card in cards"
                 class="marketplace-cards"
                 v-on:click="showCardModal(card)"
            >
              <CardComponent :card-details="card" :modifiable="isCardOwnerOrAdmin(card.creator.id)"/>
            </div>
          </div>
        </div>
        <div class="pagination-info-center">
          <div class="marketplace-sale-pagination">
            <b-pagination class="marketplace-pagination-buttons"
                          backend-pagination
                          :total="paginationInfo.totalElements"
                          v-model="current"
                          order="is-centered"
                          :simple="false"
                          :rounded=true
                          :per-page="perPage"
                          :current.sync="current"
                          @change="paginateItems"
                          icon-next="chevron-right"
                          icon-prev="chevron-left">
            </b-pagination>
            Showing {{lowerCount}} - {{upperCount}} of {{paginationInfo.totalElements}} results
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import CreateEditCardComponent from "../../../src/components/marketplace/CreateEditCardComponent";
import api from "../../api/api";
import errorPopup from "../../components/errorPopup";
import logger from "../../logger";
import CardComponent from "../../components/marketplace/CardComponent";
import CardPopUpComponent from "../../components/marketplace/CardPopUpComponent";
import countryList from "../../api/photon/countries";
import AutofillInputComponent from "../../components/autofill/AutofillInputComponent";
import autofillQuery from "../../api/photon/autofillQuery";
import paginationInfo from "../../components/paginationInfo";

const MarketSectionComponent = {
  name: "MarketSectionComponent",
  components: {AutofillInputComponent, CardComponent},
  props: ["section"],

  data() {
    return {
      current: 1,
      perPage: 12,
      selectedFilterOptions: [],
      sortByOptions: [
        {name: 'Sort: New', paramName: 'DATE_DESC'},
        {name: 'Sort: Old', paramName: 'DATE_ASC'},
        {name: 'Sort: Name (A-Z)', paramName: 'TITLE_AZ'},
        {name: 'Sort: Name (Z-A)', paramName: 'TITLE_ZA'},
      ],
      sortBy: 'DATE_DESC',
      sortByName: 'Sort: New',
      cards: [],
      paginationInfo: {
        totalPages: 0,
        totalElements: 0
      },
      countryList,
      country: "",
      region: "",
      city: "",
      keywords: [],
      keywordsList: [],
      isMatchAll: false,
      queryResults: [],
      lowerCount: 0,
      upperCount: 0,
    }
  },

  created() {
    this.init();
  },

  methods: {
    init() {
      this.cards = [];
      this.filterCards(this.current);
      this.getFilteredTags('');
    },

    /**
     * Breaks up the list of items to fit into the pagination
     *
     * @return {({name: string}|{name: string}|{name: string}|{name: string}|{name: string})[]}
     */
    paginateItems() {
      this.filterCards(this.current);
    },

    /**
     * Calls the api endpoint for searching cards by the filtered fields
     */
    filterCards(page) {
      let matchType = this.isMatchAll ? "and" : "or";
      api.cards.searchCards(this.keywords, matchType, this.section, this.sortBy, this.country, this.region, this.city, page, this.perPage)
          .then(res => {
            this.current = page;
            this.cards = res.data["paginationElements"];
            this.paginationInfo.totalPages = res.data["totalPages"];
            this.paginationInfo.totalElements = res.data["totalElements"];
            if (this.paginationInfo.totalElements !== 0) {
              let bounds = paginationInfo.getPageInfo(page, this.perPage, this.paginationInfo.totalElements);
              this.lowerCount = bounds[0]
              this.upperCount = bounds[1]
            }
          })
          .catch((error) => {
            logger.getLogger().warn(error);
            errorPopup.showPopup(this, ['Unable to get cards']);
          });

    },

    /**
     * Clears the search
     * Calls for all cards
     */
    clearSearch() {
      this.keywords = [];
      this.filterCards(1);
    },

    /**
     * Get a sorted, paginated list of cards from backend based on the option selection from the dropdown
     */
    sortCards(option) {
      this.sortByName = option.name;
      this.sortBy = option.paramName;
      this.init();
    },

    /**
     * Initializes the create card modal and opens it
     */
    createCardModal() {
      this.$buefy.modal.open({
        parent: this,
        component: CreateEditCardComponent,
        hasModalCard: true,
        props: {section: this.section},
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
     * Open a modal that shows further details about the card
     * @param cardInfo information of card to show
     */
    showCardModal(cardInfo) {
      this.$buefy.modal.open({
        parent: this,
        component: CardPopUpComponent,
        props: {cardId: cardInfo.id},
        trapFocus: true,
        events: {
          'refreshTable': () => {
            this.init();
          }
        }
      });
    },

    /**
     * Querying regions in a country
     */
    queryAutofillRegions() {
      return autofillQuery.queryRegions(this.country, this.region)
          .then(result => this.queryResults = result);
    },

    /**
     * Querying cities in a region in a country
     */
    queryAutofillCities() {
      return autofillQuery.queryCities(this.country, this.region, this.city)
          .then(result => this.queryResults = result);
    },

    /**
     * Resets the filter fields
     */
    clearAllFields() {
      this.$refs.countryInput.input = "";
      this.$refs.regionInput.input = "";
      this.$refs.cityInput.input = "";
      this.country = "";
      this.region = "";
      this.region = "";
      this.clearSearch()
    },

    /**
     * Only show delete button if the user is either the card creator, GAA, or DGAA
     */
    isCardOwnerOrAdmin(creatorId) {
      if (localStorage.getItem('role') === "ROLE_ADMIN" || localStorage.getItem('role') === "ROLE_DEFAULT_ADMIN") {
        return true;
      }
      return parseInt(localStorage.getItem('id')) === creatorId;
    },

    /**
     * Get keywords filtered by text
     * @param text to filer by
     */
    getFilteredTags(text) {
      api.keywords.getKeywords(text)
          .then(res => {
            this.keywordsList = res.data.map(x => x.name);
          });
    },

    /**
     * Show toast displaying success
     */
    notifySuccess(message) {
      this.$buefy.toast.open({
        message: message,
        type: 'is-success'
      })
    }
  },
  watch: {
    /**
     * Monitors for change in boolean isMatchAll
     * Calls searchCards when the boolean is modified
     */
    isMatchAll() {
      this.filterCards(1);
    }
  }
};

export default MarketSectionComponent;
</script>
