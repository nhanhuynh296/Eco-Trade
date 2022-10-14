<template>
  <form action="">
    <div class="modal-card"  v-bind:class="{'modal-card-with-image': this.primaryImageId !== null}" style="margin: auto">
      <header class="modal-card-head panel-heading-blue">
        <p class="modal-card-title product-create-title">Edit Product</p>
        <button
            type="button"
            class="delete"
            @click="$emit('close')"/>
      </header>

      <section class="modal-card-body product-edit-form" @submit.prevent="applyEdit">
        <div class="columns">
          <div v-if="primaryImageId !== null && data.images.length > 0" class="image-gallery-column column">
            <ImageCarousel :editable="true" v-bind:data="data" v-on="$listeners"/>
          </div>

          <div class="column">
            <div class="product-create-required-fields-key">
              Required fields are marked with *
            </div>

            <div class="product-modify-info">
              <b-field label="Name*" label-position="on-border">
                <b-input maxlength="70" label = "Name*" v-model="name" required/>
              </b-field>

              <b-field label="Description" label-position="on-border">
                <b-input maxlength="200" type="textarea" v-model="description" placeholder="About the product..."/>
              </b-field>

              <b-field label="Manufacturer" label-position="on-border">
                <b-input maxlength="100" v-model="manufacturer"/>
              </b-field>

              <b-field label="Recommended Retail Price*" label-position="on-border">
                <b-numberinput  min=0 step=0.01 v-model="recommendedRetailPrice"/>
              </b-field>

              <CatalogueUploadComponent/>

            </div>
          </div>
        </div>
      </section>

      <footer class="modal-card-foot product-create-button">
        <b-button type="submit is-info" @click="$emit('close')">Cancel</b-button>
        <b-button type="submit is-success" @click="applyEdit">Confirm</b-button>
      </footer>
    </div>
  </form>
</template>

<script>
import validate from "../../../components/businesses/catalogue/validateProduct";
import error from "../../../components/errorPopup";
import api from "../../../api/api";
import logger from "../../../logger";
import ImageCarousel from "../../../components/images/ImageCarouselComponent";
import CatalogueUploadComponent from "../../../components/businesses/catalogue/CatalogueUploadComponent";
import businesses from "../../../components/businesses/businesses"

export default {
  name: "CatalogueItemEditPopupComponent",
  components: {CatalogueUploadComponent, ImageCarousel},
  props: ["data"],

  data() {
    return {
      name: this.data.name,
      description: this.data.description,
      manufacturer: this.data.manufacturer,
      recommendedRetailPrice: this.data.recommendedRetailPrice,
      productId: this.data.id,
      primaryImageId: this.data.primaryImageId,
      images: this.data.images,
      gallery: false,
      dropFiles: [],
      errors: [],
      businessId: null
    }
  },

  created() {
    this.businessId = this.$route.params.businessId;
  },

  methods: {
    applyEdit() {
      this.errors = validate.validateRequired(this.name, this.recommendedRetailPrice)
      if (this.errors.length > 0) {
        error.showPopup(this, this.errors);
      } else {
        let payload = {
          name: this.name,
          description: this.description,
          manufacturer: this.manufacturer,
          recommendedRetailPrice: businesses.roundDecimalPlaces(this.recommendedRetailPrice),
        };

        api.businesses.modifyProduct(payload, this.businessId, this.productId)
            .then(async () => {
              if (this.dropFiles.length > 0) {
                if (this.data.images.length < 10) {
                  // The maximum amount of images that can be added to this product is determined
                  let totalImagesAdded = 10 - this.data.images.length;
                  if (this.dropFiles.length < totalImagesAdded) {
                    totalImagesAdded = this.dropFiles.length;
                  }
                  for (let i = 0; i < totalImagesAdded; i++) {
                    await api.productImages.postImage(this.businessId, this.productId, this.dropFiles[i])
                        .catch(postError => {
                          logger.getLogger().error(postError);
                          error.showPopupFromServer(this, postError);
                        })
                  }

                  // Warns the user if only some of their images were uploaded due to the image limit
                  if (totalImagesAdded < this.dropFiles.length) {
                    this.errors.push("A product can only have 10 images");
                    if (totalImagesAdded > 1) {
                      this.errors.push("Only the first " + totalImagesAdded + " images were added");
                    } else {
                      this.errors.push("Only the first image was uploaded");
                    }
                    error.showPopup(this, this.errors);
                  }
              } else {
                this.errors.push("A product can only have 10 images");
                error.showPopup(this, this.errors);
              }
            }

              this.$emit('refreshTable');
              this.$emit('close');
            })
            .catch(putError =>  {
              logger.getLogger().warn(putError);
              error.showPopupFromServer(this, putError);
            });
      }
    },

    switchGallery(value) {
      this.gallery = value
      if (value) {
        return document.documentElement.classList.add('is-clipped')
      } else {
        return document.documentElement.classList.remove('is-clipped')
      }
    },

    /**
     * Updates the list of images to be
     */
    updateDropFiles(imageList) {
      this.dropFiles = imageList;
    }
  }
}
</script>
