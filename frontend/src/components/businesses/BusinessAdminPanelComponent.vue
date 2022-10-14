<template>
  <div class="container">

    <strong>Add Business Administrator</strong>

    <UserSearchComponent @search="search($event)"/>
    <div v-if="userResultData">
      <b-table
          :data="userResultData"
          ref="table"
          style="overflow-wrap: anywhere"
          class="is-fullwidth business-admin-table"

          backend-pagination
          :pagination-simple="false"
          :total="paginationInfo.totalElements"
          :per-page="perPage"
          @page-change="onPageChange"

          striped
          paginated
          pagination-rounded
          pagination-position="bottom"
          default-sort-direction="asc"

          aria-next-label="Next page"
          aria-previous-label="Previous page"
          aria-page-label="Page"
          aria-current-label="Current page"

          @click="$router.push({name: 'userProfile', params: {userId: $event.id} })"
          focusable
      >

        <b-table-column field="firstName" label="First Name" v-slot="props" width="300">
            {{ props.row.firstName }}
        </b-table-column>

        <b-table-column field="lastName" label="Last Name" v-slot="props" width="300">
            {{ props.row.lastName }}
        </b-table-column>

        <b-table-column field="nickName" label="Nickname" v-slot="props" width="300">
          {{ props.row.nickname }}
        </b-table-column>

        <b-table-column field="makeAndRevokeAdmin" label="Make/Revoke" v-slot="props" width="200">
          <b-button v-on:click.stop
                    class="is-small"
                    v-on:click.native="!isUserABusinessAdmin(props.row.id) ? giveBusinessAdminPriv(props.row) : revokeBusinessAdminPriv(props.row)"
                    :to="{name: ''}"
                    :type="!isUserABusinessAdmin(props.row.id) ? 'is-success' : 'is-danger'">
            {{ isUserABusinessAdmin(props.row.id) ? "Revoke" : "Make" }}
          </b-button>
        </b-table-column>
      </b-table>
    </div>
  </div>
</template>

<script>
import UserSearchComponent from "@/components/users/search/UserSearchComponent";
import api from "@/api/api";
import errorPopup from "@/components/errorPopup";
import navBar from "@/components/home/navBar";

export default {
  name: "BusinessAdminPanelComponent",
  components: {UserSearchComponent},
  props: ["businessAdmins"],

  data() {
    return {
      searchQuery: null,
      showDetailIcon: true,
      userResultData: null,
      page: 1,
      perPage: 5,
      paginationInfo: {
        totalPages: 0,
        totalElements: 0
      }
    };
  },

  methods: {

    /**
     * Calls the api function
     * Sets the resultList
     */
    search(searchQuery) {
      if (searchQuery) {
        this.searchQuery = navBar.searchUsers(searchQuery);
      } else {
        this.searchQuery = '?search=';
      }

      let page = this.page - 1;
      api.users
          .searchUsers(this.searchQuery, page, this.perPage, "FIRST_DESC")
          .then(res => {
            this.userResultData = res.data.paginationElements;
            this.paginationInfo.totalPages = res.data.totalPages;
            this.paginationInfo.totalElements = res.data.totalElements;
          })
          .catch(err => {
            errorPopup.showPopupFromServer(this, ['Failed to search for users'], err)
          })
    },

    /**
     * When page is changed
     * @param page page number
     */
    onPageChange(page) {
      page = page - 1;
      api.users
          .searchUsers(this.searchQuery, page, this.perPage)
          .then(res => {
            this.userResultData = res.data.paginationElements;

            this.page = page;
            this.paginationInfo.totalPages = res.data.totalPages;
            this.paginationInfo.totalElements = res.data.totalElements;
          })
          .catch(err => {
            errorPopup.showPopupFromServer(this, ['Failed to search for users'], err)
          })
    },

    /**
     * Check if specified user is admin of the business already
     */
    isUserABusinessAdmin(user) {
      return this.businessAdmins.map(x => x.id).includes(user);
    },

    /**
     * Emits an event to make a business admin
     *
     * @param user User object
     * */
    giveBusinessAdminPriv(user) {
      this.$emit('giveBusinessAdmin', user);
    },

    /**
     * Emits an event to revoke a business admin
     * @param user User object
     */
    revokeBusinessAdminPriv(user) {
      this.$emit('revokeBusinessAdmin', user);
    }
  }
}
</script>
