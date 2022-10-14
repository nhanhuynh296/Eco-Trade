<template>
  <div class="business-home-main">
    <div class="business-home-general">
      <div class="business-home-container">
        <div class="business-sale-history-table">
          <b-table
              :data="data" striped hoverable sticky-header bordered>
            <b-table-column field="thumbnail" label="" v-slot="props" width="20px">
              {{ props.row.thumbnail }}
            </b-table-column>
            <b-table-column field="productCode" label="Product Code" v-slot="props">
              {{ props.row.productCode }}
            </b-table-column>
            <b-table-column field="productName" label="Product Name" v-slot="props">
              {{ props.row.productName }}
            </b-table-column>
            <b-table-column field="createdDate" label="Created Date" v-slot="props">
              {{ props.row.createdDate }}
            </b-table-column>
            <b-table-column field="soldDate" label="Sold Date" v-slot="props">
              {{ props.row.soldDate }}
            </b-table-column>
            <b-table-column field="liked" label="Stars/Likes" v-slot="props">
              {{ props.row.liked }}
            </b-table-column>
            <b-table-column field="quantity" label="Quantity" v-slot="props">
              {{ props.row.quantity }}
            </b-table-column>
            <b-table-column field="price" label="Price" v-slot="props">
              {{ props.row.price }}
            </b-table-column>
            <template #empty>
              <div class="has-text-centered">No records</div>
            </template>
          </b-table>

          <div class="pagination-info-center">
            <b-pagination class="catalogue-business-pagination-buttons"
                          backend-pagination
                          :total="paginationInfo.totalElements"
                          order="is-centered"
                          size="is-small"
                          :rounded="true"
                          :per-page="perPage"
                          :current.sync="page"
                          @change="onPageChange"

                          icon-prev="chevron-left"
                          icon-next="chevron-right">
            </b-pagination>
            Showing {{ lowerCount }} - {{ upperCount }} of {{ paginationInfo.totalElements }} results
          </div>
        </div>

      </div>
    </div>
  </div>

</template>

<script>
export default {
  name: "BusinessSalesHistoryComponent",
  data() {
    //todo remove data to populate
    const data =  [
      {
        thumbnail: 1,
        productCode: 123,
        productName: "abc",
        createdDate: new Date().toISOString(),
        soldDate: new Date().toISOString(),
        liked: 123,
        quantity: 10,
        price: 4.5
      },
      {
        thumbnail: 1,
        productCode: 123,
        productName: "abc",
        createdDate: new Date().toISOString(),
        soldDate: new Date().toISOString(),
        liked: 123,
        quantity: 10,
        price: 4.5
      },
      {
        thumbnail: 1,
        productCode: 123,
        productName: "abc",
        createdDate: new Date().toISOString(),
        soldDate: new Date().toISOString(),
        liked: 123,
        quantity: 10,
        price: 4.5
      },
      {
        thumbnail: 1,
        productCode: 123,
        productName: "abc",
        createdDate: new Date().toISOString(),
        soldDate: new Date().toISOString(),
        liked: 123,
        quantity: 10,
        price: 4.5
      },
      {
        thumbnail: 1,
        productCode: 123,
        productName: "abc",
        createdDate: new Date().toISOString(),
        soldDate: new Date().toISOString(),
        liked: 123,
        quantity: 10,
        price: 4.5
      },
      {
        thumbnail: 1,
        productCode: 123,
        productName: "abc",
        createdDate: new Date().toISOString(),
        soldDate: new Date().toISOString(),
        liked: 123,
        quantity: 10,
        price: 4.5
      },
      {
        thumbnail: 1,
        productCode: 123,
        productName: "abc",
        createdDate: new Date().toISOString(),
        soldDate: new Date().toISOString(),
        liked: 123,
        quantity: 10,
        price: 4.5
      },
      {
        thumbnail: 1,
        productCode: 123,
        productName: "abc",
        createdDate: new Date().toISOString(),
        soldDate: new Date().toISOString(),
        liked: 123,
        quantity: 10,
        price: 4.5
      },
      {
        thumbnail: 1,
        productCode: 123,
        productName: "abc",
        createdDate: new Date().toISOString(),
        soldDate: new Date().toISOString(),
        liked: 123,
        quantity: 10,
        price: 4.5
      },
      {
        thumbnail: 1,
        productCode: 123,
        productName: "abc",
        createdDate: new Date().toISOString(),
        soldDate: new Date().toISOString(),
        liked: 123,
        quantity: 10,
        price: 4.5
      },
      {
        thumbnail: 1,
        productCode: 123,
        productName: "abc",
        createdDate: new Date().toISOString(),
        soldDate: new Date().toISOString(),
        liked: 123,
        quantity: 10,
        price: 4.5
      },
      {
        thumbnail: 1,
        productCode: 123,
        productName: "abc",
        createdDate: new Date().toISOString(),
        soldDate: new Date().toISOString(),
        liked: 123,
        quantity: 10,
        price: 4.5
      },
      {
        thumbnail: 1,
        productCode: 123,
        productName: "abc",
        createdDate: new Date().toISOString(),
        soldDate: new Date().toISOString(),
        liked: 123,
        quantity: 10,
        price: 4.5
      },
      {
        thumbnail: 1,
        productCode: 123,
        productName: "abc",
        createdDate: new Date().toISOString(),
        soldDate: new Date().toISOString(),
        liked: 123,
        quantity: 10,
        price: 4.5
      },
      {
        thumbnail: 1,
        productCode: 123,
        productName: "abc",
        createdDate: new Date().toISOString(),
        soldDate: new Date().toISOString(),
        liked: 123,
        quantity: 10,
        price: 4.5
      },
      {
        thumbnail: 1,
        productCode: 123,
        productName: "abc",
        createdDate: new Date().toISOString(),
        soldDate: new Date().toISOString(),
        liked: 123,
        quantity: 10,
        price: 4.5
      },
    ]
    return {
      data,
      page: 1,
      perPage: 10,
      paginationInfo: {
        totalPages: 1,
        totalElements: 5
      },
      lowerCount: 1,
      upperCount: 5,
    }
  },
  methods: {
    onInitialize() {

    },
    onPageChange(page) {
      this.page = page;
      this.onInitialize();
    },
  }
}
</script>

<style scoped>

</style>