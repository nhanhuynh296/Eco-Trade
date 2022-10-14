<template>
  <div class="app-container">
    <NavBarComponent @update="actorChange"/>
    <transition name="fade">
        <router-view :key="updateBoolean"/>
    </transition>
  </div>
</template>

<script>
import NavBarComponent from "@/components/home/NavBarComponent";
import { isAllowedToRoute } from "@/routes";

export default {
  name: "mainPage",
  components: { NavBarComponent },

  data() {
    return {
      updateBoolean: false,
    };
  },

  methods: {
    actorChange() {
      if (!isAllowedToRoute(this.$router.currentRoute.meta)) {
        this.$router.push({name: 'home'});
      }

      this.updateBoolean = !this.updateBoolean;
    }
  },

  watch: {
    '$route.params': {
      handler() {
        this.actorChange();
      }
    }
  }
};
</script>
