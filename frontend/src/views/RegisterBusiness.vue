<template>
  <div class="business-register-main">
    <div class="business-register-general">
      <div class="business-register-main-container">
        <RegisterBusinessComponent/>
      </div>
    </div>
  </div>
</template>

<script>
import RegisterBusinessComponent from "@/components/businesses/BusinessRegisterComponent";

export default {
  name: "Business",
  components: {RegisterBusinessComponent},
  title: 'Register Business',
}
</script>

<style>
@import '../../public/styles/business.css';
</style>
