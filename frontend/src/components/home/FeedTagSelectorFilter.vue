<template>
  <b-field label="Filter By Tag" label-position="on-border">
    <b-select placeholder="Select a Tag" expanded v-model="filter">
      <option :value="none">None</option>
      <option value="REQUIRES_ATTENTION">Requires Attention</option>
      <option value="HIGH_PRIORITY">High Priority</option>
      <option value="MEDIUM_PRIORITY">Medium Priority</option>
      <option value="LOW_PRIORITY">Low Priority</option>
      <option value="INTERESTING">Interesting</option>
    </b-select>
  </b-field>
</template>

<script>
export default {
  name: "FeedTagSelectorFilter",
  data() {
    return {
      //Weird quirk with trying to set null as the option for selecting nothing, hence this
      none: null,
      filter: this.None,
    }
  },

  watch: {

    /**
     * Emit an event to filter based on selected tag
     */
    filter() {
      this.$emit("filterTag", this.filter);
    }
  }
}
</script>

