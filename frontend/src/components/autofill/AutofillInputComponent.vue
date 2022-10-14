<template>
  <b-autocomplete
      ref="autocomplete"
      :disabled="disabled"
      v-bind="$attrs"
      v-on="$listeners"
      v-model="input"
      :data="filteredDataArray"
      open-on-focus
      :placeholder="placeholder"
      :icon="icon"
      :clearable="clearable"
      :check-infinite-scroll="infScroll"
      @infinite-scroll="infiniteScrollFunctionWrapper"
      @select="option => this.selected = option">
      <template #empty>No results found</template>
  </b-autocomplete>
</template>

<script>
export default {
  name: "AutofillInputComponent",
  data() {
    return {
      input: this.value ?? "",
      selected: null
    };
  },
  props: {
    doFilter: null,
    inputListData: null,
    preSelect: null,
    disabled: null,
    value: null, // to use with v-model
    placeholder: null,
    icon: null,
    infScroll: null,
    infiniteScrollFunction: null,
    clearable: null,
  },

  mounted() {
    this.$refs.autocomplete.setSelected(this.preSelect);
  },

  watch: {
    value(value) {
      this.input = value;
    },
    newValue(value) {
      this.$emit('input', value)
      this.input = value;
    }
  },

  methods: {
    infiniteScrollFunctionWrapper() {
      if (this.infiniteScrollFunction !== null) {
        return this.infiniteScrollFunction();
      } else {
        return () => {};
      }
    }
  },


  computed: {

    /**
     * Filter the data to match exact strings
     *
     * @returns Array{String} List of string matches
     */
    filteredDataArray() {
      if (this.doFilter) {
        return this.inputListData.filter((option) => {
          return option
              .toString()
              .toLowerCase()
              .indexOf(this.input.toLowerCase()) >= 0
        })
      } else {
        return this.inputListData;
      }
    }
  }
}
</script>
