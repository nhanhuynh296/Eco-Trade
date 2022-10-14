<template>
  <div class="profile-main">
    <div class="profile-general">
      <div class="profile-main-container">
        <div class="profile-tabs">
          <b-tabs position="is-centered" type="is-toggle-rounded" v-model="selected">
            <b-tab-item label="My Profile" icon="account"></b-tab-item>
            <b-tab-item label="My Businesses" icon="information-outline"></b-tab-item>
            <b-tab-item label="My Cards"></b-tab-item>
          </b-tabs>
        </div>

        <ProfileComponent :is-shown="userBoolean" v-if="selected === 0"/>
        <BusinessesDisplayComponent v-if="selected === 1" :businesses="businesses" :is-shown="userBoolean" :user="user"/>
        <UserCardsComponent :user="user.firstName" v-if="selected === 2" :userId="user.id"/>
      </div>
    </div>
  </div>
</template>

<script>
import api from "@/api/api";
import ProfileComponent from "@/components/users/ProfileComponent";
import BusinessesDisplayComponent from "@/components/businesses/BusinessesDisplayComponent"
import logger from "@/logger";
import UserCardsComponent from "@/components/marketplace/UserCardsComponent";

export default {
  components: {UserCardsComponent, ProfileComponent, BusinessesDisplayComponent},
  title: 'Profile',

  data() {
    return {
      businesses: [],
      user: {id: localStorage.getItem("id")},
      selected: 0,
      userBoolean: false
    }
  },



  methods: {

    /**
     * On page initialized
     */
    onInitialize() {
      api.users
          .getUser(localStorage.getItem("id"))
          .then(res => {
            this.user = res.data;
            this.businesses = this.user.businessesAdministered;
          })
          .catch(error => {
            logger.getLogger().warn(error);
          });
      this.userBoolean = localStorage.getItem("user-type") === "user";
    },


  },
  /**
   * Once the page is created the user is retrieved and the businesses variable is set.
   */
  created() {
    this.onInitialize();
    if (this.$route.query.tab) {
      this.selected = parseInt(this.$route.query.tab);
    }
  },

}
</script>

<style>
@import '../../public/styles/profile.css';
@import '../../public/styles/business.css';
@import '../../public/styles/marketplace.css';
@import '../../public/styles/catalogue.css';
@import '../../public/styles/inventory.css';
</style>
