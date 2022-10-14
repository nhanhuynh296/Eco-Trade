<template>
  <div class="marketplace-main">
    <div class="marketplace-general">
      <div class="marketplace-main-container">
        <b-tabs position="is-centered"
                type="is-boxed"
                :animated="true"
                class="market-tabs-container"
                multiline
                expanded>
          <b-tab-item label="For Sale" value="forSale" icon="clipboard-arrow-up-outline">
            <MarketSectionComponent section="ForSale"/>
          </b-tab-item>

          <b-tab-item label="Wanted" value="wanted" icon="clipboard-arrow-down-outline">
            <MarketSectionComponent section="Wanted"/>
          </b-tab-item>

          <b-tab-item label="Exchange" value="exchange" icon="clipboard-flow-outline">
            <MarketSectionComponent section="Exchange"/>
          </b-tab-item>
        </b-tabs>
      </div>
    </div>
  </div>

</template>

<script>
import MarketSectionComponent from "../components/marketplace/MarketSectionComponent";

export default {
  name: "Marketplace",
  components: { MarketSectionComponent },
  title: 'Marketplace',
}
</script>

<style>
@import '../../public/styles/style.css';
@import '../../public/styles/home.css';
@import '../../public/styles/marketplace.css';
</style>
