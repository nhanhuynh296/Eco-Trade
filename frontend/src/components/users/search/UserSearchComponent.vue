<template>
  <div>
    <b-field class="user-search-bar-field">
      <b-input placeholder="Search user..."
               type="search"
               icon="magnify"
               v-model="query"
               icon-clickable
               @keyup.native.enter="search"
               @icon-click="search"
               class="user-search-bar"
               expanded>
      </b-input>
      <p class="control">
        <b-button class="user-search-bar-button" type="is-primary" label="Search" @click="search"/>
      </p>
    </b-field>
  </div>
</template>

<script>

const UserSearchComponent = {
  name: "userSearchBar",

  data() {
    return {
      query: null
    };
  },

  methods: {

    /**
     * Passes on the query to the parent class
     * and call the search parent method
     */
    search() {
      this.$emit('search', this.query);
    }
  }
};

export default UserSearchComponent;
</script>
