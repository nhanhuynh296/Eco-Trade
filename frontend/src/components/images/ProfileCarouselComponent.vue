<template>
  <div>
    <b-carousel :autoplay="false" :indicator="false" with-carousel-list :overlay="gallery" ex v-bind:class="{'profile-carousel-container' : editable,  'user-profile-carousel-container' : !editable}">
      <b-carousel-item v-for="(item, index) of items" :key="index">
        <b-button class="overlay-top-left is-small mr-1 mt-1" size="xs" type="is-info" v-if="editable && primaryImageId !== item.id" @click="makePrimaryImage(item.id)">
          Make Primary Image
        </b-button>

        <figure class="profile-large-image">
          <b-image :src="`${serverUrl}/users/images/${item.id}`" @error="getAltImage"/>
        </figure>
        <div class="card-content is-overlay primary-image-tag">
          <span v-if="primaryImageId === item.id" class="tag is-primary is-pulled-right is-danger">Primary Image</span>
        </div>
      </b-carousel-item>

      <template #list="props">
        <b-carousel-list class="profile-small-image"
                         v-model="props.active"
                         :data="items"
                         v-bind="al"
                         @switch="props.switch($event, false)"
                         as-indicator/>
      </template>
    </b-carousel>
  </div>
</template>

<script>

import api from "../../api/api";
import logger from "../../logger";
import errorPopup from "../../components/errorPopup";

const serverURL = process.env.VUE_APP_SERVER_ADD;

export default {
  name: "ImageCarousel",
  props: ["data", "editable", "user"],

  data() {
    return {
      primaryImageId: this.data.primaryImageId,
      images: this.data.images,

      items: [],

      serverUrl: serverURL,

      gallery: false,

      al: {
        hasGrayscale: true,
        itemsToShow: 4,
        breakpoints: {
          768: {
            hasGrayscale: false,
            itemsToShow: 4,
          },
        }
      },
    }
  },

  methods: {
    /**
     * Remove image from specific index
     * @param item image item
     */
    // removeImage(item) {
    // },

    /**
     * Make an image the primary image
     * @param id Image item
     */
    makePrimaryImage(id) {
      api.users.updatePrimaryImage(id)
        .then(() => {
          this.primaryImageId = id;
          this.$emit('updatePrimaryImage', id)
        })
        .catch(err => {
          logger.getLogger().error(err);
          errorPopup.showPopup(this, ["Failed to set new primary image"]);
        })
     },

    getAltImage(event) {
      event.target.src = "https://bulma.io/images/placeholders/128x128.png";
    }
  },

  created() {
    this.userId = this.$route.params.userId;
  },

  mounted() {
    if (this.images) {
      let primary = null;

      for (let imageId of this.images) {
        const item = {image: `${serverURL}/users/images/${imageId}`, id: imageId};
        if (item.id === this.primaryImageId) {
          primary = item;
        } else {
          this.items.push(item);
        }
      }
      this.items.unshift(primary);
    }
  },
}
</script>

<style>
@import "../../../public/styles/style.css";
</style>
