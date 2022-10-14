<template>
  <div class="business-product-container">
    <div >
      <p class="panel-heading has-text-centered panel-heading-yellow">
        <b-icon
            icon="archive"
            size="is-small">
        </b-icon>
        Inventory
      </p>

      <div class="business-product-table-container">
        <b-field grouped>
          <b-field expanded/>
          <p class="control">
            <b-button class="is-success" icon-left="plus" label="Add Inventory" @click="createInventoryItemModel" />
          </p>
        </b-field>
        <strong style="font-size: 12px">*Click to edit row or see more information</strong>

        <b-table
            :data="inventoryData"
            :sticky-header="true"
            ref="table"
            height="100%"
            style="cursor: pointer"

            aria-next-label="Next page"
            aria-previous-label="Previous page"
            aria-page-label="Page"
            aria-current-label="Current page"
            @click="editInventoryItemModal($event)"
            focusable
            :striped="true"

            backend-sorting
            @sort="onSort"
        >

          <b-table-column field="image" width="90" v-slot="props">
            <b-image :src="getProductThumbnailLink(props.row.product.businessId, props.row.product.primaryImageId)" @error="getAltImage" style="cursor: pointer"/>
          </b-table-column>

          <b-table-column field="product.id" label="Inventory ID" sortable v-slot="props" width="130">
            {{ props.row.id.toString()}}
          </b-table-column>

          <b-table-column field="product.name" label="Product Name" sortable v-slot="props" width="200">
            {{ props.row.product.name | truncateValue(50) }}
          </b-table-column>

          <b-table-column field="quantity" label="Quantity" sortable v-slot="props" width="40">
            {{ props.row.quantity }}
          </b-table-column>

          <b-table-column field="pricePerItem" label="Individual Price" sortable v-slot="props">
            {{ businessCurrency.symbol }}{{ props.row.pricePerItem }} {{businessCurrency.code }}
          </b-table-column>

          <b-table-column field="totalPrice" label="Total Price" sortable v-slot="props">
            {{ businessCurrency.symbol }}{{ props.row.totalPrice }} {{businessCurrency.code }}
          </b-table-column>

          <b-table-column field="product.manufactured" label="Manufactured Date" sortable v-slot="props">
            {{ props.row.manufactured ? new Date(props.row.manufactured).toLocaleDateString('en-GB') : "N/A" }}
          </b-table-column>

          <b-table-column field="product.expires" label="Expiry Date" sortable v-slot="props">
            {{ new Date(props.row.expires).toLocaleDateString('en-GB') }}
          </b-table-column>

          <b-table-column label="New Listing" v-slot="props" centered>
            <b-button v-on:click.stop icon-left="plus" type="is-success" @click="createListingItemModal(props.row)" />
          </b-table-column>
        </b-table>
      </div>
    </div>
    <div class="pagination-info-center" >
      <b-pagination class="catalogue-business-pagination-buttons"
                    backend-pagination
                    :total="paginationInfo.totalElements"
                    order="is-centered"
                    size="is-small"
                    :rounded="true"
                    :per-page="perPage"
                    :current.sync="page"
                    @change="onPageChange"

                    icon-prev="chevron-left"
                    icon-next="chevron-right">
      </b-pagination>
      Showing {{lowerCount}} - {{upperCount}} of {{paginationInfo.totalElements}} results
    </div>
  </div>
</template>

<script>
import api from "@/api/api";
import CreateInventoryComponent from "@/components/businesses/inventory/CreateInventoryComponent";
import ModifyInventoryComponent from "@/components/businesses/inventory/ModifyInventoryComponent";
import logger from "@/logger";
import error from "@/components/errorPopup";
import CreateListingComponent from "@/components/businesses/listings/CreateListingComponent";
import businesses from "../businesses";
import paginationInfo from "@/components/paginationInfo"

const InventoryComponent = {
  name: "InventoryComponent",

  data() {
    return {
      inventoryData: [],
      page: 1,
      perPage: 10,
      paginationInfo: {
        totalPages: 0,
        totalElements: 0
      },
      businessObject: null,
      // Business currency, default to New Zealand if can't find currency
      businessCurrency: {},
      searchQuery: "",
      lowerCount: 0,
      upperCount: 0,
      sortBy: "NAME_DESC"
    }
  },

  async beforeMount() {
    await this.onInitialize();
  },

  methods: {

    /**
     * On initializing this component
     *
     * @return {Promise<void>}
     */
    async onInitialize() {
      this.getInventory();
      this.businessCurrency = await api.currency.getCurrentBusinessCurrency(this.$route.params.businessId);
    },

    /**
     *  Manage backend pagination of table, change page and then call onInitialize()
     */
    onPageChange(page) {
      this.page = page;
      this.onInitialize();
    },

    /**
     * Initializes the create product model and opens it
     */
    createInventoryItemModel() {
      this.$buefy.modal.open({
        parent: this,
        component: CreateInventoryComponent,
        hasModalCard: true,
        customClass: 'custom-class custom-class-2',
        trapFocus: true,
        events: {
          'refreshTable': () => {
            this.refreshTable();
          }
        }
      });
    },

    /**
     * Initializes the edit inventory product model and opens it (when click on pencil icon)
     */
    editInventoryItemModal(row) {
      this.$buefy.modal.open({
        parent: this,
        component: ModifyInventoryComponent,
        hasModalCard: true,
        customClass: 'custom-class custom-class-2',
        props: {
          data: row
        },
        trapFocus: true,
        events: {
          'refreshTable': () => {
            this.refreshTable();
          }
        }
      });
    },

    createListingItemModal(row) {
      this.$buefy.modal.open({
        parent: this,
        component: CreateListingComponent,
        hasModalCard: true,
        customClass: 'custom-class custom-class-2',
        props: {
          data: row
        },
        trapFocus: true,
        events: {
          'refreshTable': () => {
            this.refreshTable();
          }
        }
      });
    },

    /**
     * Retrieves the inventory from the api, paginated depend on page and perPage variable
     */
    getInventory() {
      api.inventory
        .retrieveInventory(this.$route.params.businessId, this.page, this.perPage, this.sortBy)
        .then(res => {
          this.inventoryData = res.data["paginationElements"];
          this.paginationInfo.totalPages = res.data["totalPages"];
          this.paginationInfo.totalElements = res.data["totalElements"];
          if (this.paginationInfo.totalElements !== 0) {
            let bounds = paginationInfo.getPageInfo(this.page, this.perPage, this.paginationInfo.totalElements);
            this.lowerCount = bounds[0]
            this.upperCount = bounds[1]
          }
        })
        .catch(err => {
          logger.getLogger().warn(err);
          error.showPopupFromServer(this, err);
        });
    },

    /**
     * Event fired whenever a column sort is clicked
     */
    onSort(field, event)
    {
      if (field.slice(0, 7) === "product") {
        field = field.slice(8)
      }
      const lookup = {
        id: "ID", name: "NAME", manufactured: "MANUFACTURED", expires: "EXPIRY",
        quantity: "QUANTITY", pricePerItem: "INDIVIDUAL_PRICE", totalPrice: "TOTAL_PRICE"
      }
      this.sortBy = lookup[field] + "_" + event.toUpperCase();
      this.onPageChange(this.page);
    },


    /**
     * Refreshes the inventory table
     */
    async refreshTable() {
      await this.onInitialize();
    },

    /**
     * Takes a business id and the primary id of a product image and returns the link to the image.
     */
    getProductThumbnailLink(businessId, primaryId) {
      return businesses.getProductImageLink(businessId, primaryId, true);
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
  },

  filters: {

    /**
     * Returns a smaller string based on the string and length given.
     *
     * @param value string value of the description
     * @param length int value
     *
     * @return returns a modified string to fit the given length
     */
    truncateValue(value, length) {
      return businesses.maxDisplay(value, length);
    }
  },
}

export default InventoryComponent;
</script>
