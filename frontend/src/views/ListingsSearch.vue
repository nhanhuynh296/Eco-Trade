<template>
  <div class="user-search-main">
    <div class="user-search-general">
      <ListingsSearchResultComponent/>
    </div>
  </div>
</template>

<script>
import ListingsSearchResultComponent
  from "@/components/businesses/listings/listingsSearch/ListingsSearchResultComponent";

export default {
  name: "ListingsSearch",
  components: {ListingsSearchResultComponent},
  title: 'Listings Search',

}
</script>

<style scoped>
@import "../../public/styles/style.css";
@import "../../public/styles/userSearch.css";
@import "../../public/styles/listingSearch.css";
@import '../../public/styles/marketplace.css';
</style>
