<template>
  <div class="user-home-container">
    <div class="user-home-content">
      <div class="tile is-ancestor">
        <div class="tile">
          <div class="tile is-parent">
            <div class="tile is-child">
              <div class="user-home-nav-container panel">
                <b-tabs v-model="activeTab"  size="is-large" vertical>
                  <b-tab-item icon="home" label="Home"></b-tab-item>
                  <b-tab-item icon="card-bulleted" label="My Cards"></b-tab-item>
                  <b-tab-item icon="archive" label="Archived"></b-tab-item>
                </b-tabs>
              </div>
            </div>
          </div>

          <div class="tile is-parent feed-component">
            <div class="tile is-child white-background panel" v-if="activeTab === 0">
              <FeedComponent :userId="user.id"/>
            </div>

            <div class="tile is-child white-background panel " v-if="activeTab === 1">
              <UserCardsComponent :user="user.firstName" :userId="user.id"/>
            </div>

            <div class="tile is-child white-background panel " v-if="activeTab === 2">
              <ArchivedComponent :userId="user.id"/>
            </div>

          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from "../../api/api";
import logger from "../../logger";
import FeedComponent from "./FeedComponent";
import UserCardsComponent from "@/components/marketplace/UserCardsComponent";
import ArchivedComponent from "@/components/home/ArchivedComponent";

const UserHomeComponent = {
  name: "UserHome",
  components: {ArchivedComponent, FeedComponent, UserCardsComponent},

  data() {
    return {
      activeTab: 0,
      user: {id: localStorage.getItem("id")},

      homeString: "This tab contain all notifications and messages you have received"
    }
  },

  created() {
    this.onInitialize();
  },

  methods: {

    /**
     * On page initialization
     */
    onInitialize() {
      this.setUserInfo();
    },

    /**
     * Retrieves the current users information
     */
    setUserInfo() {
      api.users
          .getUser(localStorage.getItem("id"))
          .then(res => {
            this.user = res.data;
          })
          .catch(error => {
            logger.getLogger().warn(error);
          });
      this.userBoolean = localStorage.getItem("user-type") === "user";
    },
  }

};

export default UserHomeComponent;
</script>
