<template>
  <b-table
    :data="userData"
    ref="table"
    class="is-fullwidth business-admin-table"
    striped
    height="15rem"
    @click="onRowClick"
    hoverable
    :row-class="(row) => row.id === primaryAdmin && 'is-info'"
  >

    <b-table-column field="firstName" label="First Name" v-slot="props">
        <p class="user-search-link"> {{props.row.firstName}}</p>
    </b-table-column>

    <b-table-column field="lastName" label="Last Name" v-slot="props">
      <p class="user-search-link">{{ props.row.lastName }}</p>
    </b-table-column>
  </b-table>
</template>

<script>
export default {
  name: "BusinessAdminsComponent",
  props: ["userData", "primaryAdmin"],
  data() {
    return {
      rowSelected: null,
    }
  },
  methods: {
    /**
     * On row clicked
     * @param user
     */
    onRowClick(user) {
      this.$router.push({name: 'userProfile', params: {userId: user.id} })
    }
  }
}
</script>

<style>
  tr.is-info {
    overflow-wrap: anywhere;
    background: #fddb57;
    color: #fff;
  }
</style>
