<template>
  <b-navbar class="navbar-container">
    <template #brand>
      <b-navbar-item tag="router-link" :to="{ name: 'home' }">
        <img class="logo" src="../../../public/assets/logo2.png" alt="Logo">
      </b-navbar-item>
    </template>

    <template #start>
      <b-navbar-item tag="div" v-if="!businessCheck">
        <b-field class="navbar-search">
          <p class="control">
            <b-dropdown v-model="isUserSearch">

              <template #trigger>
                <b-button :label="isUserSearch ? 'User Search' : 'Business Search'" icon-right="menu-down" type="is-primary is-light" />
              </template>

              <b-dropdown-item :value=true>User Search</b-dropdown-item>
              <b-dropdown-item :value=false>Business Search</b-dropdown-item>
            </b-dropdown>
          </p>

          <p class="control" v-if="!isUserSearch">
            <b-dropdown v-model="businessSearchType">

              <template #trigger>
                <b-button :label="businessSearchType ? businessSearchType : 'All'" icon-right="menu-down" type="is-primary is-light" />
              </template>

              <b-dropdown-item>All</b-dropdown-item>
              <b-dropdown-item value="Accommodation">Accommodation</b-dropdown-item>
              <b-dropdown-item value="Food Services">Food Services</b-dropdown-item>
              <b-dropdown-item value="Retail Trade">Retail Trade</b-dropdown-item>
              <b-dropdown-item value="Charitable organisation">Charitable organisation</b-dropdown-item>
              <b-dropdown-item value="Non-profit organisation">Non-profit organisation</b-dropdown-item>
            </b-dropdown>
          </p>

          <b-tooltip position="is-bottom" size="is-large" multilined>
            <input v-bind:class="{'navbar-search-bar': searchQuery === '', 'navbar-search-bar-text': searchQuery !== '' }" type="search" @keyup.enter="search" v-model="searchQuery" placeholder="">

            <template v-slot:content>
              <span v-if="isUserSearch">
                You may only search for other user's first name, last name, and/or nickname. <br/><br/> Combine <strong class="white">AND</strong> & <strong class="white">OR</strong> terms within your search for more advanced searching. <br/> <strong class="white">"your search"</strong> provides exact results.
              </span>

              <span v-if="!isUserSearch">
                You may only search by business name. <br/><br/> Combine <strong class="white">AND</strong> & <strong class="white">OR</strong> terms within your search for more advanced searching. <br/> <strong class="white">"your search"</strong> provides exact results.
              </span>
            </template>
          </b-tooltip>

          <p class="control">
            <b-button type="is-primary" label="Search" @click="search"></b-button>
          </p>
        </b-field>
      </b-navbar-item>

      <b-navbar-item tag="div" class="" v-if="!businessCheck">
        <b-button icon-left="home" v-if="isLogged" class="user-search-button" size="is-medium" tag="router-link"
                  :to="{ name: 'home' }"><span class="navbar-button-name">Home</span>
        </b-button>
      </b-navbar-item>

      <b-navbar-item tag="div" class="" v-if="!businessCheck">
        <b-button icon-left="shopping" v-if="isLogged" class="user-search-button" size="is-medium" tag="router-link"
                  :to="{ name: 'marketplace' }"><span class="navbar-button-name">Community Marketplace</span>
        </b-button>
      </b-navbar-item>

      <b-navbar-item tag="div" class="" v-if="!businessCheck">
        <b-button icon-left="google-my-business" v-if="isLogged" class="user-search-button" size="is-medium" tag="router-link"
                  :to="{ name: 'listingsSearch' }"><span class="navbar-button-name">Business Listings</span>
        </b-button>
      </b-navbar-item>

      <b-navbar-item tag="div" v-if="!dgaaCheck && businessCheck">
        <b-button icon-left="book-open" v-if="isLogged" class="user-search-button" size="is-medium" tag="router-link"
                  :to="{ name: 'businessHome', params: { businessId: businessId }}">
          <span class="navbar-button-name">Catalogue</span>
        </b-button>
      </b-navbar-item>

      <b-navbar-item tag="div" v-if="!dgaaCheck && businessCheck">
        <b-button icon-left="finance" v-if="isLogged" class="user-search-button" size="is-medium" tag="router-link"
                  :to="{ name: 'salesReport', params: { businessId: businessId }}">
          <span class="navbar-button-name">Sales Report</span>
        </b-button>
      </b-navbar-item>

      <b-navbar-item tag="div" v-if="!dgaaCheck && businessCheck">
        <b-button icon-left="history" v-if="isLogged" class="user-search-button" size="is-medium" tag="router-link"
                  :to="{ name: 'businessSalesHistory', params: { businessId: businessId } }">
          <span class="navbar-button-name">Sales History</span>
        </b-button>
      </b-navbar-item>

    </template>

    <template #end>
      <b-navbar-item tag="div" v-if="isLogged">
        <div v-if="!businessCheck && $route.name !== 'home'" class="notification-icon">
          <b-dropdown
            append-to-body
            position="is-bottom-left"
            aria-role="menu"
            :scrollable="isScrollable"
            :max-height=maxHeight
            ref="notificationDropdown"

          >
            <template #trigger>
              <a class="notification-icon">
                <div v-if="notificationsList.length > 0">
                  <b-icon icon="bell-alert" type="is-warning"></b-icon>
                </div>
                <div v-else>
                  <b-icon icon="bell" type="is-success"></b-icon>
                </div>
              </a>
            </template>
              <div class="notifications-title">
                <strong style="color: black">Notifications</strong>
              </div>

            <hr class="dropdown-divider">

            <div class="user-notifications">
              <div class="user-notifications-data"
                   v-for="notification in notificationsList" v-bind:key="notification.id"
                   @click="redirectToCard(notification)">
                  <b-notification
                      :type="getColourFromType(notification.type)"
                      role="alert"
                      :closable="false"
                      :class="notification.type === 'CARD_EXPIRING' ? 'user-notifications-message-info' : 'user-notifications-message-warning'"
                    >
                    <div class="notification-content">
                      <div style="padding-inline: 0.5vw">
                        <b-icon icon="alert" v-if="notification.type === 'warning'"></b-icon>
                        <b-icon icon="information" v-else></b-icon>
                      </div>
                       {{ notification.message }}
                    </div>
                    <b-button class="round-button" size="is-small" @click.native.stop="deleteNotificationFromPanel(notification.id)">
                      <b-icon icon="close" custom-size="3vw"></b-icon>
                    </b-button>
                  </b-notification>
                </div>
              <div v-if="notificationsList.length === 0" style="text-align: center">
                <p>No notifications available</p>
              </div>
            </div>
          </b-dropdown>
        </div>
      </b-navbar-item>

      <b-navbar-item tag="div" v-if="isLogged">
        <div class="role-box">
          <div class="role-box-notification notification is-danger" v-if=adminCheck>Admin</div>
        </div>
        <div class="role-box">
          <div class="role-box-notification notification is-info" v-if=businessCheck>Business</div>
        </div>
      </b-navbar-item>

      <b-navbar-item tag="div" v-if="isLogged">
        <div class="navbar-logout">
          <div class="logout-button-div">
            <b-dropdown
                append-to-body
                position="is-bottom-left"
                aria-role="menu">
              <template #trigger>
                <a class="user-options">
                  <span v-bind:class="{'user-name-span':  parseInt(businessId) === -1, 'business-name-span': parseInt(businessId) !== -1}">{{ welcomeString }}</span>
                  <b-icon icon="menu-down"></b-icon>
                </a>
              </template>
              <b-dropdown-item custom aria-role="menuitem">
                <div v-if="!businessCheck">
                  Logged in as <strong style="color: black">{{ userName }}</strong>
                </div>
                <div v-if="businessCheck">
                  Acting as <strong style="color: #262626">{{ businessName }}</strong>
                </div>
              </b-dropdown-item>
              <hr class="dropdown-divider">
              <b-dropdown-item v-if="businessCheck" aria-role="menuitem" @click="redirect('home')">
                <b-icon icon="book-open"></b-icon>
                Catalogue
              </b-dropdown-item>
              <b-dropdown-item v-else aria-role="menuitem" @click="redirect('home')">
                <b-icon icon="home"></b-icon>
                Home
              </b-dropdown-item>
              <b-dropdown-item v-if="businessCheck" aria-role="menuitem" @click="redirect('profile')">
                <b-icon icon="abacus"></b-icon>
                Business Profile
              </b-dropdown-item>
              <b-dropdown-item v-else aria-role="menuitem" @click="redirect('profile')">
                <b-icon icon="account"></b-icon>
                Profile
              </b-dropdown-item>
              <b-dropdown-item aria-role="menuitem" @click="actAsUser" v-if="businessCheck">
                <b-icon icon="account"></b-icon>
                Act as {{ userName }}
              </b-dropdown-item>
              <b-dropdown-item aria-role="menuitem" v-if="businesses.length > 0">
                <b-dropdown position="is-bottom-left" :triggers="['hover']" aria-role="list" :scrollable="true" :max-height="120">
                  <template #trigger>
                    <b-icon icon="book-open"></b-icon>
                    Act as business
                    <b-icon icon="menu-down"></b-icon>
                  </template>

                  <b-dropdown-item @click="redirect('registerBusiness')" v-if="!businessCheck">
                    <div class="is-flex is-flex-direction-row ">
                      <b-icon icon="plus" class="mr-1" type="is-success"></b-icon>
                      Add Business
                    </div>
                  </b-dropdown-item>

                  <div v-for="business in businesses" v-bind:key="business.id">
                    <b-dropdown-item class="business-name-span" aria-role="listitem" @click="actAsBusiness(business)">
                      {{ business.name }}
                    </b-dropdown-item>
                  </div>

                </b-dropdown>
              </b-dropdown-item>
              <b-dropdown-item v-if="businesses.length === 0" @click="redirect('registerBusiness')">
                <b-icon icon="plus" type="is-success"></b-icon>
                Add Business
              </b-dropdown-item>
              <hr class="dropdown-divider">
              <b-dropdown-item @click="logout()" aria-role="menuitem">
                <b-icon icon="logout"></b-icon>
                Logout
              </b-dropdown-item>
            </b-dropdown>
          </div>
        </div>
      </b-navbar-item>

      <b-navbar-item tag="div" v-if="isLogged">
        <div class="navbar-user-thumbnail">
          <b-image id="user-thumbnail" :src="getUserThumbnail()" @error="getAltImage"/>
        </div>
      </b-navbar-item>
    </template>
  </b-navbar>
</template>

<script>
import avatar from '../../../public/assets/avator.png';
import api from '../../api/api';
import navBar from "../../components/home/navBar";
import logger from "../../logger";
import error from "../../components/errorPopup";
import CardPopUpComponent from "../../components/marketplace/CardPopUpComponent";
import userProfile from "../../components/users/userProfile";

const OFFSET = 60;

const NavBarComponent = {
  name: 'navbar',
  components: {CardPopUpComponent},
  data() {
    return {
      isLogged: api.loginRegister.isLoggedIn(),
      welcomeString: null,
      dgaaCheck: false,
      adminCheck: false,
      user: null,
      userName: null,
      userRole: null,
      businessName: null,
      businesses: [],
      businessCheck: false,
      businessId: localStorage.getItem("business-id") ? localStorage.getItem("business-id") : -1,
      isScrollable: true,
      isUserSearch: true,
      searchQuery: "",
      businessSearchType: null,
      maxHeight: 500,
      selected: 0,
      notificationsList: [],
      showNavbar: true,
      lastScrollPosition: 0,
      scrollValue: 0,
      searchBar: false
    };
  },

  /**
   * Checks if the user is logged in when created
   */
  created() {
    this.onInitialize();
  },

  mounted () {
    window.addEventListener('scroll', this.onScroll)
  },

  beforeDestroy () {
    window.removeEventListener('scroll', this.onScroll)
  },

  methods: {

    /**
     * On initialize of this component
     */
    onInitialize() {
      if (this.isLogged) {
        api.users
            .getUser(localStorage.getItem("id"))
            .then(res => {
              this.user = res.data;
              this.userRole = this.user.role;
              this.userName = this.user.firstName;
              this.businesses = [];

              localStorage.setItem("user-role", this.userRole);

              if (this.userName.length > 14) {
                this.userName = this.userName.substring(0, 14) + "...";
              }

              for (const business of this.user.businessesAdministered) {
                this.businesses.push({"id": business.id, "name": business.name});
              }

              if (localStorage.getItem("user-type") === "user") {
                this.setWelcomeString(this.userRole, this.userName);
              } else {
                let businessName;

                for (const business of this.businesses) {
                  if (business.id === parseInt(localStorage.getItem("business-id"))) {
                    businessName = business.name;
                  }
                }
                this.setWelcomeString("BUSINESS", businessName);
              }
            })
            .catch(err => {
              logger.getLogger().warn(err);
              error.showPopup(this, "Get User Error", [err]);
            });

        // Connection to API call to get all the notifications of the user to populate the panel with.
        api.notifications
            .getNotification()
            .then(res => {
              this.notificationsList = res.data;
              for (const item in res.data) {
                if (this.notificationsList[item].type === 'COMMENT_RECEIVED') {
                  this.formatNotificationMessage(item);
                }
              }
            })
            .catch(err => {
              logger.getLogger().warn(err);
              error.showPopup(this, ["Unable to retrieve notifications"]);
            });
      }
    },

    /**
     * Formats a comment received notification for the navbar notification panel by removing the bulk message. This is because
     * there is no way to expand a b-notification to reveal the full message.
     */
    formatNotificationMessage(item) {
      let splitMessage = this.notificationsList[item].message.split("\n")
      this.notificationsList[item].message = "";
      splitMessage.pop()
      for (const i in splitMessage) {
        this.notificationsList[item].message += splitMessage[i].toString().trim() + "\n"
      }
    },

    /**
     * Delete the users notification, from the panel and also calls the delete notification API
     *
     * @param id The id of the notification to delete
     */
    deleteNotificationFromPanel(id) {
      this.notificationsList = this.notificationsList.filter(function (notification) {
        return notification.id !== id;
      });
      api.notifications.deleteNotification(id);
    },

    /**
     * Redirects the user to the card after clicking on the notification
     *
     * @param notification
     */
    redirectToCard(notification) {
      const functions = {
        "COMMENT_RECEIVED": () => { this.$router.push({name: `home`, query: {tab: "0"}})},
        "CARD_DELETED": () => { this.$router.push({name: `profile`, query: {tab: "2"}})},
        "CARD_EXPIRING": () => {
          this.$router.push({name: `profile`, query: {tab: "2"}});
          this.showCardModal(notification.cardId);
        },
        "LIKED": () => {
          if (notification.listingId) {
            this.$router.push({name: `listingsSearch`, query: {listingId: notification.listingId}});
          }
        }
      }

      if (functions[notification.type]) {
        functions[notification.type]();
      }
      this.$refs.notificationDropdown.toggle();
    },

    /**
     * Get colour from notification type
     */
    getColourFromType(type) {
      let returnColour = '';
      switch (type) {
        case 'CARD_DELETED':
          returnColour = 'is-warning is-light';
          break;
        case 'CARD_EXPIRING':
          returnColour = 'is-danger is-light';
          break;
        case 'STARRED':
          returnColour = 'is-success is-light';
          break;
        case 'KEYWORD_ADDED':
          returnColour = 'is-info is-light';
          break;
      }

      return returnColour;
    },

    /**
     * Open a modal that shows further details about the card
     *
     * @param cardId ID of card to show
     */
    showCardModal(cardId) {
      this.$buefy.modal.open({
        parent: this,
        component: CardPopUpComponent,
        props: {cardId: cardId},
        trapFocus: true,
      });

    },

    /**
     * Logs the user out
     * Resets the token and id of the user within local storage
     */
    logout() {
      this.isLogged = false;
      localStorage.clear();
      api.loginRegister.logoutUser();
      this.$router.push({name: 'login'});
    },

    /**
     * Sets the welcome string
     *
     * @param role string, the role of the user
     * @param name string, the name of the user
     */
    setWelcomeString(role, name) {
      switch (role) {
        case "ROLE_DEFAULT_ADMIN":
          this.userName = "ADMIN";
          this.welcomeString = "ADMIN";
          this.businessCheck = false;
          this.dgaaCheck = true;
          this.adminCheck = true;
          break;
        case "ROLE_ADMIN":
          this.userName = name
          this.welcomeString = this.userName;
          this.dgaaCheck = false;
          this.adminCheck = true;
          this.businessCheck = false;
          break;
        case "BUSINESS":
          this.businessName = name;
          this.welcomeString = this.businessName;
          this.dgaaCheck = false;
          this.adminCheck = false;
          this.businessCheck = true;
          break;
        default:
          this.userName = name
          this.businessCheck = false;
          this.welcomeString = this.userName;
      }
    },

    /**
     * Redirects to the required page
     *
     * @param name string, the route name
     */
    redirect(name) {
      if (name === "profile") {
        if (localStorage.getItem("user-type") === "user") {
          this.$router.push({name: `${name}`});
        } else {
          this.$router.push({name: 'business', params: {businessId: localStorage.getItem("business-id")}});
        }
      } else {
        this.$router.push({name: `${name}`});
      }
    },

    /**
     * Swaps to acting as a business
     *
     * @param business the array of business id and name
     */
    actAsBusiness(business) {
      localStorage.setItem("business-id", business.id);
      localStorage.setItem("user-type", "business");
      this.businessId = business.id;

      this.setWelcomeString("BUSINESS", business.name);
      this.$emit('update');
    },

    /**
     * Swaps to acting as a user
     */
    actAsUser() {
      localStorage.setItem("business-id", -1);
      localStorage.setItem("user-type", "user");
      this.businessId = -1;

      this.setWelcomeString(this.userRole, this.userName);
      this.$emit('update');
    },

    /**
     * Retrieves the final search query depending on the search type
     * Creates an api call to retrieve the search results based on the query
     */
    search() {
      let query;

      //If we're searching for a user
      if (this.isUserSearch) {
        query = navBar.searchUsers(this.searchQuery);
        this.$router.push({name: "userSearch", query: {search: query, page: "1", size: "10"}})
      } else {
        query = navBar.searchBusinesses(this.searchQuery, this.businessSearchType);
        this.$router.push({name: "businessSearch", query: {search: query.substr(8), page: "1", size: "10"}})
      }
    },

    /**
     * Hides the navbar if scrolling down and shows it when scrolling up
     */
    onScroll() {
      if (window.pageYOffset < 0) {
        return;
      }
      if (Math.abs(window.pageYOffset - this.lastScrollPosition) < OFFSET) {
        return;
      }

      this.showNavbar = window.pageYOffset < this.lastScrollPosition;
      this.lastScrollPosition = window.pageYOffset;

      if (this.showNavbar) {
        document.querySelector(".navbar-container").style.visibility = 'visible';
        document.querySelector(".navbar-container").style.transform = 'translate3d(0, 0, 0)';
        document.querySelector(".navbar-container").style.transition = '0.1s all ease-out';
      } else {
        document.querySelector(".navbar-container").style.visibility = 'hidden';
        document.querySelector(".navbar-container").style.transform = 'translate3d(0, -100%, 0)';
      }
    },

    /**
     * If there's an error this function is called and sets the
     * src of the target html event to the default image.
     *
     * @param event
     */
    getAltImage(event) {
      event.target.src = avatar;
    },

    /**
     * Takes the primary image id and returns a link to the image.
     * If there is no image (primary id is null) a default image is sent.
     */
    getUserThumbnail() {
      if (this.user) {
          return userProfile.getUserImageLink(this.user.id, this.user.primaryImageId, true)
      }
    },

  },

  watch: {

    /**
     * Monitors any changes in the route
     * If the user logs in/logs out the route changes,
     * checks for the value of the token and stets boolean value to isLogged
     *
     * If isLogged true, the userName is set
     */
    $route() {
      this.route = this.$router.currentRoute.path;
      this.isLogged = api.loginRegister.isLoggedIn();
      this.onInitialize();
    }
  }
};

export default NavBarComponent;
</script>
<style scoped>
strong{
  color: white;
}
</style>
