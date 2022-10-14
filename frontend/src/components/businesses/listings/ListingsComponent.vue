<template>
  <div class="business-product-container">
    <div class="table-content-container">
      <p class="panel-heading has-text-centered panel-heading-greenish">
        <b-icon icon="clipboard-text-multiple" size="is-small">
        </b-icon>
        Listings
      </p>
      <strong style="font-size: 12px">*Click row for more information</strong>
      <div class="business-product-table-container">
        <b-table
            :data="listingData"
            :sticky-header="true"
            ref="table"
            height="100%"
            style="cursor: pointer"

            detailed
            detail-key="id"
            show-detail-icon

            aria-next-label="Next page"
            aria-previous-label="Previous page"
            aria-page-label="Page"
            aria-current-label="Current page"
            @click="openListingModal($event)"
            focusable
            :striped="true"

            backend-sorting
            @sort="onSort"
        >

          <b-table-column field="image" width="90" v-slot="props">
            <div>
              <b-image :src="getProductThumbnailLink(props.row.inventoryItem.product.primaryImageId)"  style="cursor: pointer"/>
            </div>
          </b-table-column>

          <b-table-column field="id" label="Listing ID" sortable v-slot="props" width="130">
            {{ props.row.id }}
          </b-table-column>

          <b-table-column field="listing.product" label="Product Name" sortable v-slot="props" width="480">
            {{ props.row.inventoryItem.product.name | truncateValue(40) }}
          </b-table-column>

          <b-table-column field="id" label="Likes" sortable v-slot="props" width="130">
            {{ props.row.likes.length }}
          </b-table-column>

          <b-table-column field="listing.quantity" label="Quantity" sortable v-slot="props">
            {{ props.row.quantity }}
          </b-table-column>

          <b-table-column field="listing.price" label="Price" sortable v-slot="props">
            {{ businessCurrency.symbol }}{{ props.row.price }} {{businessCurrency.code }}
          </b-table-column>

          <b-table-column field="listing.created" label="Created" sortable v-slot="props">
            {{ new Date(props.row.created).toLocaleDateString('en-GB') }}
          </b-table-column>

          <b-table-column field="listing.expires" label="Closes" sortable v-slot="props">
            {{ new Date(props.row.closes).toLocaleDateString('en-GB') }}
          </b-table-column>

          <template #detail="props">
            <article class="media">

              <div class="media-content">
                <div class="content">
                  <p>
                    <strong>Product Name:</strong> {{ props.row.inventoryItem.product.name }}<br><br>
                    <strong>Description:</strong> {{ props.row.moreInfo }}
                  </p>
                </div>
              </div>
            </article>
          </template>
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
import api from "../../../api/api";
import logger from "../../../logger";
import error from "../../../components/errorPopup";
import businesses from "../businesses";
import ImageCarouselComponent from "../../../components/images/ImageCarouselComponent";
import paginationInfo from "../../../components/paginationInfo";
import ListingPopUpComponent from "@/components/businesses/listings/listingsSearch/ListingPopUpComponent";

const ListingsComponent = {
  name: "ListingsComponent",

  data() {
    return {
      listingData: [],
      searchQuery: "",
      page: 1,
      perPage: 10,
      paginationInfo: {
        totalPages: 0,
        totalElements: 0
      },

      businessId: this.$route.params.businessId,
      // Business currency, default to New Zealand if can't find currency
      businessCurrency: {},

      lowerCount: 0,
      upperCount: 0,
      sortBy: "NAME_DESC",
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
      this.getListings();
      api.currency.getCurrentBusinessCurrency(this.businessId).then(res => this.businessCurrency = res);
    },

    /**
     * Open a modal that shows further details about the listing
     * @param listing The listing the user selected
     */
    openListingModal(listing) {
      if (listing.id) {
        this.$buefy.modal.open({
          parent: this,
          component: ListingPopUpComponent,
          props: { listingId: listing.id, actingAs: localStorage.getItem('user-type')},
          trapFocus: true,
          events: {
            'refreshTable': () => {
              this.refreshTable();
            }
          }
        });
      }
    },

    /**
     * Gets a list of listings for the business, paginated depend on page and perPage variable
     */
    getListings() {
      api.listing.getListings(this.$route.params.businessId, this.page, this.perPage, this.sortBy)
          .then(res => {
            this.listingData = res.data["paginationElements"];
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
     *  Manage backend pagination of table, change page and then call getListings()
     */
    onPageChange(page) {
      this.page = page
      this.getListings();
    },

    /**
     * Event fired whenever a column sort is clicked
     */
    onSort(field, event)
    {
      if (field.slice(0, 7) === "listing") {
        field = field.slice(8)
      }
      const lookup = {
        id: "ID", product: "NAME", quantity: "QUANTITY", price: "PRICE",
        created: "CREATED", expires: "CLOSES"
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
    getProductThumbnailLink(primaryId) {
      return businesses.getListingImageLink(primaryId, true);
    },

    /**
     * Initializes the edit inventory product model and opens it (when click on pencil icon)
     */
    imageCarouselModal(row) {
      this.$buefy.modal.open({
        parent: this,
        component: ImageCarouselComponent,
        hasModalCard: true,
        customClass: '',
        props: {
          data: row.inventoryItem.product,
          isListing: true,
        },
        trapFocus: true
      });
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

export default ListingsComponent;
</script>
