<template>
  <form action="">
    <div class="modal-card" style="margin: auto">
      <header class="modal-card-head panel-heading-blue">
        <p class="modal-card-title product-create-title">New Product Form</p>
        <button
            type="button"
            class="delete"
            @click="$emit('close')"/>
      </header>

      <section class="modal-card-body product-create-form" @submit.prevent="createProduct">
        <div class="product-create-required-fields-key">
          Required fields are marked with *
        </div>

        <div class="product-create-info">
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
            <b-numberinput min=0 step=0.01 v-model="recommendedRetailPrice"/>
          </b-field>

          <CatalogueUploadComponent/>

        </div>
      </section>

      <footer class="modal-card-foot product-create-button">
        <b-button type="submit is-info" @click="$emit('close')">Cancel</b-button>
        <b-button type="submit is-success" @click="createProduct">Add Product</b-button>
      </footer>
    </div>
  </form>
</template>

<script>
import error from "../../../components/errorPopup";
import api from "../../../api/api";
import logger from "../../../logger";
import validate from "../../../components/businesses/catalogue/validateProduct";
import CatalogueUploadComponent from "../../../components/businesses/catalogue/CatalogueUploadComponent";
import businesses from "../../../components/businesses/businesses";

const CreateProductComponent = {
  name: "CreateProductComponent",
  components: {CatalogueUploadComponent},
  data() {
    return {
      name: null,
      description: "",
      manufacturer: "",
      recommendedRetailPrice: null,
      dropFiles: [],
      errors: [],
      businessId: null,
    }
  },

  created() {
    this.businessId = this.$route.params.businessId;
  },

  methods: {
    /**
     * Carries out validation and submissions of product creation form
     */
    async createProduct() {
      this.errors = [];
      this.errors = validate.validateRequired(this.name, this.recommendedRetailPrice, this.dropFiles);
      if (this.errors.length !== 0) {
        error.showPopup(this, this.errors);
      } else {
        let payload = {
          name: this.name,
          description: this.description,
          manufacturer: this.manufacturer,
          recommendedRetailPrice: businesses.roundDecimalPlaces(this.recommendedRetailPrice),
        };

        api.businesses
            .createProduct(payload, this.businessId)
            .then(async (res) => {
              for (let i = 0; i < this.dropFiles.length; i++) {
                await api.productImages.postImage(this.businessId, res.data.productId, this.dropFiles[i])
                    .catch(postError => {
                      logger.getLogger().error(postError);
                      error.showPopupFromServer(this, postError);
                    })
              }

              this.$emit('refreshTable');
              this.$emit('close');
            })
            .catch(postError =>  {
              logger.getLogger().error(postError);
              error.showPopupFromServer(this, postError);
            });
      }
    },

    /**
     * Updates the list of images to be
     */
    updateDropFiles(imageList) {
      this.dropFiles = imageList;
    }
  }
};

export default CreateProductComponent;
</script>
