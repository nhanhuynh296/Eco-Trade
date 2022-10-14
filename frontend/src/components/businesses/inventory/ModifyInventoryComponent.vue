<template>
  <form action="">
    <div class="modal-card" v-bind:class="{'modal-card-with-image': this.primaryImageId !== null}" style="margin: auto">
      <header class="modal-card-head panel-heading-yellow">
        <p class="modal-card-title inventory-item-title">Edit Inventory
          <button
            style="float:right"
            type="button"
            class="delete"
            @click="$emit('close')"
          />
        </p>
      </header>

      <section class="modal-card-body product-edit-form" @submit.prevent="modifyInventoryItem">

        <div class="columns">
          <div v-if="primaryImageId !== null && data.product.images.length > 0" class="image-gallery-column column">
            <ImageCarousel v-bind:data="data.product"/>
          </div>

          <div class="column">
            <div class="inventory-item-create-required-fields-key">
              Required fields are marked with *
            </div>
            <div class="inventory-item-create-info">

              <b-field>
                <b-input class="modal-card-title" disabled="true" :readonly='true' v-model="currentItem"/>
              </b-field>

              <!-- manufactured cannot be after sellby or best before -->
              <b-field label="Manufactured Date" label-position="on-border">
                <b-datepicker v-model="manufactured" :first-day-of-week="1" placeholder="Click to select.." icon="calendar-today" trap-focus :date-formatter="formatter"/>
              </b-field>

              <!-- Sell by date cannot be before manufactured date or after expiry date -->
              <b-field label="Sell By Date" label-position="on-border">
                <b-datepicker v-model="sellBy" :first-day-of-week="1" placeholder="Click to select.." icon="calendar-today" trap-focus required :date-formatter="formatter"/>
              </b-field>

              <!-- Best before date cannot be after the expiry date or before manufactured date -->
              <b-field label="Best Before Date" label-position="on-border">
                <b-datepicker v-model="bestBefore" :first-day-of-week="1" placeholder="Click to select.." icon="calendar-today" trap-focus required :date-formatter="formatter"/>
              </b-field>

              <!-- Expiry date cannot be best before date -->
              <b-field label="Expiry Date*" label-position="on-border">
                <b-datepicker v-model="expires" :first-day-of-week="1" placeholder="Click to select.." icon="calendar-today" trap-focus required :date-formatter="formatter"/>
              </b-field>

              <b-field label="Quantity*" label-position="on-border">
                <b-input type="number" min=0 v-model="quantity" @input="setTotal" placeholder=0 />
              </b-field>

              <b-field label="Price per item" label-position="on-border">
                <b-input type="number" min=0 step=0.01 v-model="pricePerItem" @input="setTotal" placeholder=0 />
              </b-field>

              <b-field label="Total Price" label-position="on-border">
                <b-input  type="number" min=0 step=0.01 v-model="totalPrice" placeholder=0  />
              </b-field>
            </div>
          </div>
        </div>
      </section>

      <footer class="modal-card-foot inventory-item-create-button">
        <b-button type="submit is-info" @click="$emit('close')">Cancel</b-button>
        <b-button type="submit is-success" @click="modifyInventoryItem">Modify Item</b-button>
      </footer>
    </div>
  </form>
</template>

<script>
import api from "@/api/api";
import inventoryHelper from "@/components/businesses/inventory/inventoryHelper";
import logger from "@/logger";
import error from "@/components/errorPopup";
import ImageCarousel from "@/components/images/ImageCarouselComponent";
import businesses from "@/components/businesses/businesses";

export default {
  name: "ModifyInventoryComponent",
  components: {ImageCarousel},
  props: ["data"],


  data() {
    return {
      inventoryId: this.data.id,
      // Form input fields
      name: this.data.product.name,
      productId: this.data.product.id,
      quantity: this.data.quantity,
      pricePerItem: this.data.pricePerItem,
      totalPrice: this.data.totalPrice,

      // Since date is nullable, if date doesn't exist, defaults to 01/01/1970 (Unix)
      manufactured: this.data.manufactured ? new Date(this.data.manufactured) : null,
      sellBy: this.data.sellBy ? new Date(this.data.sellBy) : null,
      bestBefore: this.data.bestBefore ? new Date(this.data.bestBefore) : null,

      // This date is not nullable
      expires: new Date(this.data.expires),

      primaryImageId: this.data.product.primaryImageId,
      images: this.data.product.images,

      businessId: this.$route.params.businessId,

      currentItem: null,
    }
  },

  created() {
    this.currentItem = ` (Product ID: ${this.data.product.id}) ${this.data.product.name}`;
  },

  methods: {
    /**
     * Converts the date chosen with datepicker to the local timezone and New Zealand format.
     */
    formatter (date) {
      return date.toLocaleDateString('en-GB');
    },

    /**
     * A function to calculate the total price given the quantity and the price per item. Default it quantity * price per item
     * (recommended retail price). Changes anytime an changes are made on quantity or price per item field. Can still be changed
     * by the user.
     */
    setTotal() {
      this.totalPrice = Number((this.pricePerItem * this.quantity).toFixed(2))
    },

    /**
     * Adds a given number of days to a given date so the date stored in the database is correct
     * (due to formatting differences with JSON the day is one off if not adjusted as below).
     * Ignores null dates.
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
     * Checks if the date for a given value has been modified from the original and if so returns 1.
     * Otherwise, it returns 0.
     */
    returnDaysOffset(originalDate, modifiedDate) {
      if (modifiedDate !== null) {
        let originalDateFormatted = this.returnCorrectDate(originalDate, 0);
        if (originalDate === null || originalDateFormatted.getTime() !== modifiedDate.getTime()) {
          return 1;
        }
      }
      return 0;
    },

    /**
     * Validates and then sends the API request to modify the currently opened inventory product
     */
    modifyInventoryItem() {
      logger.getLogger().info(`Modifying product with ID: ${this.productId}`);
      let errors = inventoryHelper.validateFields(
          this.productId,
          this.quantity,
          this.pricePerItem,
          this.totalPrice,
          this.manufactured,
          this.sellBy,
          this.bestBefore,
          this.expires
      )

      if (errors.length !== 0) {
        error.showPopup(this, errors)
      } else {
        let manufacturedDaysOffset = this.returnDaysOffset(this.data.manufactured, this.manufactured);
        let sellByDaysOffset = this.returnDaysOffset(this.data.sellBy, this.sellBy);
        let bestBeforeDaysOffset = this.returnDaysOffset(this.data.bestBefore, this.bestBefore);
        let expiryDaysOffset = this.returnDaysOffset(this.data.expires, this.expires);

        let payload = {
          id: this.inventoryId,
          productId: this.productId,
          quantity:  Math.round(this.quantity),
          pricePerItem: businesses.roundDecimalPlaces(this.pricePerItem),
          totalPrice: businesses.roundDecimalPlaces(this.totalPrice),
          manufactured: this.returnCorrectDate(this.manufactured, manufacturedDaysOffset),
          sellBy: this.returnCorrectDate(this.sellBy, sellByDaysOffset),
          bestBefore: this.returnCorrectDate(this.bestBefore, bestBeforeDaysOffset),
          expires: this.returnCorrectDate(this.expires, expiryDaysOffset)
        }

        api.inventory.updateInventoryItem(payload, this.businessId, this.inventoryId)
            .then(() => {
              this.$emit('refreshTable');
              this.$emit('close');
            })
            .catch(putError => {
              logger.getLogger().error(putError);
              error.showPopupFromServer(this, putError);
            });
      }
    }
  }



}
</script>
