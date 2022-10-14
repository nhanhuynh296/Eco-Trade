<template>
    <div>
      <b-carousel :autoplay="false" :indicator="false" with-carousel-list :overlay="gallery">
        <b-carousel-item v-for="(item, index) of items" :key="index">
          <b-dropdown aria-role="list" class="overlay-top-left" v-if="editable">
            <template #trigger="{ active }">
              <b-button
                  class="is-small mr-1 mt-1"
                  size="xs"
                  label="Options"
                  type="is-info"
                  :icon-right="active ? 'menu-up' : 'menu-down'" />
            </template>

            <b-dropdown-item aria-role="listitem" @click="makePrimaryImage(item)" v-if="primaryImageId !== item.id">
              Make Primary Image
            </b-dropdown-item>
            <b-dropdown-item aria-role="listitem" @click="removeImage(item)">
              Remove
            </b-dropdown-item>
          </b-dropdown>

          <figure class="image">
            <b-image :class="{ 'primary-image': !isListing, 'primary-image-listing': isListing }" :src="item.image" @error="getAltImage"/>
          </figure>
          <div class="card-content is-overlay primary-image-tag">
            <span v-if="primaryImageId === item.id" class="tag is-primary is-pulled-right is-danger">Primary Image</span>
          </div>
        </b-carousel-item>
        <template #list="props">
          <b-carousel-list class="primary-image-listing"
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
import errorPopup from "../../components/errorPopup";

const serverURL = process.env.VUE_APP_SERVER_ADD;

export default {
  name: "ImageCarousel",
  props: ["data", "editable", "isListing"],

  data() {
    return {
      primaryImageId: this.data.primaryImageId,
      images: this.data.images,

      items: [],

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
    removeImage(item) {
      api.productImages.deleteProductImage(this.data.businessId, this.data.id, item.id)
          .then(() => {
            this.$emit('removeImage', this.data.id, item.id);
            this.items = this.items.filter(x => x.id !== item.id);

            // We're deleting the primary image so we need to update it
            if (this.items.length > 0 && this.primaryImageId === item.id) {
              this.makePrimaryImage(this.items[0]);
            }
          })
          .catch(() => {
            errorPopup.showPopup(this, ['Failed to delete image'])
          });
    },

    /**
     * Make an image the primary image
     * @param item Image item
     */
    async makePrimaryImage(item) {
      //Revert back to old primary image on fail
      const previousPrimaryId = this.primaryImageId;
      this.primaryImageId = item.id;
      api.productImages.putPrimaryProductImage(this.data.businessId, this.data.id, item.id)
        .then(this.$emit('updatePrimaryImage', this.data.id, item.id))
        .catch(() => {
          this.primaryImageId = previousPrimaryId;
          errorPopup.showPopup(this, ['Failed to assign image as primary'])
        });
    },

    getAltImage(event) {
      event.target.src = "https://bulma.io/images/placeholders/128x128.png";
    }
  },

  created() {
    this.businessId = this.$route.params.businessId;
  },

  mounted() {
    if (this.images) {
      let primary = null;

      for (let image of this.images) {
        let item = null;

        if (this.isListing) {
          item = {image: `${serverURL}/images/${image.id}`, id: image.id,};
        } else {
          item = {image: `${serverURL}/business/${this.businessId}/products/images/${image.id}`, id: image.id,};
        }

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

<style scoped>
@import "../../../public/styles/style.css";
</style>
