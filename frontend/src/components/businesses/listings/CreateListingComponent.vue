<template>
    <form action="">
        <div class="modal-card" style="margin: auto">
            <header class="modal-card-head panel-heading-green">
                <p class="modal-card-title inventory-item-title">New Listing
                  <button
                      style="float:right"
                      type="button"
                      class="delete"
                      @click="$emit('close')"
                  />
                </p>
            </header>

            <section class="modal-card-body listing-create-form">
              <strong>Required fields are marked with *</strong>
              <br>
              <br>
              <b-field label="Product" label-position="on-border">
                <b-input class="modal-card-title" disabled="true" :readonly='true'
                         v-model="inventoryItem.product.name"/>
              </b-field>
              <div class="listing-create-info ">
                <b-field label="Closing Date" label-position="on-border">
                  <b-datepicker placeholder="Click to select..." :min-date="createdDate" :max-date="new Date(this.data.expires)" v-model="closingDate" :date-formatter="formatter"/>
                </b-field>

                <b-field label="Date Created" label-position="on-border">
                  <b-datepicker v-model="createdDate" disabled locale="en-GB"/>
                </b-field>

                <b-field label="Quantity*" label-position="on-border" id="quantity">
                    <b-numberinput label = "Quantity*" min="0" :max="inventoryItem.quantity" v-model="quantity" controls-position="compact" expanded required/>
                    <p class="control">
                        <b-button label="Max" @click="setQuantityMax"/>
                    </p>
                </b-field>

                <b-field label="Price*" label-position="on-border">
                    <b-numberinput label = "Price*" min="0" step=0.01 v-model="price" controls-position="compact" expanded required/>
                    <p class="control">
                        <b-button label="Auto" @click="setDefaultPrice" />
                    </p>
                </b-field>

                <b-field label="Description" label-position="on-border">
                    <b-input maxlength="200" v-model="moreInfo" type="textarea" />
                </b-field>
              </div>
            </section>

            <footer class="modal-card-foot inventory-item-create-button">
                <b-button type="submit is-info" @click="$emit('close')">Cancel</b-button>
                <b-button type="submit is-success" @click="createListing">Confirm</b-button>
            </footer>
        </div>
    </form>
</template>

<script>
import validate from "@/components/businesses/listings/validateListing";
import error from "@/components/errorPopup";
import api from "@/api/api";
import logger from "@/logger";
import businesses from "@/components/businesses/businesses";

export default {
    name: "CreateListingComponent",
    props: ["data"],

    data() {
      return {
          quantity: 0, // 0 <= quantity <= total stock
          price: 0, // default to quantity * inventory item price
          createdDate: new Date(Date.now()), // auto set to today
          closingDate: new Date(this.data.expires), // default to the expiry date as per specifications
          inventoryItem: this.data,
          moreInfo: null,

          businessId: this.$route.params.businessId
      }
    },

    methods: {
      /**
       * Converts the date chosen with datepicker to the local timezone and New Zealand format.
       */
      formatter (date) {
        return date.toLocaleDateString('en-GB');
      },

      /**
       * called when the user clicks the auto button
       * sets the price for the user automatically
       */
      setDefaultPrice() {
        if ((this.quantity !== this.inventoryItem.quantity) && this.inventoryItem.pricePerItem) {
            this.price =  this.quantity * this.inventoryItem.pricePerItem;
        } else {
            this.price = this.inventoryItem.totalPrice;
        }
      },

      /**
       * sets the quantity field to the maximum number in stock
       */
      setQuantityMax() {
        this.quantity = this.inventoryItem.quantity;
      },

      /**
       * Adds or subtracts days to a given date so the date stored in the database is correct (due to formatting
       * differences with JSON the day is one off if not adjusted as below). Ignores null dates.
       */
      returnCorrectDate(date, days) {
        // if null is not checked a default date is set
        if (date !== null) {
          const correctedDate = new Date(date);
          correctedDate.setDate(correctedDate.getDate() + days);
          return correctedDate;
        }
      },

      /**
       * Carries out validation and submissions of listing creation form
       */
      createListing() {
        // Checks the required values are valid
        this.errors = validate.validateRequired(
            this.quantity,
            this.price,
            this.closingDate
        );

        if (this.errors.length !== 0) {
          error.showPopup(this, this.errors);
        } else {
          // Modifies the date depending on if the expiry date is also the closing date
          let expiry = new Date(JSON.parse(JSON.stringify(this.data.expires)));
          let trueClosingDate = this.closingDate;
          if (expiry.getTime() === trueClosingDate.getTime()) {
            // If they are equal the returned closing date is not modified
            trueClosingDate = this.returnCorrectDate(this.closingDate, 0);
          } else {
            // A single day is added to the date in order to counteract the formatting differences caused by JSON
            trueClosingDate = this.returnCorrectDate(this.closingDate, 1);
          }

          // API spec for POST listings
          let payload = {
            inventoryItemId: this.inventoryItem.id,
            quantity: Math.round(this.quantity),
            price: businesses.roundDecimalPlaces(this.price),
            moreInfo: this.moreInfo,
            closes: trueClosingDate
          };

          api.listing
            .addListing(payload, this.businessId)
            .then(() => {
              this.$emit('refreshTable');
              this.$emit('close');
            })
            .catch(postError =>  {
              logger.getLogger().error(postError);
              error.showPopupFromServer(this, postError);
            });
        }
      }
    }
}
</script>

