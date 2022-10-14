<template>
  <div class="sales-history-container">
    <div class="business-sale-history-table">
      <p class="has-text-weight-bold">*Ordered by most recently sold</p>
      <b-table
          height="100%"
          width="100%"
          :data="data" striped sticky-header
          default-sort="saleDate">
        <b-table-column field="thumbnail" label="" v-slot="props" width="20px">
          <b-image :src="getProductThumbnailLink(props.row.product.primaryImageId)" @error="getAltImage"
                   class="image is-64x64"/>
        </b-table-column>
        <b-table-column field="product.id" label="Product Code" v-slot="props">
          {{ props.row.product.id }}
        </b-table-column>
        <b-table-column field="product.name" label="Product Name" v-slot="props">
          {{ props.row.product.name | truncateValue(50) }}
        </b-table-column>
        <b-table-column field="listingDate" label="Created Date" v-slot="props">
          {{ new Date(props.row.listingDate).toLocaleDateString('en-GB') }}
        </b-table-column>
        <b-table-column field="saleDate" label="Sold Date" v-slot="props">
          {{ new Date(props.row.saleDate).toLocaleDateString('en-GB') }}
        </b-table-column>
        <b-table-column field="numLikes" label="Likes" v-slot="props">
          {{ props.row.numLikes }}
        </b-table-column>
        <b-table-column field="quantity" label="Quantity" v-slot="props">
          {{ props.row.quantity }}
        </b-table-column>
        <b-table-column field="soldFor" :label="`Price ` + (businessCurrency.symbol ? `(${businessCurrency.symbol}${businessCurrency.code})` : ``)" v-slot="props">
          {{ props.row.soldFor }}
        </b-table-column>
        <template #empty>
          <div class="has-text-centered">No records</div>
        </template>
      </b-table>
    </div>
    <div class="pagination-info-center" style="padding-top: 1rem">
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
      Showing {{ lowerCount }} - {{ upperCount }} of {{ paginationInfo.totalElements }} results
    </div>
  </div>


</template>

<script>
import api from "../../api/api";
import businesses from "../../components/businesses/businesses";
import paginationInfo from "../../components/paginationInfo";
import errorPopup from "../../components/errorPopup";
import logger from "../../logger";
import currencyQuery from "../../api/restcountries/currencyQuery";

export default {
  name: "BusinessSalesHistoryComponent",
  props: {
    businessId: {type: Number, required: true}
  },
  data() {
    return {
      data: [],
      businessData: {},
      page: 1,
      perPage: 10,
      paginationInfo: {
        totalPages: 0,
        totalElements: 0
      },
      lowerCount: Number,
      upperCount: Number,

      businessCurrency: {
        symbol: "",
        code: ""
      }
    }
  },
  beforeMount() {
    this.getBusinessSaleHistory(this.page);
    currencyQuery.getCurrentBusinessCurrency(this.businessId)
        .then(res => this.businessCurrency = res)
        .catch();
  },
  methods: {
    /**
     * Getting business sale history
     */
    getBusinessSaleHistory(page) {
      api.sales.getSalesHistory(this.businessId, page, this.perPage)
          .then(res => {
            this.paginationInfo.totalElements = res.data.totalElements;
            this.paginationInfo.totalPages = res.data.totalPages;
            this.data = res.data.paginationElements
            const bounds = paginationInfo.getPageInfo(this.page, this.perPage, this.paginationInfo.totalElements);
            this.lowerCount = bounds[0]
            this.upperCount = bounds[1]
          }).catch(err => {
        errorPopup.showPopupFromServer(this, err);
      }).catch(err => {
        logger.getLogger().warn(err);
        this.$router.push({name: 'notFound'});
      });

    },

    /**
     * On page change
     *
     * @param page The current page
     */
    onPageChange(page) {
      this.page = page;
      this.getBusinessSaleHistory(page);
    },

    /**
     * Takes the primary id of a product image and returns a link to an existing image.
     * If there is no image (primary id is null) a default image is sent.
     */
    getProductThumbnailLink(primaryId) {
      return businesses.getProductImageLink(this.$route.params.businessId, primaryId, true)
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
</script>
