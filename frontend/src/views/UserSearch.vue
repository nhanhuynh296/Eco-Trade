<template>
  <div class="user-search-main">
    <div class="user-search-general">
      <div class="user-search-main-container">
        <div class="result-container">
          <b-table
              class="user-result-container"
              :data="userResultData"
              ref="table"
              aria-next-label="Next page"
              aria-previous-label="Previous page"
              aria-page-label="Page"
              aria-current-label="Current page"
              scrollable

              backend-sorting
              @sort="onSort"

              @click="$router.push({name: 'userProfile', params: {userId: $event.id} });"
              :striped="true"
              focusable
          >
            <b-table-column field="image" v-slot="props" width="100">
              <b-image :src="getUserThumbnailLink(props.row.id, props.row.primaryImageId)" @error="getAltImage" style="cursor: pointer; height:64px; width:64px" id="user-thumbnail"/>
            </b-table-column>

            <b-table-column field="firstName" label="First Name" sortable v-slot="props" width="180">
              {{ props.row.firstName }}
            </b-table-column>

            <b-table-column field="lastName" label="Last Name" sortable v-slot="props" width="180">
              {{ props.row.lastName }}
            </b-table-column>

            <b-table-column field="nickName" label="Nickname" sortable v-slot="props" width="180">
              {{ `${props.row.nickname || 'N/A'}` }}
            </b-table-column>

            <b-table-column field="country" label="Country" sortable v-slot="props">
              {{ `${props.row.homeAddress.country || 'N/A'}` }}
            </b-table-column>

            <b-table-column field="city" label="City" sortable v-slot="props">
              {{ `${props.row.homeAddress.city || 'N/A'}` }}
            </b-table-column>

            <b-table-column field="makeAndRevokeAdmin" label="Make/Revoke"
                            v-slot="props" v-if="loggedInUserAdmin">
              <b-button v-on:click.stop
                        @click="!isUserAnAdmin(props.row.role) ? giveAdminPriv(props.row) : revokeAdminPriv(props.row)"
                        :type="!isUserAnAdmin(props.row.role) ? 'is-success' : 'is-danger'"
                        id="make-admin-button">
                {{ isUserAnAdmin(props.row.role) ? "Revoke Admin" : "Make Admin" }}
              </b-button>
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
              Showing {{lowerCount}} - {{upperCount}} of {{paginationInfo.totalElements}}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import errorPopup from "../components/errorPopup";
import api from "../api/api";
import userSearchService from "../components/users/search/userSearch";
import logger from "../logger";
import paginationInfo from "../components/paginationInfo";
import userProfile from "../components/users/userProfile";

export default {
  name: "UserSearch",
  title: 'User Search',

  data() {
    return {
      showDetailIcon: true,
      userResultData: [],
      page: 1,
      perPage: 10,
      sortBy: "FIRST_ASC",
      paginationInfo: {
        totalPages: 0,
        totalElements: 0
      },
      //Is the logged in user an admin
      loggedInUserAdmin: false,
      lowerCount: 0,
      upperCount: 0,
    }


  },

  /**
   * Before component is loaded
   */
  mounted() {
    this.onPageChange(this.page);
  },

  beforeMount() {
    this.setLoggedInUserRole();
  },

  methods: {

    /**
     * When page is changed
     * @param page page number
     */
    onPageChange(page) {
      api.users
          .searchUsers(`${this.$route.query.search}`, page-1, this.$route.query.size, this.sortBy)
          .then(res => {
            this.userResultData = res.data.paginationElements;

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
            errorPopup.showPopupFromServer(this, ['Failed to search for users'], err)
          })
    },

    /**
     * Event fired whenever a column sort is clicked
     */
    onSort(field, event)
    {
      const lookup = { firstName: "FIRST", lastName: "LAST", nickName: "NICK", country: "COUNT", city: "CITY"}
      this.sortBy = lookup[field] + "_" + event.toUpperCase();
      this.onPageChange(this.page);
    },

    /**
     * Set admin flag if user is an admin
     */
    setLoggedInUserRole() {
      api.users
          .getUser(localStorage.getItem("id"))
          .then(res => {
            let user = res.data;
            this.loggedInUserAdmin = userSearchService.isAdminRole(user.role);
            logger.getLogger().info(`Is searching user an admin: ${this.loggedInUserAdmin}`);
          })
          .catch(error => {
            logger.getLogger().warn(error);
          });
    },

    /**
     * Check if specified role is an admin
     */
    isUserAnAdmin(userRole) {
      return userSearchService.isAdminRole(userRole, false);
    },

    /**
     * Gives the selected user the admin role
     *
     * @param user User object
     * */
    giveAdminPriv(user) {
      api.admin
          .makeAdmin(user.id)
          .then(() => {
            user.role = "ROLE_ADMIN";
          })
          .catch(error => {
            logger.getLogger().warn(error);
          });
    },

    /**
     * Revokes the selected users admin role
     *
     * @param user User object
     */
    revokeAdminPriv(user) {
      api.admin
          .revokeAdmin(user.id)
          .then(() => {
            user.role = "ROLE_DEFAULT";
          })
          .catch(error => {
            logger.getLogger().warn(error);
          });
    },

    /**
     * Takes the primary id of a profile image and returns a link to an existing image.
     * If there is no image (primary id is null) a default image is sent.
     */
    getUserThumbnailLink(userId, primaryId) {
      return userProfile.getUserImageLink(userId, primaryId, true);
    },

    /**
     * If there's an error this function is called and sets the
     * src of the target html event to the default image.
     *
     * @param event
     */
    getAltImage(event) {
      event.target.src = "https://bulma.io/images/placeholders/128x128.png";
    }
  }
}
</script>

<style scoped>
@import "../../public/styles/userSearch.css";
</style>
