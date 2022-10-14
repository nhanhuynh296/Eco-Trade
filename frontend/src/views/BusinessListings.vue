<template>
  <div class="business-home-main">
    <div class="business-home-general">
      <div class="business-home-container">
        <ListingsComponent/>
      </div>
    </div>
  </div>
</template>

<script>

import ListingsComponent from "@/components/businesses/listings/ListingsComponent";

export default {
  name: "Listings",
  components: {ListingsComponent},
}
</script>
