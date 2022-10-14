<template>
  <div>
    <div v-if="text.length > 60">
      <div class="item-body">
        <p>{{ getSubstring(text, 0, 15) }} <span v-if="!showMore">...</span><span v-else>{{ getSubstring(text, 15) }}</span></p>
      </div>
      <b-button type="is-info" class="item-footer" @click="showMore = !showMore" size="is-small">
        {{ showMore ? "Show Less" : "Show More" }}
      </b-button>
    </div>
    <div v-else>
      <div class="item-body">
        <p>{{ text }}</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "ExpandableTextComponent",
  props: ['text'],

  data() {
    return {
      showMore: false
    }
  },

  methods: {
    /**
     * Get substring from a string
     *
     * @param string String to slice
     * @param start Start index (Usually 0)
     * @param count Count of words to grab (Optional, null if go to end)
     * @return string Words
     */
    getSubstring(string, start, count) {
      return string.split(/\s+/).slice(start, count).join(" ");
    }
  }
}
</script>
