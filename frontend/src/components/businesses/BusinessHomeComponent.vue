<template>
  <div class="business-home-main">
    <div class="business-home-general">
      <div class="business-home-container">
        <b-tabs position="right"
                type="is-boxed"
                v-model="activeTab"
                :animated="false"
                class="is-flex is-flex-direction-column"
                expanded>

          <b-tab-item label="Catalogue" value="catalogue" icon="book-open">
            <b-tooltip class="custom-tooltip" multilined slot="header" :label="catalogueString" position="is-bottom">
              <template>
                <b-icon class="tab-icons" icon="book-open"></b-icon>
                <strong>Catalogue</strong>
              </template>
            </b-tooltip>

            <CatalogueComponent :key="activeTab"/>
          </b-tab-item>

          <b-tab-item value="inventory">
            <b-tooltip class="custom-tooltip" multilined slot="header" :label="inventoryString" position="is-bottom">
              <template>
              <b-icon class="tab-icons" icon="archive" style="align-items: center"></b-icon>
              <strong>Inventory</strong>
              </template>
            </b-tooltip>

            <InventoryComponent :key="activeTab"/>
          </b-tab-item>


          <b-tab-item label="Listings" value="listings" icon="clipboard-text-multiple">

            <b-tooltip class="custom-tooltip" multilined slot="header" :label="listingString" position="is-bottom">
              <template>
                <b-icon class="tab-icons" icon="clipboard-text-multiple"></b-icon>
                <strong>Listings</strong>
              </template>
            </b-tooltip>

            <ListingsComponent :key="activeTab"/>
          </b-tab-item>
        </b-tabs>
      </div>
    </div>
  </div>
</template>

<script>
import InventoryComponent from "@/components/businesses/inventory/InventoryComponent";
import CatalogueComponent from "@/components/businesses/catalogue/CatalogueComponent";
import ListingsComponent from "@/components/businesses/listings/ListingsComponent";

const BusinessHomeComponent = {
  name: "BusinessHome",
  components: {CatalogueComponent, InventoryComponent, ListingsComponent},

  data() {
    return {
      activeTab: "catalogue",
      businessId: this.$route.params.businessId,

      catalogueString: "Each item represents a product that your business can potentially stock and sell.",
      inventoryString: "Each item represents a batch of products that are in stock, ready to be listed and sold.",
      listingString: "Each item represents a product from your inventory that has be listed for sale on the business listings page."
    }
  }
};

export default BusinessHomeComponent;
</script>

<style>
@import '../../../public/styles/catalogue.css';
@import '../../../public/styles/inventory.css';
@import '../../../public/styles/home.css';
</style>
