<template>
  <div class="home-container">
    <div class="user-home-general">
      <UserHomeComponent/>
    </div>
  </div>
</template>

<script>
import UserHomeComponent from "@/components/home/UserHomeComponent.vue";

export default {
  name: "home",
  components: {UserHomeComponent},
  title: 'Home',

  /**
   * Initialized
   */
  created() {
    this.setChecks();
  },

  methods: {

    /**
     * Redirect if business
     */
    setChecks() {
      if (localStorage.getItem("user-type") !== "user") {
        const businessId = localStorage.getItem("business-id");
        this.$router.push({ name: "businessHome", params: { businessId: businessId } });
      }
    }
  },
};
</script>

<style scoped>
@import '../../public/styles/home.css';
</style>
