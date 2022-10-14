<template>
  <div class="business-home-main">
    <div class="business-home-general">
      <div class="business-home-container tile is-ancestor mb-6">
        <div class="tile is-vertical" v-if="businessData !== undefined">
          <div class="panel is-marginless">
            <h1 class="panel-heading has-addons-centered panel-heading-sales-history">{{ businessData.name }} Sales History</h1>
          </div>
          <div class="tile is-child">
            <BusinessSalesHistoryTableComponent :business-id="businessData.id"/>
          </div>

        </div>
      </div>
    </div>
  </div>
</template>

<script>
import BusinessSalesHistoryTableComponent from "@/components/businesses/BusinessSalesHistoryTableComponent";
import api from "@/api/api";
import errorPopup from "@/components/errorPopup";

export default {
  name: "SalesHistory",
  components: {BusinessSalesHistoryTableComponent},
  title: 'Sales History',
  data() {
    return {
      businessData: undefined
    }
  },
  beforeMount() {
    this.getBusinessDetail();
  },
  methods: {
    /**
     * Get business detail (for showing name of business)
     */
    getBusinessDetail() {
      api.businesses.getBusiness(this.$route.params.businessId)
          .then(res => {
            this.businessData = res["data"];
          }).catch(err => errorPopup.showPopupFromServer(this, err));
    },
  }
}
</script>

<style scoped>
@import "../../public/styles/salesHistory.css";
</style>
