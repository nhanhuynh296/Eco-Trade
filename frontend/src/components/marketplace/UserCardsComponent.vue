<template>
  <div v-bind:class="{'user-cards-main-container': this.routeName === 'home', 'profile-container': this.routeName !== 'home'}">
    <div class="user-cards-container">
      <div class="user-cards-title">
        {{ user }}'s Cards
      </div>

      <hr/>

      <div class="card-container-overflow">
        <div class="card-container">
          <div v-bind:key="card.name" v-for="card in cards" class="user-cards-items" v-on:click="showCardModal(card)">
            <CardComponent :card-details="card" :modifiable="isCardOwnerOrAdmin(card.creator.id)"/>
          </div>

          <div v-bind:class="{'profile-cards-pagination-home': this.routeName === 'home', 'profile-cards-pagination': this.routeName !== 'home'}">
            <b-pagination class="profile-cards-pagination-buttons"
                :total="paginationInfo.totalElements"
                order="is-centered"
                size="is-small"
                :rounded="true"
                :per-page="perPage"
                :current.sync="current"
                @change="paginateItems"

                icon-prev="chevron-left"
                icon-next="chevron-right">
            </b-pagination>
          </div>
        </div>
      </div>

      <div class="pagination-info-center">
        Showing {{lowerCount}} - {{upperCount}} of {{paginationInfo.totalElements}} results
      </div>
    </div>
  </div>
</template>

<script>
import CardComponent from "../../components/marketplace/CardComponent";
import CreateEditCardComponent from "../../components/marketplace/CreateEditCardComponent";
import api from "../../api/api";
import logger from "../../logger";
import errorPopup from "../../components/errorPopup";
import CardPopUpComponent from "../../components/marketplace/CardPopUpComponent";
import paginationInfo from "../../components/paginationInfo";

const UserCardsComponent = {
  name: "userCards",
  components: {CardComponent},
  props: ["user", "userId"],

  data() {
    return {
      current: 1,
      perPage: 9,
      sortByOptions: [
        {name: 'Newest posts', paramName: 'DATE_DESC'},
        {name: 'Oldest posts', paramName: 'DATE_ASC'},
        {name: 'Title (A-Z)', paramName: 'TITLE_AZ'},
        {name: 'Title (Z-A)', paramName: 'TITLE_ZA'},
      ],
      sortBy: 'DATE_DESC',
      sortByName: 'Newest posts',
      cards: [],
      paginationInfo: {
        totalPages: 0,
        totalElements: 0
      },
      cardTitle: "My ",
      lowerCount: 0,
      upperCount: 0,
      routeName: this.$route.name
    }
  },

  created() {
    if (this.routeName === "home") {
      this.perPage = 12;
    } else if (this.routeName === "profile") {
      this.perPage = 9;
    }

    this.init();
  },

  methods: {

    init() {
      this.cards = [];
      this.getCards(1);

      if (this.$route.query.cardId) {
        this.showCardModal({id: this.$route.query.cardId});
        const query = Object.assign({}, this.$route.query);
        delete query.cardId;
        this.$router.replace({ query });
      }

      if (this.user.length > 14) {
        this.user = this.user.substring(0, 14) + "...";
      }
    },

    /**
     * Breaks up the list of items to fit into the pagination
     *
     * @return {({name: string}|{name: string}|{name: string}|{name: string}|{name: string})[]}
     */
    paginateItems() {
      this.getCards(this.current, '');
    },

    /**
     * Get cards from backend
     */
    getCards(page) {
      api.cards.getUsersCards(this.userId, page, this.perPage)
          .then(res => {
            this.cards = res.data["paginationElements"];
            this.paginationInfo.totalPages = res.data["totalPages"];
            this.paginationInfo.totalElements = res.data["totalElements"];
            if (this.paginationInfo.totalElements !== 0) {
              let bounds = paginationInfo.getPageInfo(this.current, this.perPage, this.paginationInfo.totalElements);
              this.lowerCount = bounds[0]
              this.upperCount = bounds[1]
            }
          })
          .catch((error) => {
            logger.getLogger().warn(error);
            errorPopup.showPopup(this, ['Unable to get cards'])
          });
    },

    /**
     * Get a sorted, paginated list of cards from backend based on the option selection from the dropdown
     */
    sortCards(option) {
      this.sortByName = option.name;
      this.sortBy = option.paramName;
      this.init();
    },

    /**
     * Initializes the create for sale card modal and opens it
     */
    createCardModal(section) {
      this.$buefy.modal.open({
        parent: this,
        component: CreateEditCardComponent,
        hasModalCard: true,
        props: {section: section},
        customClass: 'custom-class custom-class-2',
        trapFocus: true,
        events: {
          'refreshTable': () => {
            this.init();
          }
        }
      });
      this.current = 1;
    },

    /**
     * Open a modal that shows further details about the card
     * @param cardInfo information of card to show
     */
    showCardModal(cardInfo) {
      this.$buefy.modal.open({
        parent: this,
        component: CardPopUpComponent,
        props: {cardId: cardInfo.id},
        trapFocus: true,
        events: {
          'refreshTable': () => {
            this.init();
          }
        }
      });
    },

    /**
     * Only show delete button if the user is either the card creator, GAA, or DGAA
     */
    isCardOwnerOrAdmin(creatorId) {
      if (localStorage.getItem('role') === "ROLE_ADMIN" || localStorage.getItem('role') === "ROLE_DEFAULT_ADMIN") {
        return true;
      }
      return parseInt(localStorage.getItem('id')) === creatorId;
    },
  }
};

export default UserCardsComponent;
</script>
