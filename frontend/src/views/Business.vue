<template>
  <div>
    <BusinessComponent/>
  </div>
</template>

<script>
import BusinessComponent from "@/components/businesses/BusinessComponent";

export default {
  components: {BusinessComponent},
  title: 'Business Profile'
}
</script>

<style scoped>
@import '../../public/styles/business.css';
</style>
