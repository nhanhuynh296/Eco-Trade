<template>
  <div v-if="tableData" class="tile is-parent sales-report-table-wrapper">
    <div class="tile is-child panel is-dark is-flex is-flex-direction-column">
      <div class="title panel-heading title is-marginless"> Sales Breakdown</div>
      <b-table
          :data="tableData"
          :striped="true"
          class="p-2 is-flex-grow-1 is-flex is-flex-direction-column sales-report-table"
          sticky-header
          :loading="tableLoading"
      >
        <b-table-column field="period" label="Period" v-slot="props">
          {{ props.row.period }}
        </b-table-column>

        <b-table-column field="dateRange" label="Date range" v-slot="props" >
          {{ formatDate(props.row.initialDate, props.row.finalDate)}}
        </b-table-column>

        <b-table-column field="numSales" label="Number of sales" v-slot="props">
          {{ props.row.totalSales }}
        </b-table-column>

        <b-table-column field="totalRevenue" :label="`Total revenue ` + (businessCurrency.symbol ? `(${businessCurrency.symbol}${businessCurrency.code})` : ``)" sortable v-slot="props">
          {{ props.row.totalRevenue }}
        </b-table-column>

      </b-table>
      <div class="pagination-info-center" >
        <b-pagination class="catalogue-business-pagination-buttons"
                      backend-pagination
                      :total="paginationInfo.totalElements"
                      order="is-centered"
                      size="is-small"
                      :rounded="true"
                      :per-page="paginationInfo.perPage"
                      :current.sync="paginationInfo.page"
                      @change="onPageChange"

                      icon-prev="chevron-left"
                      icon-next="chevron-right">
        </b-pagination>
        Showing {{paginationInfo.lowerCount}} - {{paginationInfo.upperCount}} of {{paginationInfo.totalElements}} results
      </div>
    </div>
  </div>
</template>

<script>

export default {
  name: "SalesReportTableComponent",
  props: {
    granularity: null,
    tableData: null,
    paginationInfo: null,
    businessCurrency: {},
    tableLoading: null,
  },

  methods: {

    /**
     * Formats the date into the current regions locale
     *
     * @param startDate
     * @param endDate
     * @return {string} String formatted representation
     */
    formatDate(startDate, endDate) {
      return `${new Date(startDate).toLocaleDateString('en-GB')} - ${new Date(endDate).toLocaleDateString('en-GB')}`;
    },

    onPageChange(page){
      this.$emit('pageChange', page)
    }
  }
}

</script>
