<template>

  <div class="modal-card" style="margin: auto">
    <header class="modal-card-head">
      <p class="modal-card-title">Change current card currency</p>
      <button
          type="button"
          class="delete"
          @click="cancel"/>
    </header>
    <section class="modal-card-body">
      {{ Message }}
    </section>
    <footer class="modal-card-foot ">
      <b-button
          type="is-info is-danger"
          label="No"
          @click="cancel"/>
      <b-button
          type="is-info is-success"
          label="Yes"
          @click="confirm"/>
    </footer>
  </div>

</template>

<script>

export default {
  name: "UserChangeCountryComponent",
  data() {
    return {
      Message: null,
    };
  },
  props: {
    currencyData: null,
  },
  mounted() {
    this.Message = `Your new currency is ${this.currencyData.name} (${this.currencyData.symbol} ${this.currencyData.code}). Do you want to apply this currency to all of your currently active products?`;
  },
  methods: {
    confirm() {
      this.$emit('confirm');
      this.$emit('close');
    },
    cancel() {
      this.$emit('cancel');
      this.$emit('close');
    }
  }
}
</script>

