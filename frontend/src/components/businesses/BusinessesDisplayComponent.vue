<template>
  <div class="profile-container">
    <div class="profile-business">
      <div class="user-cards-title">
        {{ userName }}'s Businesses
      </div>

      <hr/>

      <div class="businesses-contribute" v-on:click="registerBusiness" v-if="isShown && userCheck">
        <b-button type="is-primary" icon-left="plus">Create Business</b-button>
      </div>

      <div class="businesses-table">
        <b-table
          :data="businessTableData"
          ref="table"

          @click="$router.push({name: 'business', params: {businessId: $event.id} });"

          aria-next-label="Next page"
          aria-previous-label="Previous page"
          aria-page-label="Page"
          aria-current-label="Current page"
          :striped="true"
          focusable
        >

          <b-table-column field="name" label="Business Name" v-slot="props" width="30vh">
            {{ props.row.name }}
          </b-table-column>

          <b-table-column field="created" label="Date Created" v-slot="props" width="10vh">
            {{ new Date(props.row.created).toLocaleDateString() }}
          </b-table-column>

        </b-table>

        <div class="pagination-info-center profile-business-pagination">
          <b-pagination class="profile-business-pagination-buttons"
                        backend-pagination
                        :total="businesses.length"
                        order="is-centered"
                        size="is-small"
                        :rounded="true"
                        :per-page="perPage"
                        :current.sync="page"
                        @change="getPageBound"

                        icon-prev="chevron-left"
                        icon-next="chevron-right">
          </b-pagination>
          Showing {{lowerCount}} - {{upperCount}} of {{businesses.length}} businesses
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import paginationInfo from "@/components/paginationInfo";

const BusinessesDisplayComponent = {
  name: "businessesProfile",
  props: ["businesses", "isShown", "user"],

  data() {
    return {
      userCheck: true,
      userName: 'My',
      page: 1,
      perPage: 10,
      lowerCount: 0,
      upperCount: 0,
      businessTableData: []
    }
  },

  /**
   * Sets the userCheck
   */
  created() {
    if (this.$route.params.userId) {
      this.userCheck = false;
    }
    this.userName = this.user.firstName;

    if (this.userName.length > 14) {
      this.userName = this.userName.substring(0, 14) + "...";
    }

    this.getPageBound(this.page);
  },

  methods: {

    /**
     * Routes to Business Register page.
     */
    registerBusiness() {
      this.$router.push({name: "registerBusiness"});
    },

    /**
     * Retrieves the new page bounds when the user clicks on pagination arrows
     */
    getPageBound(page){
      this.page = page
      if (this.businesses.length !== 0) {
        let bounds = paginationInfo.getPageInfo(this.page, this.perPage, this.businesses.length );
        this.lowerCount = bounds[0]
        this.upperCount = bounds[1]
      }
      this.businessTableData = this.businesses.slice(this.lowerCount-1,this.upperCount)
    }
  }
}

export default BusinessesDisplayComponent;
</script>
