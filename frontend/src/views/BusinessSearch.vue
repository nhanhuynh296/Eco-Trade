<template>
  <div class="user-search-main">
    <div class="user-search-general">
      <div class="user-search-main-container">
        <div class="result-container">
          <b-table
              class="user-result-container"
              :data="businessResultData"
              ref="table"

              backend-sorting
              @sort="onSort"

              aria-next-label="Next page"
              aria-previous-label="Previous page"
              aria-page-label="Page"
              aria-current-label="Current page"
              @click="$router.push({name: 'business', params: {businessId: $event.id }});"
              focusable
              :striped="true"
          >

            <b-table-column field="name" label="Name" sortable v-slot="props" width="330">
              {{ props.row.name }}
            </b-table-column>

            <b-table-column field="businessType" label="Type" sortable v-slot="props" width="330">
              {{ props.row.businessType }}
            </b-table-column>

            <b-table-column field="created" label="Date Created" sortable v-slot="props" width="330">
              {{ props.row.created }}
            </b-table-column>

          </b-table>

          <div class="pagination-info-center search-pagination">
            <b-pagination class="search-pagination-buttons"
                backend-pagination
                :total="paginationInfo.totalElements"
                v-model="page"
                order="is-centered"
                :simple="false"
                :rounded=true
                :per-page="perPage"
                :current.sync="page"
                @change="onPageChange"
                icon-next="chevron-right"
                icon-prev="chevron-left">
            </b-pagination>

            <span class="search-pagination-text">
              Showing {{lowerCount}} - {{upperCount}} of {{paginationInfo.totalElements}} results
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import errorPopup from "@/components/errorPopup";
import api from "@/api/api";
import paginationInfo from "@/components/paginationInfo";

export default {
  name: "BusinessSearch",
  title: 'Business Search',

  data() {
    return {
      businessResultData: [],
      page: 1,
      perPage: 10,
      paginationInfo: {
        totalPages: 0,
        totalElements: 0
      },
      lowerCount: 0,
      upperCount: 0,
      sortBy: "NAME_ASC",
    }
  },

  /**
   * Before component is loaded
   */
  created() {
    this.onPageChange(this.page);
  },

  methods: {
    /**
     * When page is changed
     * @param page page number
     */
    onPageChange(page) {
      api.businesses
          .searchBusiness(`?search=${this.$route.query.search}`, page-1, this.$route.query.size, this.sortBy)
          .then(res => {
            this.businessResultData = res.data.paginationElements;

            this.page = page;
            this.perPage = this.$route.query.size;
            this.paginationInfo.totalPages = res.data.totalPages;
            this.paginationInfo.totalElements = res.data.totalElements;

            if (this.paginationInfo.totalElements !== 0) {
              let bounds = paginationInfo.getPageInfo(this.page, this.perPage, this.paginationInfo.totalElements);
              this.lowerCount = bounds[0];
              this.upperCount = bounds[1]
            }
          })
          .catch(err => {
            errorPopup.showPopupFromServer(this, ['Failed to search for businesses'], err)
          });
    },

    /**
     * Event fired whenever a column sort is clicked
     */
    onSort(field, event)
    {
      const lookup = { name: "NAME", businessType: "TYPE", created: "DATE"}
      this.sortBy = lookup[field] + "_" + event.toUpperCase();
      this.onPageChange(this.page);
    },
  }
}
</script>

<style scoped>
@import "../../public/styles/style.css";
@import "../../public/styles/userSearch.css";
</style>
