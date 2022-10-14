<template>
  <div class="user-container">
    <br/>
    <div class="user-image">
      <div class="user-preview">
        <div class="profile-preview-buttons">
            <b-button type="submit is-info is-small" v-on:click="viewProfileImages()" v-if="images.length > 0">View {{ userName }}'s Images</b-button>
        </div>
      </div>
    </div>

    <div class="user-section-one">
      <b-field class="user-name user-name-span">
        {{ user.firstName }}
      </b-field>
      <b-field class="user-name user-name-span" v-if="user.middleName">
        {{ user.middleName }}
      </b-field>
      <b-field class="user-name user-name-span">
        {{ user.lastName }}
      </b-field>
      <b-field class="user-name user-name-span" v-if="user.nickname">
        ({{ user.nickname }})
      </b-field>
    </div>

    <div class="user-member-date">
      Member since {{userDate}} | {{userDays}}
    </div>

    <div class="user-section-two">
      <div class="user-basic">
        <b-field label="Bio">
          {{ user.bio }}
        </b-field>
      </div>
      <div class="user-contact">
        <b-field label="Country">
          {{ `${user.homeAddress.country || 'N/A'}` }}
        </b-field>
        <b-field label="Region">
          {{ `${user.homeAddress.region || 'N/A'}` }}
        </b-field>
        <b-field label="City">
          {{ `${user.homeAddress.city || 'N/A'}` }}
        </b-field>
      </div>
    </div>
  </div>
</template>

<script>
import userProfile from "@/components/users/userProfile";
import ProfileCarouselComponent from "@/components/images/ProfileCarouselComponent";

const UserComponent = {
  name: "user",
  props: ["user"],

  data() {
    return {
      photoUrl: null,
      userDate: null,
      userDays: null,
      region: "",
      images: [],
      userName: this.user.firstName
    };
  },

  /**
   * When the page is created the userDate, userDays, and region are set
   * and the image preview is set
   */
  created() {
    this.userDate = new Date(this.user.created);
    this.userDate = this.userDate.toLocaleDateString("en-GB");
    this.userDays = userProfile.dateDiff(new Date(this.user.created), new Date());

    if (this.userName.length > 14) {
      this.userName = this.userName.substring(0, 14) + "...";
    }

    for (let image of this.user.images) {
      this.images.push(image.id)
    }

    fetch(document.querySelector(".user-preview")).then(() => {
      this.setPreview();
    });
  },

  methods: {

    /**
     * Sets the background image to default preview image
     */
    setPreview() {
      let image = this.getUserThumbnailLink(this.user.primaryImageId);
      document.querySelector(".user-preview").style.backgroundImage = `url(${image})`;
    },

    /**
     * Takes the primary id of a profile image and returns a link to an existing image.
     * If there is no image (primary id is null) a default image is sent.
     */
    getUserThumbnailLink(primaryId) {
      return userProfile.getUserImageLink(this.user.id, primaryId, true)
    },

    /**
     * Initializes the images carousel for profile
     */
    viewProfileImages() {
      this.$buefy.modal.open({
        parent: this,
        component: ProfileCarouselComponent,
        props: {
          data: {images: this.images, primaryImageId: this.user.primaryImageId},
          editable: false,
          user: this.user
        },
        trapFocus: true
      });
    },
  }
}

export default UserComponent;
</script>
