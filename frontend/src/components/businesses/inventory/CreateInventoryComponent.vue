<template>
  <form action="">
    <div class="modal-card" style="margin: auto">
      <header class="modal-card-head panel-heading-yellow">
        <p class="modal-card-title inventory-item-title">New Inventory Product Form</p>
        <button
            type="button"
            class="delete"
            @click="$emit('close')"/>
      </header>

      <section class="modal-card-body inventory-item-create-form" @submit.prevent="createInventoryItem">
        <div class="inventory-item-create-required-fields-key">
          Required fields are marked with *
        </div>

        <div class="inventory-item-create-info">

          <b-field label="Find product in your catalogue*" label-position="on-border">
            <AutofillInputComponent :input-list-data="catalogItemStrings" @input="setRRP(), setTotal()" maxlength="70" placeholder = "e.g. Beans" v-model="name" :do-filter="true" open-on-focus required/>
          </b-field>

          <div class="inventory-date-picker-container">
            <!-- manufactured cannot be after sellby or best before -->
            <b-field label="Manufactured Date" label-position="on-border" class="inventory-date-picker">
              <b-datepicker v-model="manufactured" :first-day-of-week="1" placeholder="Click to select.." :years-range="[-50, 50]"
                            icon="calendar-today" trap-focus required :date-formatter="formatter"/>
            </b-field>

            <!-- Sell by date cannot be before manufactured date or after expiry date -->
            <b-field label="Sell By Date" label-position="on-border" class="inventory-date-picker">
              <b-datepicker v-model="sellBy" :first-day-of-week="1" placeholder="Click to select.." :years-range="[-50, 50]"
                            icon="calendar-today" trap-focus required :date-formatter="formatter"/>
            </b-field>
          </div>

          <div class="inventory-date-picker-container"><!-- Best before date cannot be after the expiry date or before manufactured date -->
            <b-field label="Best Before Date" label-position="on-border" class="inventory-date-picker">
              <b-datepicker v-model="bestBefore" :first-day-of-week="1" placeholder="Click to select.." :years-range="[-50, 50]"
                            icon="calendar-today" trap-focus required :date-formatter="formatter"/>
            </b-field>

            <!-- Expiry date cannot be best before date -->
            <b-field label="Expiry Date*" label-position="on-border" class="inventory-date-picker">
              <b-datepicker v-model="expires" :first-day-of-week="1" placeholder="Click to select.." :years-range="[-50, 50]"
                            icon="calendar-today" trap-focus required :date-formatter="formatter"/>
            </b-field>
          </div>

          <b-field label="Quantity*" label-position="on-border">
            <b-input type="number" min=0 v-model="quantity" @input="setTotal()" placeholder=0 />
          </b-field>

          <b-field label="Price per item" label-position="on-border">
            <b-input type="number" min=0 step=0.01 v-model="pricePerItem" @input="setTotal()" placeholder=0 />
          </b-field>

          <b-field label="Total Price" label-position="on-border">
            <b-input type="number" min=0 step=0.01 v-model="totalPrice" placeholder=0 />
          </b-field>
        </div>
      </section>

      <footer class="modal-card-foot inventory-item-create-button">
        <b-button type="submit is-info" @click="$emit('close')">Cancel</b-button>
        <b-button id="create-btn" type="submit is-success" @click="createInventoryItem">Add Item</b-button>
      </footer>
    </div>
  </form>
</template>

<script>
import api from "../../../api/api";
import logger from "../../../logger";
import error from "../../../components/errorPopup";
import AutofillInputComponent from "../../../components/autofill/AutofillInputComponent";
import inventoryHelper from "../../../components/businesses/inventory/inventoryHelper";

const InventoryCreateComponent = {
  name: "CreateInventoryComponent",
  components: {AutofillInputComponent},
  data() {
    const today = new Date();
    return {
      minDate: new Date(today.getFullYear() - 100, today.getMonth(), today.getDate()), // ten years ago today
      maxDate: new Date(today.getFullYear() + 100, today.getMonth(), today.getDate()), // ten years from today

      name: null,
      productId: null,
      quantity: null,
      pricePerItem: null,
      totalPrice: null,
      manufactured: null,
      sellBy: null,
      bestBefore: null,
      expires: null,
      businessId: this.$route.params.businessId,
      errors: [],

      //What to show in the select box
      catalogItemStrings: [],
      //Map string to ID
      catalogItems: {}
    }
  },

  /**
   * Get all (50000 product) of business to autocomplete in create inventory
   */
  created() {
    this.businessId = this.$route.params.businessId;
    api.businesses.getProducts(this.businessId, 1, 50000).then(res => {
      this.catalogItems = res.data.paginationElements;
      this.catalogItemStrings = inventoryHelper.convertItemsToStrings(res.data.paginationElements);
    });
  },

  methods: {
    /**
     * Converts the date chosen with datepicker to the local timezone and New Zealand format.
     */
    formatter (date) {
      return date.toLocaleDateString('en-GB');
    },

    /**
     * When the user selects a item from the drop down list, the price per item defaults to the recommended retail price
     * of that item. The user can then change this value depending on how they feel.
     */
    setRRP() {
      this.pricePerItem = inventoryHelper.convertStringToRPP(this.name, this.catalogItems)
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

    createInventoryItem() {
      this.productId = inventoryHelper.convertStringsToIds(this.name, this.catalogItems);
      logger.getLogger().info(`Adding product with ID: ${this.productId} to inventory`);

      this.errors = inventoryHelper.validateFields(
          this.productId,
          this.quantity,
          this.pricePerItem,
          this.totalPrice,
          this.manufactured,
          this.sellBy,
          this.bestBefore,
          this.expires
      )

      if (this.errors.length !== 0) {
        error.showPopup(this, this.errors)
      } else {
        let payload = {
          productId: this.productId,
          quantity:  Math.round(this.quantity),
          pricePerItem: Math.round((this.pricePerItem) * 100) / 100,
          totalPrice: Math.round((this.totalPrice) * 100) / 100,
          manufactured: this.returnCorrectDate(this.manufactured),
          sellBy: this.returnCorrectDate(this.sellBy),
          bestBefore: this.returnCorrectDate(this.bestBefore),
          expires: this.returnCorrectDate(this.expires)
        }

        api.inventory.addToInventory(payload, this.businessId)
            .then(() => {
              this.$emit('refreshTable');
              this.$emit('close');
            })
            .catch(postError => {
              logger.getLogger().error(postError);
              error.showPopupFromServer(this, postError);
            });
      }
    }
  }
}

export default InventoryCreateComponent;
</script>
