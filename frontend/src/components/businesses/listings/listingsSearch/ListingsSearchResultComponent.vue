<template>
  <div class="listings-search-main">
    <div class="listing-search-main-container">
      <div class="tile is-ancestor">
        <div class="tile is-vertical">
          <div class="tile">
            <div class="tile is-parent is-vertical is-3">
              <div class="tile is-child panel white-background is-primary">
                <p class="title panel-heading">Filters</p>

                <div class="content">
                  <div class="listings-search-filter-container">

                    <p class="for-sale-filter-title">Product Search</p>

                    <b-field class="control" label="Search by product name" label-position="on-border">
                      <p class="control">
                        <b-tooltip position="is-bottom" size="is-large" multilined>
                          <b-input type="search" @keyup.enter="constructFullSearch(1)" @icon-click="constructFullSearch(1)"
                                   v-model="searchQuery" placeholder="" icon="magnify"/>

                          <template v-slot:content>
                            Combine <strong style="color: white">AND</strong> & <strong style="color: white">OR</strong>
                            terms within your search for more advanced searching. <br/> <strong style="color: white">"your search"</strong> provides exact result.
                          </template>
                        </b-tooltip>
                      </p>

                      <p class="control">
                        <b-button id="listings-search-btn" type="is-primary" label="Search" @click="constructFullSearch(1)"></b-button>
                      </p>
                    </b-field>

                    <hr/>

                    <p class="for-sale-filter-title">Sort By</p>

                    <b-dropdown aria-role="list" class="marketplace-sale-filter-dropdown" style="width: 100%">
                      <template #trigger="{ active }">
                        <b-button
                            type="is-primary"
                            :icon-right="active ? 'menu-up' : 'menu-down'"
                            class="marketplace-sale-filter-dropdown-button"
                        >
                          {{ sortByName }}
                        </b-button>
                      </template>

                      <b-dropdown-item aria-role="menuitem" v-for="item in sortByOptions" :key="item.paramName"
                                       v-on:click="sortListings(item)">
                        {{ item.name }}
                      </b-dropdown-item>
                    </b-dropdown>

                    <hr/>

                    <p class="for-sale-filter-title" style="font-size: large">Filter Options</p>

                    <b-field label="Price Range $$ - MIN">
                      <b-numberinput v-model="priceRange[0]" type="is-info" :min="0" controls-position="compact" exponential	controls-rounded></b-numberinput>
                    </b-field>
                    <b-field label="Price Range $$ - MAX">
                      <b-numberinput v-model="priceRange[1]" type="is-info" :min="0" controls-position="compact" exponential controls-rounded></b-numberinput>
                    </b-field>

                    <b-field label="Select closing date">
                      <b-datepicker
                          :first-day-of-week="1"
                          icon="calendar-today" trap-focus required
                          placeholder="Click to select closing date..."
                          :date-formatter="formatter"
                          v-model="closingDate"
                          :min-date="minDatetime">
                      </b-datepicker>
                    </b-field>

                    <b-collapse class="card" animation="slide" aria-id="contentIdForA11y3" :open="false">
                      <template #trigger="props">
                        <div
                            class="card-header location-filter-header-wrapper"
                            role="button"
                            aria-controls="contentIdForA11y3">
                          <p class="card-header-title location-filter-header-title">
                            Business
                          </p>
                          <a class="card-header-icon">
                            <b-icon
                                :icon="props.open ? 'menu-up' : 'menu-down'">
                            </b-icon>
                          </a>
                        </div>
                      </template>

                      <div class="card-content">
                        <b-field>
                          <b-dropdown v-model="businessType" multiple style="width: 100%">

                            <template #trigger>
                              <b-button icon-right="menu-down" type="is-primary is-light">
                                Business Types Selected ({{ businessType.length }})
                              </b-button>
                            </template>

                            <b-dropdown-item value="Accommodation">Accommodation</b-dropdown-item>
                            <b-dropdown-item value="Food Services">Food Services</b-dropdown-item>
                            <b-dropdown-item value="Retail Trade">Retail Trade</b-dropdown-item>
                            <b-dropdown-item value="Charitable organisation">Charitable organisation</b-dropdown-item>
                            <b-dropdown-item value="Non-profit organisation">Non-profit organisation</b-dropdown-item>
                          </b-dropdown>
                        </b-field>

                        <b-field label="Business Name" label-position="on-border">
                          <b-input v-model="businessesName"/>
                        </b-field>

                        <b-field label="Country" label-position="on-border">
                          <AutofillInputComponent ref="countryInput" type="text" v-model="country" :input-list-data=countryList do-filter="true"/>
                        </b-field>

                        <b-field label="Region" label-position="on-border">
                          <AutofillInputComponent type="text" v-model="region"
                                                  :disabled="country.length < 1"
                                                  ref="regionInput"
                                                  :input-list-data=queryResults
                                                  @input="queryAutofillRegions()"
                                                  @focus="queryAutofillRegions()"
                                                  do-filter="true"/>
                        </b-field>

                        <b-field label="City/Town" label-position="on-border" class="register-city">
                          <AutofillInputComponent type="text" v-model="city"
                                                  :disabled="region.length < 1"
                                                  ref="cityInput"
                                                  :input-list-data=queryResults
                                                  @input="queryAutofillCities()"
                                                  @focus="queryAutofillCities()"
                                                  do-filter="true"
                          />
                        </b-field>
                      </div>
                    </b-collapse>

                    <br/>

                    <b-field>
                      <b-button id="listings-search-clear-filter-btn" class="button location-clear-btn" label="Clear Filters" icon-left="close" @click="clearBusinessFilter()"></b-button>
                    </b-field>

                    <hr>

                    <b-field>
                      <b-button id="listings-search-apply-filter-btn" class="listings-search-apply-button" v-on:click="constructFullSearch(1)" label="Apply Filter"></b-button>
                    </b-field>
                  </div>
                </div>
              </div>
            </div>

            <div class="tile is-parent is-vertical">
              <div class="tile is-child panel is-info white-background is-flex is-flex-direction-column">
                <p class="title panel-heading">Listings</p>
                <b-table
                    class="listing-result-container is-flex-grow-1"
                    :data="listingsResultData"
                    ref="table"

                    striped
                    :show-header="false"

                    aria-next-label="Next page"
                    aria-previous-label="Previous page"
                    aria-page-label="Page"
                    aria-current-label="Current page"
                    focusable

                    @click="openListingModal($event)"
                >

                  <b-table-column field="image" v-slot="props">
                    <b-image :src="getProductThumbnailLink(props.row.inventoryItem.product.primaryImageId)" @error="getAltImage" style="cursor: pointer" class="image is-64x64"/>
                  </b-table-column>

                  <b-table-column field="description" label="Name" v-slot="props">
                    <p>
                      {{ props.row.inventoryItem.product.name }}
                    </p>
                    <p>
                      {{ props.row.quantity }} available at {{ props.row.currencySymbol }}{{ props.row.price }} {{ props.row.currencyCode }} each
                    </p>
                  </b-table-column>

                  <b-table-column field="business" label="Business" v-slot="props">
                    <p>
                      Sold By {{ props.row.business.name }}
                    </p>
                    <span class="tag px-1">
                      {{ props.row.business.address.country }}, {{ props.row.business.address.city }}
                    </span>
                  </b-table-column>

                  <b-table-column field="businessType" label="Type" sortable v-slot="props">
                    <div class="multiline-center">
                      <p>
                        <span class="tag is-success px-1" style="margin-bottom: 10%">
                          Created {{ new Date(props.row.created).toLocaleDateString('en-GB') }}
                        </span>

                        <span class="tag is-danger px-1" style="display: flex; flex-grow: 1">
                          Closes {{ new Date(props.row.closes).toLocaleDateString('en-GB') }}
                        </span>
                      </p>
                    </div>
                  </b-table-column>
                </b-table>

                <div class="tile is-child">
                  <div class="listing-pagination">
                    <b-pagination class="user-listing-pagination-buttons"
                                  backend-pagination
                                  :total="paginationInfo.totalElements"
                                  order="is-centered"
                                  size="is-small"
                                  :rounded="true"
                                  :per-page="perPage"
                                  :current.sync="page"
                                  v-model="page"
                                  @change="searchListings"
                                  icon-prev="chevron-left"
                                  icon-next="chevron-right">
                    </b-pagination>
                    Showing {{lowerCount}} - {{upperCount}} of {{paginationInfo.totalElements}} results
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import businesses from "../../../../components/businesses/businesses";
import countryList from "../../../../api/photon/countries";
import AutofillInputComponent from "../../../../components/autofill/AutofillInputComponent";
import ListingPopUpComponent from "../../../../components/businesses/listings/listingsSearch/ListingPopUpComponent";
import autofillQuery from "../../../../api/photon/autofillQuery";
import api from "../../../../api/api";
import errorPopup from "../../../../components/errorPopup";
import paginationInfo from "../../../../components/paginationInfo";
import currencyQuery from "../../../../api/restcountries/currencyQuery";

const ListingsSearchResultComponent = {
  name: "ListingsSearchResultComponent",
  components: {AutofillInputComponent, ListingPopUpComponent},

  data() {
    return {
      // Sorting Options
      selectedFilterOptions: [],
      sortByOptions: [
        {name: 'Newest listings', paramName: 'DATE_DESC'},
        {name: 'Oldest listings', paramName: 'DATE_ASC'},
        {name: 'Title (A-Z)', paramName: 'TITLE_AZ'},
        {name: 'Title (Z-A)', paramName: 'TITLE_ZA'},
      ],
      sortBy: 'DATE_DESC',
      sortByName: 'Newest listing',
      queryResults: [],

      // Filtering Options
      countryList,
      country: "",
      region: "",
      city: "",
      businessesName: null,
      businessType: [],
      priceRange: [0.00, 0.00],
      minDatetime: new Date(),
      closingDate: null,

      // Search
      searchQuery: "",
      fullSearch: "?search=",

      listingsResultData: [],

      page: 1,
      oldPage: 1,
      perPage: 8,
      paginationInfo: {
        totalPages: 0,
        totalElements: 0
      },
      lowerCount: 0,
      upperCount: 0,
    }
  },

  /**
   * Before component is loaded
   */
  created() {
    this.minDatetime.setDate(this.minDatetime.getDate() - 1)
    this.onInitialize();

    if (this.$route.query.listingId) {
      this.openListingModal({id: this.$route.query.listingId});
    }
  },

  methods: {

    /**
     * Calls the search function
     * Sets filters if they are in the params of the route
     */
    onInitialize() {
      let page = this.$route.query.page;

      if (page) {
        this.getSortOption();

        if (this.$route.params.query !== undefined) {
          this.searchQuery = this.$route.params.query;

          this.priceRange = this.getListFromParams(this.$route.params.setRange, "price");
          this.closingDate = this.$route.params.setClosingDate;
          this.businessesName = this.$route.params.setBusinessName;
          this.businessType = this.getListFromParams(this.$route.params.setBusinessType, "business");
          this.country = this.$route.params.setCountry;
          this.region = this.$route.params.setRegion;
          this.city = this.$route.params.setCity;
        }

        this.searchListings(page);
      } else {
        this.constructFullSearch(1);
      }
    },

    /**
     * Takes a business id and the primary id of a product image and returns the link to the image.
     */
    getProductThumbnailLink(primaryId) {
      return businesses.getListingImageLink(primaryId, true);
    },

    /**
     * Sets the sortByName by sortBy option
     */
    getSortOption() {
      this.sortBy = this.$route.query.sortBy;

      if (this.sortBy === 'DATE_DESC') {
        this.sortByName = 'Newest listings';
      }
      if (this.sortBy === 'DATE_ASC') {
        this.sortByName = 'Oldest listings';
      }
      if (this.sortBy === 'TITLE_AZ') {
        this.sortByName = 'Title (A-Z)';
      }
      if (this.sortBy === 'TITLE_ZA') {
        this.sortByName = 'Title (Z-A)';
      }
    },

    /**
     * Splits the string into a list by ','
     * If the type is price the sets each value to an int
     */
    getListFromParams(list, type) {
      let splitList = list ? list.split(',') : [] ;

      if (type === "price") {
        splitList[0] = parseInt(splitList[0]);
        splitList[1] = parseInt(splitList[1]);
      }

      return splitList;
    },

    /**
     * Converts the date chosen with datepicker to the local timezone and New Zealand format.
     */
    formatter (date) {
      return date.toLocaleDateString('en-GB');
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
     * Clears the location data and resets the searchQuery
     */
    clearBusinessFilter() {
      this.businessesName = null;
      this.businessType = [];
      this.closingDate = null;
      this.priceRange = [0, 0];
      this.country = "";
      this.region = "";
      this.city = "";

      this.constructFullSearch(1);
    },

    /**
     * Adds one day to a given date so the date stored in the database is correct (due to formatting differences
     * with JSON the day is one off if not adjusted as below). Ignores null dates.
     */
    returnCorrectDate(date) {
      // if null is not checked a default date is set
      if (date !== null) {
        const correctedDate = new Date(date);
        correctedDate.setDate(correctedDate.getDate() + 1);
        return correctedDate;
      }
    },

    /**
     * Sets the sortBy variable
     * Calls the searchListings method
     */
    sortListings(item) {
      this.sortByName = item.name;
      this.sortBy = item.paramName;
      this.searchListings(1);
    },

    /**
     * Calls appropriate functions that put together the fullSearch query
     * Calls the search method afterwords
     */
    constructFullSearch(page) {
      this.priceRange[0] = parseInt(this.priceRange[0]);
      this.priceRange[1] = parseInt(this.priceRange[1]);

      if (this.priceRange[0] >= 0 && this.priceRange[1] >= 0) {
        this.fullSearch = businesses.getListingsSearchQuery(this.searchQuery);

        if (this.businessesName) {
          this.fullSearch = businesses.addBusinessNameSearch(this.fullSearch, this.businessesName);
        }
        if (this.businessType[0] !== undefined) {
          this.fullSearch = businesses.addBusinessTypeSearch(this.fullSearch, this.businessType);
        }
        if (this.closingDate !== undefined && this.closingDate != null) {
          this.fullSearch = businesses.addClosingDateSearch(this.fullSearch, this.returnCorrectDate(this.closingDate));
        }

        this.fullSearch = businesses.addPriceRangeSearch(this.fullSearch, this.priceRange);
        this.fullSearch = businesses.addBusinessAddressSearch(this.fullSearch, this.country, this.region, this.city);

        this.$router.push({ name: "listingsSearch",
          query: {search: this.fullSearch.substr(8), page: page.toString(), size: this.perPage.toString(), sortBy: this.sortBy},
          params: {query: this.searchQuery, setRange: this.priceRange.toString(), setClosingDate: this.closingDate, setBusinessType:
                this.businessType.toString(), setBusinessName: this.businessesName, setCountry: this.country, setRegion: this.region, setCity: this.city} }).catch();

        this.searchListings(page);
      } else {
        this.priceRange = [0, 0];
      }
    },

    /**
     * Calls the api for searching listings
     * Uses specs for search query, passes the sortBy, page and total elements per page
     */
    searchListings(page) {
      page = page - 1;

      api.listing
          .searchListings(this.sortBy, this.$route.query.search, page, this.$route.query.size)
          .then(res => {
            this.listingsResultData = res.data.paginationElements;

            this.oldPage = page;
            this.addBusinessCurrencies()
              .then(data => {
                //Essentially check if the page has changed since we last requested the currencies, if so, we can discard the result
                if (this.oldPage === page) {
                  this.listingsResultData = data;
                }
              })
              .catch();

            this.page = page + 1;
            this.paginationInfo.totalPages = res.data.totalPages;
            this.paginationInfo.totalElements = res.data.totalElements;

            if (this.paginationInfo.totalElements !== 0) {
              let bounds = paginationInfo.getPageInfo(this.page, this.perPage, this.paginationInfo.totalElements);
              this.lowerCount = bounds[0];
              this.upperCount = bounds[1];
            }
          })
          .catch(err => {
            errorPopup.showPopupFromServer(this, ['Failed to search for Listings'], err)
          });
    },

    /**
     * Add business currency to all results
     */
    async addBusinessCurrencies() {
      let resultData = [];
      if (this.listingsResultData && this.listingsResultData.length > 0) {
        const promiseArray = this.listingsResultData.map((x) => currencyQuery.getCurrentCurrencyByCountry(x.business.countryForCurrency));
        const results = await Promise.all(promiseArray).then();
          results.forEach((res, index) => {
            const listingResult = this.listingsResultData[index];
            listingResult.currencySymbol = res.symbol;
            listingResult.currencyCode = res.code;
            resultData.push(listingResult);
          });
        }

      return resultData;
    },

    /**
     * Open a modal that shows further details about the listing
     * @param listing information of listing to show
     */
    openListingModal(listing) {
      const query = Object.assign({}, this.$route.query);
      query.listingId = listing.id;

      //Replace the URL with listingID without refreshing the page using .replace
      history.pushState(
          {},
          null,
          '#' + this.$route.path +
          '?' +
          Object.keys(query)
              .map(key => {
                return (
                    encodeURIComponent(key) + '=' + encodeURIComponent(query[key])
                )
              })
              .join('&')
      )

      this.$buefy.modal.open({
        parent: this,
        component: ListingPopUpComponent,
        props: {listingId: listing.id, actingAs: localStorage.getItem('user-type')},
        events: {
          'removeListing': this.removeListing
        }
      });
    },

    removeListing(listingId) {
      this.listingsResultData = this.listingsResultData.filter(x => x.id !== listingId);
    }

  },

  watch: {
    /**
     * Monitors the userSearch variable
     * If its empty the fullSearch variable is reset
     */
    searchQuery() {
      if (this.searchQuery === "" || this.fullSearch === null) {
        this.fullSearch = '?search=';
      }
    }
  }
}

export default ListingsSearchResultComponent;
</script>
