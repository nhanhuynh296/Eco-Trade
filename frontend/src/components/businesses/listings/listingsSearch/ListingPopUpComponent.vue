<template>
  <div v-if="ready" class="tile is-ancestor listing" style="margin: auto;">
    <div class="tile is-vertical">
      <div class="tile">
        <div class="tile is-parent">
          <div class="tile is-child panel white-background is-primary">
            <p class="panel-heading listing-popup-title">
              <button type="button" style="float:right" class="delete is-large" v-on:click="onExit">Close</button>
              {{ product.name }}
            </p>

            <div class="tile is-parent">

              <!-- Left Column   -->
              <div class="tile is-child box" style="margin-right: 0.8rem !important;">

                <div class="tile is-vertical">

                  <div class="tile is-parent is-justify-content-center">
                    <div class="listing-popup-image" :style="{ backgroundImage: 'url(' + listingImage + ')' }">
                    </div>
                  </div>

                  <b-tooltip position="is-top" size="is-medium" multilined style="width: 100%">
                    <div class="tile is-parent is-vertical">
                      <div class="title is-6 has-text-centered">
                        <p class="listing-popup-information-title">Product Information
                          <b-icon icon="information-outline" size="is-small" type="is-primary"/>
                        </p>
                        <hr>
                      </div>

                      <div class="tile is-child">
                        <p>
                          <strong>Manufactured Date</strong>: {{ formatDate(inventoryItem.manufactured) || "N/A" }}
                        </p>
                        <p>
                          <strong>Sell By Date</strong>: {{ formatDate(inventoryItem.sellBy) || 'N/A' }}
                        </p>
                        <p>
                          <strong>Best Before Date</strong>: {{ formatDate(inventoryItem.bestBefore) || 'N/A' }}
                        </p>
                        <p>
                          <strong>Expiry Date</strong>: {{ formatDate(inventoryItem.expires) || 'N/A' }}
                        </p>
                      </div>

                    </div>
                    <template v-slot:content>
                      Description: {{ product.description }} <br><br>
                      Manufacturer: {{ product.manufacturer }} <br>
                    </template>
                  </b-tooltip>

                </div>
              </div>


              <!-- Right Column   -->

              <div class="tile is-child box">

                <div class="tile is-vertical">
                  <div class="tile is-vertical">

                    <div class="title is-parent listing-popup-dates">
                      <b-field>
                        <b-icon icon="calendar-plus" size="is-small"></b-icon>
                        Listed Date: {{ new Date(details.created).toLocaleDateString('en-GB') }}
                      </b-field>
                      <b-field>
                        <b-icon icon="calendar-plus" size="is-small"></b-icon>
                        Closing Date: {{ new Date(details.closes).toLocaleDateString('en-GB') }}
                      </b-field>

                    </div>

                    <b-button v-if="liked && actingAs === 'user'" type="is-warning" label="Unlike" icon-left="thumb-up" @click="unlikeListing"></b-button>
                    <b-button v-if="!liked && actingAs === 'user'" type="is-warning" label="Like" icon-left="thumb-up-outline" @click="likeListing"></b-button>
                    <div class="tile is-child">
                      <p v-if="details.likes.length === 1"> 1 person has liked this listing</p>
                      <p v-else> {{ details.likes.length }} people have liked this listing</p>
                    </div>

                    <div class="is-vertical is-parent">
                      <p class="listing-popup-information-title" style="cursor: auto">More Information</p>
                      <hr>

                      <div class="is-child">
                        <p class="listing-popup-description">{{ details.moreInfo }}</p>
                      </div>
                    </div>

                    <div class="tile is-vertical is-parent">
                      <p class="listing-popup-information-title" style="cursor: auto">Price</p>
                      <hr/>
                      <p><strong>{{ currency.code }} {{ currency.symbol }}{{ details.price.toFixed(2) }}</strong> ({{ details.quantity }} for
                        {{ currency.symbol }}{{ (details.price / details.quantity).toFixed(2) }} each)</p>

                      <br>
                      <b-button v-if="details.quantity < 2 && actingAs === 'user'" type="is-info" label="Buy" @click="buyListingConfirm"></b-button>
                      <b-button v-if="details.quantity > 1 &&  actingAs === 'user'" type="is-info" label="Buy All" @click="buyListingConfirm"></b-button>
                    </div>

                    <br>

                    <div class="tile is-parent is-vertical">

                      <div class="has-text-centered" v-on:click="showBusinessProfile()">
                        <b-tooltip position="is-top" size="is-medium" multilined>
                          <p class="listing-popup-information-title" style="cursor: pointer">Business Information</p>
                          <template v-slot:content>
                            Click to view business profile page.
                          </template>
                        </b-tooltip>
                        <b-icon style="margin-left: 1%" icon="launch" size="is-small" type="is-primary"/>
                      </div>
                      <hr>

                      <div class="tile is-child">
                        <p><strong>Name</strong>: {{ business.name }}</p>
                        <p><Strong>Type</strong>: {{ business.businessType }}</p>
                        <p>
                          <b-icon icon="map-marker" size="is-small"></b-icon>
                          {{ `${business.address.city || 'N/A'}, ${business.address.region || 'N/A'}` }}
                        </p>
                      </div>

                    </div>

                  </div>
                </div>
              </div>

            </div>

          </div>
        </div>
      </div>
    </div>
    <b-modal :active.sync="confirmPurchaseDialog" has-modal-card class="confirm-modal">
        <div class="modal-card" style="width: auto !important;">
          <header class="modal-card-head" >
            <p class="modal-card-title"><strong style="color: white">Confirm Purchase</strong></p>
          </header>
          <section class="modal-card-body">
            Are you sure you would like to purchase {{this.product.name}}
          </section>
          <footer class="modal-card-foot">
            <button class="button" type="button" @click="confirmPurchaseDialog = false">Close</button>
            <button class="button is-primary" @click="buyListing">Confirm</button>
          </footer>
        </div>
    </b-modal>
  </div>
</template>

<script>
import api from "../../../../api/api";
import logger from "../../../../logger";
import errorPopup from "../../../../components/errorPopup";
import businesses from "../../../../components/businesses/businesses";


export default {
  name: "ListingPopUpComponent",

  props: ['listingId', 'actingAs'],
  data() {
    return {
      details: null,
      ready: false,
      inventoryItem: null,
      product: null,
      business: null,
      dateClosingString: null,
      liked: false,

      listingImage: null,
      confirmPurchaseDialog: false,
      currency: "NZD"
    }
  },

  mounted() {
    this.getDetails();

  },

  methods: {
    /**
     * Converts the date to to the local timezone and New Zealand format. If null returns null.
     */
    formatDate(date) {
      if (date) {
        date = new Date(date).toLocaleDateString('en-GB');
      }
      return date;
    },

    /**
     * Gets the specific listing details from backend API
     */
    getDetails() {
      api.listing.getOneListing(this.listingId)
          .then(res => {
            this.details = res.data;
            this.business = res.data.business;
            this.inventoryItem = res.data.inventoryItem;
            this.product = res.data.inventoryItem.product;
            this.liked = !!res.data.likes.includes(parseInt(localStorage.getItem('id')))
            this.ready = true;
            this.listingImage = this.getProductImageLink(this.product.primaryImageId);

            api.currency.getCurrentCurrencyByCountry(this.details.business.countryForCurrency)
                .then(r => {
                  this.currency = r;
                })
                .catch(error => {
                  logger.getLogger().warn(error);
            });

          }).catch(err => {
        logger.getLogger().warn(err);
        this.$parent.close();
        errorPopup.showPopup(this, ['That listing no longer exists']);
      });
    },

    /**
     * Takes a business id and the primary id of a product image and returns the link to the image.
     */
    getProductImageLink(primaryId) {
      return businesses.getListingImageLink(primaryId, false);
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
     * Route to the listing business profile
     */
    showBusinessProfile() {
      this.$emit('close');
      this.$router.push({name: 'business', params: {businessId: this.business.id}});
    },

    /**
     * Show popup for confirming purchase
     */
    buyListingConfirm() {
      this.confirmPurchaseDialog = true;
    },

    /**
     * Method called when buying a listing, removes it from the listings page
     */
    buyListing() {
      api.listing.buyListing(this.details.id)
      .then(() => {
        this.$emit('removeListing', this.details.id);
        this.onExit();
        errorPopup.showPopup(this, [`You have successfully bought: ${this.product.name}`]);
      })
      .catch((err) => {
        logger.getLogger().warn(err);
        this.onExit();
        errorPopup.showPopup(this, ['Cannot buy listing!']);
      })
    },

    /**
     * Like a listing and add to total likes
     */
    likeListing() {
      api.listing.likeListing(this.details["id"])
      .then(res => {
        if ([200, 405].includes(res.status)) {
          this.details.likes.push(parseInt(localStorage.getItem('id')))
          this.liked = true;
          this.$emit("refreshTable");
        }
      }).catch(err => {
        errorPopup.showPopupFromServer(this, err)
      })
    },

    /**
     * Unlike a listing and remove from total likes
     */
    unlikeListing() {
      api.listing.unlikeListing(this.details["id"])
          .then(res => {
            if ([200, 405].includes(res.status)) {
              const index = this.details.likes.indexOf(parseInt(localStorage.getItem('id')))
              if (index > -1) {
                this.details.likes.splice(index, 1);
                this.liked = false;
                this.$emit("refreshTable");
              }
            }
          }).catch(err => {
            errorPopup.showPopupFromServer(this, err)
      })
    },

    /**
     * Removes the listing id from route params on pop-up close
     */
    onExit() {
      const query = Object.assign({}, this.$route.query);
      delete query.listingId;
      this.$router.push({path: this.$route.path, query: query});
      this.$emit('close');
    }
  }
}
</script>

