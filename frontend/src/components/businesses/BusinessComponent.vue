<template>
  <div class="business-main">
    <div class="business-general">

      <div class="business-container">
        <div class="business-header-container">
          <div class="business-image"/>
          <section class="hero business-image-hero is-small business-main-hero-body">
              <div class="business-top-hero-body">
                <p class="title business-image-text">
                  <strong>{{ business.name ? business.name : "Business Name" }} </strong>
                </p>
                <p class="subtitle business-image-text">
                  Registered since {{registrationDate}} | {{businessDays}}
                </p>
              </div>
              <div class="business-hero-buttons">
                <div class="admin-buttons" v-if="isBusinessAdmin()">
                  <b-button class="business-inventory-button"
                            tag="router-link" :to="{  name: 'businessHome', params: { businessId: businessId } }">
                    <b-icon icon="book-open" size="is-small" class="small-margin-inline"></b-icon>
                    Catalogue
                  </b-button>
                  <b-button class="business-inventory-button"
                            tag="router-link" :to="{  name: 'salesReport', params: { businessId: businessId } }">
                    <b-icon icon="finance" size="is-small" class="small-margin-inline"></b-icon>
                    Sales Report
                  </b-button>
                  <b-button class="business-inventory-button"
                            tag="router-link" :to="{  name: 'businessSalesHistory', params: { businessId: businessId } }">
                    <b-icon icon="history" size="is-small" class="small-margin-inline"></b-icon>
                    Sales History
                  </b-button>
                </div>
                <div class="user-buttons" v-if="!isBusinessAdmin()">
                  <b-button class="business-listings-button"
                            tag="router-link" :to="{  name: 'businessListings', params: { businessId: businessId }  }">
                    Listings
                  </b-button>
                </div>
              </div>
          </section>
        </div>

        <div class="business-info-container">
          <div class="tile is-ancestor">
            <div class="tile is-5 is-vertical is-parent">
              <div class="panel tile is-child is-info">
                <p class="panel-heading">
                  <b-icon
                      icon="information"
                      size="is-small">
                  </b-icon>
                  About
                </p>
                <div class="panel-block">
                  <p><strong>Type:</strong> {{ business.businessType ? business.businessType : "N/A" }}</p>
                </div>
                <div class="panel-block">
                  <p><strong>Address:</strong> {{ address ? address : "N/A" }}</p>
                </div>
              </div>
              <div class="panel tile is-child is-warning">
                <p class="panel-heading">
                  <b-icon
                      icon="tooltip-text"
                      size="is-small">
                  </b-icon>
                  Administrators
                </p>
                <div>
                  <BusinessAdminsComponent :user-data="admins" :primary-admin="primaryAdminId"/>
                </div>
              </div>
            </div>
            <div class="tile is-parent">
              <div class="panel tile is-child is-dark">
                <p class="panel-heading">
                  <b-icon
                      icon="tooltip-text"
                      size="is-small">
                  </b-icon>
                  Description
                </p>
                <div class="panel-block">
                  <p>{{ business.description ? business.description : "No description available." }}</p>
                </div>
              </div>
            </div>
          </div>
          <!--          Only show if the current user is the primary admin -->
          <div v-if="isPrimaryAdmin()">
            <div class="panel is-child is-danger">
              <p class="panel-heading">
                <b-icon
                    icon="tooltip-text"
                    size="is-small">
                </b-icon>
                Business Owner Panel
              </p>
              <div class="panel-block">
                <BusinessAdminPanelComponent style="min-height: 30vh"
                                             :business-admins="this.admins"
                                             @giveBusinessAdmin="giveBusinessAdminPriv($event)"
                                             @revokeBusinessAdmin="revokeBusinessAdminPriv($event)"/>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from '@/api/api';
import userProfile from "@/components/users/userProfile";
import logger from "@/logger";
import BusinessAdminsComponent from "@/components/businesses/BusinessAdminsComponent";
import BusinessAdminPanelComponent from "@/components/businesses/BusinessAdminPanelComponent";
import userSearch from "@/components/users/search/userSearch";
import errorPopup from "@/components/errorPopup";

const BusinessComponent = {
    name: "business",
    components: {BusinessAdminPanelComponent, BusinessAdminsComponent},

    /**
     * View specific data
     */
    data() {
      return {
        business: {},
        address: null,
        admins: [],
        registrationDate: null,
        primaryAdminId: -1,
        businessDays: null,
        photoUrl: null,
        businessId: this.$route.params.businessId
      }
    },

  /**
   * When view mounted
   */
  created() {
    this.onInitialize();
  },

  /**
   * View methods
   */
  methods: {

    /**
     * On component being initialized
     */
    onInitialize() {
      api.businesses
          .getBusiness(this.$route.params.businessId)
          .then(res => {
            this.business = res.data;

            this.admins = this.business.administrators;
            this.primaryAdminId = this.business.primaryAdministratorId;
            this.business.created = new Date(this.business.created);
            this.registrationDate = this.business.created.toLocaleDateString("en-GB")
            this.businessDays = userProfile.dateDiff(this.business.created, new Date());
            this.address = userProfile.formatFullAddress(this.business.address);
          })
          .catch(error => {
            logger.getLogger().warn(error);
            this.$router.push({name: 'notFound'});
          });
    },

    /**
     * Is the current user a primary admin of the business?
     */
    isPrimaryAdmin() {
      if (localStorage.getItem("user-type") === "user") {
        return this.primaryAdminId === parseInt(localStorage.getItem('id')) ||
            userSearch.isAdminRole(localStorage.getItem("role"), false);
      }
      return false;
    },

    /**
     * Is the current user acting as a business admin, or are they an admin?
     */
    isBusinessAdmin() {
        return (localStorage.getItem("user-type") === "business" && this.business.id === parseInt(localStorage.getItem('business-id')))
            || userSearch.isAdminRole(localStorage.getItem("role"), false);
    },

    /**
     * Gives the selected user the business admin role
     *
     * @param user User object
     * */
    giveBusinessAdminPriv(user) {
      api.businesses
          .makeBusinessAdministrator(this.business.id, user.id)
          .then(() => {
            this.admins.push(user);
          })
          .catch(error => {
            logger.getLogger().warn(error);
            errorPopup.showPopup(this, ["Unable to make user a business administrator"]);
          });
    },

    /**
     * Revokes the selected users business admin role
     *
     * @param user User object
     */
    revokeBusinessAdminPriv(user) {
      api.businesses
          .removeBusinessAdministrator(this.business.id, user.id)
          .then(() => {
            this.admins = this.admins.filter(admin => admin.id !== user.id);
          })
          .catch(error => {
            logger.getLogger().warn(error);
            errorPopup.showPopup(this, ["Unable to revoke business administrator from business owner"]);
          });
    },
  },
};

export default BusinessComponent;
</script>
