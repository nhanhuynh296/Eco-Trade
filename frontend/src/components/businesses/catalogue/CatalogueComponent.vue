<template>
  <div class="business-product-container">
    <div>
      <p class="panel-heading has-text-centered panel-heading-blue">
        <b-icon
            icon="book-open"
            size="is-small">
        </b-icon>
        Catalogue
      </p>

      <div class="business-product-table-container">
        <b-field grouped>
          <b-field expanded/>
          <p class="control">
            <b-button class="is-success" icon-left="plus" label="Add Product" id="openCreateProductBtn"
                      @click="createProductCardModel"/>
          </p>
        </b-field>
        <strong style="font-size: 12px">*Click to edit row or see more information</strong>

        <b-table
            :data="catalogueData"
            :sticky-header="true"
            :checked-rows.sync="checkedRows"
            default-sort="id"
            ref="table"
            height="100%"
            style="cursor: pointer"

            aria-next-label="Next page"
            aria-previous-label="Previous page"
            aria-page-label="Page"
            aria-current-label="Current page"
            @click="editProductCardModal($event)"
            id="catalogueTable"
            :striped="true"
            focusable

            backend-sorting
            @sort="onSort"
        >

          <b-table-column field="image" width="90" v-slot="props">
            <div style="cursor: pointer">
              <b-image :src="getProductThumbnailLink(props.row.primaryImageId)" @error="getAltImage" class="image is-64x64"/>
            </div>
          </b-table-column>

          <b-table-column field="id" label="Product ID" v-slot="props" sortable width="130">
            {{ props.row.id }}
          </b-table-column>

          <b-table-column field="name" label="Name" sortable v-slot="props" width="250">
            {{ props.row.name | truncateValue(50)}}
          </b-table-column>

          <b-table-column field="description" label="Description" sortable v-slot="props" width="250">
            {{ props.row.description | truncateValue(50)}}
          </b-table-column>

          <b-table-column field="recommendedRetailPrice" label="RRP" sortable v-slot="props" width="150">
            {{ businessCurrency.symbol }}{{ props.row.recommendedRetailPrice }} {{businessCurrency.code }}
          </b-table-column>

          <b-table-column field="created" label="Created" sortable v-slot="props" width="140">
            {{ new Date(props.row.created).toLocaleDateString('en-GB') }}
          </b-table-column>

          <b-table-column field="manufacturer" label="Manufacturer" sortable v-slot="props">
            {{ props.row.manufacturer | truncateValue(40) }}
          </b-table-column>
        </b-table>
      </div>
    </div>
    <div class="pagination-info-center">
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
import catalogueSearch from "../../../components/businesses/catalogue/catalogueSearch";
import CreateProductComponent from "../../../components/businesses/catalogue/CreateProductComponent";
import api from "../../../api/api";
import logger from "../../../logger";
import ModifyProductComponent from "../../../components/businesses/catalogue/ModifyProductComponent";
import businesses from "../../../components/businesses/businesses";
import paginationInfo from "../../../components/paginationInfo";

const CatalogueComponent = {
  name: "CatalogueComponent",

  data() {
    return {
      catalogueData: [],
      checkedRows: [],
      page: 1,
      perPage: 10,
      paginationInfo: {
        totalPages: 0,
        totalElements: 0
      },
      searchQuery: '',
      businessId: this.$route.params.businessId,
      // Business currency, default to New Zealand if can't find currency
      businessCurrency: {},
      primarySrc: "",
      lowerCount: 0,
      upperCount: 0,
      sortBy: "NAME_DESC",
    }
  },

  /**
   * Initialise the catalogue table
   **/
  created() {
    this.onInitialize();
  },

  computed: {

    /**
     * Dynamically alters the catalogue table results
     *
     * @returns {*[]}
     */
    filter: function() {
      return catalogueSearch.catalogueSearch(this.searchQuery, this.catalogueData);
    }
  },

  methods: {

    /**
     * On initializing this component
     *
     * @return {Promise<void>}
     */
    async onInitialize() {
      this.getCatalogue();
      await api.currency.getCurrentBusinessCurrency(this.businessId)
        .then(res => this.businessCurrency = res)
        .catch(err => {
          logger.getLogger().warn(err);
        });
    },

    /**
     * Returns a list of inventory to display in the catalogue table
     */
    getCatalogue() {
      api.businesses
        .getProducts(this.businessId, this.page, this.perPage, this.sortBy)
        .then(res => {
          this.catalogueData = res.data.paginationElements;
          this.paginationInfo.totalPages = res.data.totalPages;
          this.paginationInfo.totalElements = res.data.totalElements;
          if (this.paginationInfo.totalElements !== 0) {
            let bounds = paginationInfo.getPageInfo(this.page, this.perPage, this.paginationInfo.totalElements);
            this.lowerCount = bounds[0]
            this.upperCount = bounds[1]
          }
        })
        .catch(err => {
          logger.getLogger().warn(err);
          this.$router.push({name: 'notFound'});
        });
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
    createProductCardModel() {
      this.$buefy.modal.open({
        parent: this,
        component: CreateProductComponent,
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
     * Initializes the edit product model and opens it (when click on pencil icon)
     */
    editProductCardModal(row) {
      this.$buefy.modal.open({
        parent: this,
        component: ModifyProductComponent,
        hasModalCard: true,
        customClass: 'custom-class custom-class-2',
        props: {
          data: row
        },
        trapFocus: true,
        events: {
          'refreshTable': () => {
            this.refreshTable();
          },

          'updatePrimaryImage': (productId, primaryImageId) => {
            this.updatePrimaryImage(productId, primaryImageId);
          },

          'removeImage': (productId, itemId) => {
            this.removeImage(productId, itemId);
          }
        }
      });
    },

    /**
     * Event fired whenever a column sort is clicked
     */
    onSort(field, event)
    {
      const lookup = {
        id: "ID", name: "NAME", recommendedRetailPrice: "RRP", created: "CREATED",
        manufacturer: "MANUFACTURER", description: "DESCRIPTION"
      }
      this.sortBy = lookup[field] + "_" + event.toUpperCase();
      this.onPageChange(this.page);
    },

    /**
     * Refreshes the catalogue table
     */
    async refreshTable() {
      await this.onInitialize();
    },

    /**
     * Update a rows primary image id
     */
    updatePrimaryImage(productId, primaryImageId) {
      this.catalogueData.find(x => x.id === productId).primaryImageId = primaryImageId;
    },

    /**
     * Sends a request to remove one image from a product
     */
    removeImage(productId, imageId) {
      const product = this.catalogueData.find(x => x.id === productId);
      product.images = product.images.filter(x => x.id !== imageId);
      // Force primary image to refresh if we deleted it
      if (product.primaryImageId === imageId) {
        product.primaryImageId = null;
      }
    },

    /**
     * Takes the primary id of a product image and returns a link to an existing image.
     * If there is no image (primary id is null) a default image is sent.
     */
    getProductThumbnailLink(primaryId) {
      return businesses.getProductImageLink(this.businessId, primaryId, true)
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

export default CatalogueComponent;
</script>
