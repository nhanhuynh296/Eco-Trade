<template>
  <div class="user-profile-main">
    <div class="user-profile-general">
      <div class="user-profile-main-container">
        <div class="user-profile-tabs">
          <b-tabs position="is-centered" type="is-toggle-rounded" v-model="selected">
            <b-tab-item label="Profile" icon="account"></b-tab-item>
            <b-tab-item label="Businesses" icon="information-outline"></b-tab-item>
            <b-tab-item label="Cards"></b-tab-item>
          </b-tabs>
        </div>

        <UserComponent v-if="user && selected === 0" :user="user"/>
        <BusinessesDisplayComponent v-if="selected === 1" :businesses="businesses" :is-shown="true" :user="user"/>
        <UserCardsComponent :user="user.firstName" v-if="selected === 2" :user-id="userId"/>
      </div>
    </div>
  </div>
</template>

<script>
import api from '@/api/api';
import UserComponent from "@/components/users/UserComponent";
import BusinessesDisplayComponent from "@/components/businesses/BusinessesDisplayComponent"
import UserCardsComponent from "@/components/marketplace/UserCardsComponent";
import logger from "@/logger";

export default {
  name: "user-profile",
  components: {UserComponent, BusinessesDisplayComponent, UserCardsComponent},
  title: 'User Profile',

  data() {
    return {
      user: null,
      userId: null,
      businesses: [],
      selected: 0
    };
  },

  created() {
    this.onInitialize();
  },

  methods: {

    /**
     * On page initialization
     */
    onInitialize() {
      this.userId = this.$route.params.userId;

      api.users
          .getUser(this.userId)
          .then(res => {
            this.user = res.data;
            this.businesses = this.user.businessesAdministered;
          })
          .catch(error => {
            logger.getLogger().warn(error);
            this.$router.push({ name: 'notFound' });
          });
    },
  },
};
</script>

<style>
@import '../../public/styles/user.css';
@import '../../public/styles/profile.css';
</style>
