<template>
  <form action="">
    <div class="model-card">
      <header class="modal-card-head panel-heading-greenish">
        <p class="modal-card-title product-create-title"> My Profile Images</p>
        <button
            type="button"
            class="delete"
            @click="$emit('close')"/>
      </header>

      <section class="modal-card-body profile-upload-img-wrapper" @submit.prevent="updateProfileImages">
        <div v-if="primaryImageId !== null && data.images.length > 0" class="column profile-images-carousel">
          <ProfileCarouselComponent v-bind:data="data" :editable="editable" :user="user"  v-on="$listeners" @updatePrimaryImage="updateProfileImage($event)"/>
        </div>
        <CatalogueUploadComponent style="min-height: 50%"/>
      </section>

      <footer class="modal-card-foot product-create-button">
        <b-button type="submit is-info" @click="$emit('close')">Cancel</b-button>
        <b-button type="submit is-success" @click="updateProfileImages">Save</b-button>
      </footer>
    </div>
  </form>
</template>

<script>
import ProfileCarouselComponent from "../../components/images/ProfileCarouselComponent";
import error from "../../components/errorPopup";
import CatalogueUploadComponent from "../../components/businesses/catalogue/CatalogueUploadComponent";
import api from "../../api/api";
import logger from "../../logger";

const ProfileImagesComponent = {
  name: "profileCarousel",
  components: {CatalogueUploadComponent, ProfileCarouselComponent},
  props: ["data", "user"],

  data() {
    return {
      primaryImageId: this.data.primaryImageId,
      errors: [],
      newImages: [],
      editable: true
    }
  },

  created() {
    this.editable = this.user.id.toString() === localStorage.getItem("id");
  },

  methods: {
    /**
     * Removes files that are too large or if more than 10 are uploaded.
     */
    checkImages() {
      // List of items to remove (by index)
      const indices = [];

      for (let i = 0; i < this.newImages.length; i++) {
        // 1048576 is the max size accepted by backend
        if (this.newImages[i].size > 1048576) {
          this.errors.push(`The file '${this.newImages[i].name}' is greater than 1mb!`);
          // Store index of the large file for removal below
          indices.push(i);
        }
      }

      // Remove the files that are larger than 1mb
      this.newImages = this.newImages.filter((_, index) => !indices.includes(index));

      if (this.newImages.length > 10) {
        this.errors.push('You may upload at most 10 files!');
        this.newImages.splice(10, this.newImages.length - 10);
      }

      if (this.errors.length !== 0) {
        error.showPopup(this, this.errors);
        this.errors = [];
      }
    },
    /**
     * Updates the list of images to be
     */
    updateDropFiles(imageList) {
      this.newImages = imageList;
    },

    /**
     * Calls the api to update user's images
     */
    async updateProfileImages() {
      for (let i = 0; i < this.newImages.length; i++) {
        await api.users.postImage(parseInt(localStorage.getItem("id")), this.newImages[i])
            .then((res) => {
              this.$emit('addImage', res.data.imageId);
            })
            .catch(postError => {
              logger.getLogger().error(postError);
              error.showPopupFromServer(this, postError);
            })
      }
      this.$emit('close');
    },

    updateProfileImage(id) {
      this.primaryImageId = id;
      this.$emit("updatePrimaryImage", id);
    },

    /**
     * Deletes a file from the newImages data list at a certain index
     */
    deleteImage(index) {
      this.newImages.splice(index, 1);
    }
  }
}

export default ProfileImagesComponent;
</script>
