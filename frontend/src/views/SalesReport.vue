<template>
  <div class="business-home-main">
    <div class="business-home-general">
      <div class="business-home-container tile is-ancestor mb-6">
        <div class="tile is-vertical full-width">
          <div class="panel is-marginless is-info">
            <div class="panel-heading has-text-centered">
              {{ business.name }} Sales Report
            </div>
          </div>

<!--          FILTERS-->
          <div class="tile is-parent is-justify-content-space-between ">
<!--            DATE PICKERS-->
            <div class="tile is-justify-content-center is-mobile panel is-flex-wrap-wrap is-primary mb-2 is-almost-6">
              <div class="title panel-heading is-marginless full-width p-3 is-flex is-align-items-center">
                Select Period
                <b-switch v-model="isCustomDate"
                          left-label
                          type="is-info"
                          passive-type="is-info"
                          size="is-small"
                          class="is-justify-content-space-between mx-3"
                >
                </b-switch>
                Custom Period
              </div>
              <div v-if="isCustomDate" class="full-width columns m-1 has-text-centered is-flex-wrap-wrap">
                <div class="is-flex is-flex-direction-row is-align-items-center is-flex-grow-1 mx-2">
                  <b-datepicker editable rounded range :first-day-of-week="1"
                                :date-formatter="formatter" class="is-flex-grow-1 date-range-picker"
                                icon="calendar-today" placeholder="Date Range"
                                v-model="dateRange"
                                :max-date="maxDate"
                                :min-date="minDate"
                                :years-range="[-150, 150]"
                                trap-focus
                  />
                  <b-button type="is-danger" class="is-align-self-center my-3 clear-date-button" @click="clearDate">
                    <b-icon class="clear-date-icon" icon="close-circle"></b-icon>
                  </b-button>
                </div>
                <b-button type="is-success" rounded class="m-2 is-pulled-left width-17" @click="getSalesReportCustom">
                  Apply
                </b-button>
              </div>
              <div v-else class="full-width columns m-1 has-text-centered is-align-items-center is-flex-wrap-wrap">

                <b-field label="Year" label-position="on-border" class="m-2 width-17">
                  <AutofillInputComponent placeholder="Select"
                                          rounded
                                          :clearable="true"
                                          icon="calendar"
                                          :inf-scroll="true" v-model="year"
                                          :infinite-scroll-function="getRestrictedYears"
                                          :input-list-data=years />
                </b-field>

                <b-field label="Month" label-position="on-border" class="m-2 width-17">
                  <AutofillInputComponent placeholder="Select"
                                          rounded
                                          :clearable="true"
                                          icon="calendar"
                                          v-model="month"
                                          :disabled="!canSelectMonth"
                                          :input-list-data=months />
                </b-field>

                <b-field label="Week" label-position="on-border" class="m-2 width-17">
                  <AutofillInputComponent placeholder="Select"
                                          rounded
                                          :clearable="true"
                                          icon="calendar"
                                          v-model="week"
                                          :disabled="!canSelectWeeks"
                                          :input-list-data=weeks />
                </b-field>
                <p>or</p>
                <b-field label="Day" label-position="on-border" class="m-2 width-17">
                  <AutofillInputComponent placeholder="Select"
                                          rounded
                                          :clearable="true"
                                          icon="calendar"
                                          v-model="day"
                                          :disabled="!canSelectDays"
                                          :input-list-data=days />
                </b-field>

                <b-button type="is-success" rounded class="m-2 width-17" @click="getSalesReportDefault">
                  Apply
                </b-button>
              </div>
            </div>
            <!--            RADIO BUTTONS-->
            <div class="tile is-5 is-justify-content-center is-mobile panel is-flex-wrap-wrap is-primary mb-2 ">
              <div class="title panel-heading is-marginless full-width p-3 is-min-content" >Granularity</div>
              <div class="full-width columns m-1 is-flex-wrap-wrap">
                <b-radio v-model="granularity"
                         class="column"
                         name="annually"
                         native-value="Annually"
                         @click.native="changeGranularity('Annually')"
                >
                  Annually
                </b-radio>
                <b-radio v-model="granularity"
                         class="column"
                         name="monthly"
                         native-value="Monthly"
                         @click.native="changeGranularity('Monthly')"
                >
                  Monthly
                </b-radio>
                <b-radio v-model="granularity"
                         class="column"
                         name="weekly"
                         native-value="Weekly"
                         @click.native="changeGranularity('Weekly')">
                  Weekly
                </b-radio>
                <b-radio v-model="granularity"
                         class="column"
                         name="daily"
                         native-value="Daily"
                         @click.native="changeGranularity('Daily')"
                >
                  Daily
                </b-radio>
              </div>
            </div>
          </div>

          <!--          TABLES AND GRAPHS-->
          <b-tabs v-model="activeTab" type="is-boxed" class="sales-report-tabs">
            <b-tab-item label="Sales Report" icon="file-table"></b-tab-item>
            <b-tab-item label="Graphs" icon="chart-bar"></b-tab-item>
          </b-tabs>

          <SalesReportTableComponent :table-loading="tableLoading"
                                     :table-data="reportData"
                                     v-if="activeTab === 0"
                                     :pagination-info="paginationInfo"
                                     @pageChange="customPageChange"
                                     :business-currency="businessCurrency"/>

          <div v-if="activeTab === 1" >
            <div class="is-flex is-flex-direction-row is-justify-content-center my-5">
              <b-tabs type="is-toggle" class="revenue-sales-tabs" v-model="graphToggle" v-on:click="changeDataset">
                <b-tab-item label="Total Revenue"></b-tab-item>
                <b-tab-item label="Total Sales"></b-tab-item>
              </b-tabs>
            </div>

            <div class="is-flex is-justify-content-center is-align-items-center my-3">
              <b-button icon-left="history" type="is-info" class="mr-3" @click="popCallStack">
                Previous
              </b-button>

              <b-tooltip label="Clicking on a bar/pie here will also change the sales report table" type="is-info">
                <b-field>
                  <b-checkbox v-model="linkTable" type="is-info">Link Report</b-checkbox>
                </b-field>
              </b-tooltip>
            </div>

            <SalesReportBar :chartData="chartData" :options="barChartOptions" ref="graph"/>

            <div class="tile is-parent is-justify-content-space-between m-6 ">
              <div class="tile is-child is-6">
                <SalesReportLine :chartData="chartData" :options="barChartOptions" ref="graph"/>
              </div>
              <div class="tile is-child is-6">
                <SalesReportPie :chartData="chartData" :options="pieChartOptions" ref="graph"/>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import SalesReportTableComponent from "@/components/businesses/salesReport/SalesReportTableComponent";
import getFilterData from "@/components/businesses/salesReport/getFilterData";
import AutofillInputComponent from "@/components/autofill/AutofillInputComponent";
import logger from "@/logger";
import api from "@/api/api";
import SalesReportBar from "@/components/businesses/salesReport/SalesReportBar";
import SalesReportPie from "@/components/businesses/salesReport/SalesReportPie";
import SalesReportLine from "@/components/businesses/salesReport/SalesReportLine";
import paginationInfo from "@/components/paginationInfo";
import currencyQuery from "@/api/restcountries/currencyQuery";
import errorPopup from "@/components/errorPopup";

export default {
  name: "SalesReport",
  components: {SalesReportLine, SalesReportPie, SalesReportBar, AutofillInputComponent, SalesReportTableComponent},
  title: 'Sales Report',

  data() {
    return {
      reportData: [],
      maxDate: new Date(),
      minDate: new Date(2010, 0, 1),

      business: {},
      businessCurrency: {
        symbol: "",
        code: ""
      },
      businessId: this.$route.params.businessId,

      granularity: 'Daily',
      isCustomDate: false,
      curDate: new Date(),
      years: getFilterData.getYears(new Date()),
      months: getFilterData.months,
      day_list: getFilterData.day_list,

      //If using custom date, this is a list of all dates in the custom range
      dateRange: [],
      //If not using a custom date these are the entered in date values
      year: null,
      month: null,
      week: null,
      day: null,

      activeTab: 0,

      graphToggle: 0,
      showChart: false,
      chartData: [],

      pieChartOptions: {responsive: true, maintainAspectRatio: false, onClick: this.drill,},
      barChartOptions: { responsive: true, maintainAspectRatio: false, onClick:this.drill, legend: {display: false} },
      graphLabels: [],
      totalSalesDataset: [],
      totalRevenueDataset: [],
      datasetDates: [],
      tableLoading: false,
      linkTable: true,
      callStack: [],

      paginationInfo: {
        page: 1,
        perPage: 10,
        totalPages: 0,
        totalElements: 0,
        lowerCount: 0,
        upperCount: 0,
      },
    }
  },

  created() {
    api.businesses.getBusiness(this.businessId)
      .then(res => {
        this.business = res.data;

        let startDate = new Date();
        startDate.setMonth(0);
        this.year = startDate.getFullYear();
        let endDate = new Date(startDate.getFullYear(), 11, 31);
        this.dateRange = [startDate, endDate];

        this.getSalesReportCustom();
      })
      .catch(err => {
        logger.getLogger().warn(err);
        this.$router.push({name: "home"});
      });

    currencyQuery.getCurrentBusinessCurrency(this.businessId)
        .then(res => this.businessCurrency = res)
        .catch();
  },

  computed: {
    /**
     * Get the day count in a month
     */
    days: function () {
      if (!this.canSelectDays) {
        return [];
      }
      return this.getDaysOfMonth(this.year, this.month, this.week);
    },

    /**
     * Get the weeks in a month
     */
    weeks: function () {
      let numWeeks = this.weekCount(getFilterData.months.indexOf(this.month));
      let res = [];
      for (let i = 1; i < numWeeks; i++) {
        res.push(`Week ${i}`);
      }
      return res;
    },

    /**
     * Returns false in all cases except where year, month and week are all selected.
     * Indicates that the user is allowed to select a day.
     */
    canSelectDays: function () {
      return (this.year !== null && this.month !== null &&
          this.year > 0 && this.month.length > 0);
    },

    /**
     * Returns false in all cases except where month and week are both selected.
     * Indicates that the user is allowed to select a week.
     */
    canSelectWeeks: function () {
      return (this.year !== null && this.month !== null &&
          this.year > 0 && this.month.length > 0);
    },

    /**
     * Returns false in all cases except where year is selected.
     * Indicates that the user is allowed to select a month.
     */
    canSelectMonth: function () {
      return (this.year !== null && this.year > 0);
    },
  },

  methods: {
    /**
     * Takes an int year, a string month and a string week.
     * Returns a list of all days in the form ["Sunday", ..., "Saturday"]
     * in the given year, month and week.
     */
    getDaysOfMonth(year, month) {
      month = this.months.findIndex(m => m === month);
      let selectedDate = new Date(year, month, 3);
      let daysIndex = selectedDate.getDay();
      let dayList = (this.day_list.slice(daysIndex)).concat(this.day_list.slice(0, daysIndex));
      for (let j = 0; j < 5; j++) {
        dayList = dayList.concat(dayList)
      }
      let daysInMonth = getFilterData.getDaysInMonth(this.year, getFilterData.months.indexOf(this.month)).length;
      let day;
      for (let i = 0; i < daysInMonth; i++) {
        day = `${i + 1}`;
        if (day.length < 2) {
          day = "0" + day
        }
        dayList[i] = day + " " + dayList[i]
      }
      if (dayList.length > daysInMonth) {
        dayList = dayList.slice(0, daysInMonth)
      }
      return dayList;
    },

    /**
     * Get as many years as we need for the datepicker range
     */
    getRestrictedYears() {
      this.curDate.setFullYear(this.curDate.getFullYear() - 50);
      this.years = this.years.concat(getFilterData.getYears(this.curDate));
    },

    /**
     * Get the week count in a month
     *
     * @param month_number
     * @return {number}
     */
    weekCount(month_number) {
      let firstOfMonth = new Date(this.year, month_number, 1);
      let lastOfMonth = new Date(this.year, month_number + 1, 0);
      let used = firstOfMonth.getDay() + 6 + lastOfMonth.getDate();
      return Math.ceil(used / 7);
    },

    /**
     * Formats the date into the current regions locale
     *
     * @param date Array of two dates, start and end
     * @return {string} String formatted representation
     */
    formatter(date) {
      if (date.length > 0) {
        return `${date[0].toLocaleDateString('en-GB')} - ${date[1].toLocaleDateString('en-GB')}`;
      }
    },

    /**
     * Checks the granularity as it's formatted for the frontend combo-box
     * and returns the granularity in the format needed for the backend
     */
    getGranularity() {
      if (this.granularity === "Annually") {
        return "YEAR"
      } else if (this.granularity === "Monthly") {
        return "MONTH"
      } else if (this.granularity === "Weekly") {
        return "WEEK"
      } else {
        return "DAY"
      }
    },

    /**
     * Get request for sales report data using the custom date picker.
     * Formats the given start and end dates into the format required by the backend
     * and then calls an api endpoint to retrieve the corresponding data and saves it.
     */
    getSalesReportCustom() {
      let startDate, endDate;
      [startDate, endDate] = this.getDateRange();
      this.getSalesReportData(startDate, endDate);
      this.getSalesGraphData(startDate, endDate, true);
    },

    /**
     * Get the formatted started date and end date of data request query
     * @returns {string[]} index 0: start date, index 1: end date
     */
    getDateRange() {
      let originalStartDate = this.dateRange[0];
      let startDate = new Date(originalStartDate.getFullYear(), originalStartDate.getMonth(), originalStartDate.getDate() + 1).toISOString().substring(0, 10);
      let originalEndDate = this.dateRange[1];
      let endDate = new Date(originalEndDate.getFullYear(), originalEndDate.getMonth(), originalEndDate.getDate() + 1).toISOString().substring(0, 10);

      return [startDate, endDate]
    },

    /**
     * Populate the report table from backend
     * @param startDate start of period
     * @param endDate end of period
     */
    getSalesReportData(startDate, endDate) {
      this.tableLoading = true;
      let businessId = this.$route.params.businessId;
      api.sales.getSalesReport(businessId, this.getGranularity(), startDate, endDate, this.paginationInfo.page-1, this.paginationInfo.perPage)
          .then((result) => {
            this.reportData = result.data.paginationElements;
            this.getPaginationInfo(result.data);
            this.tableLoading = false;
          })
          .catch(err => {
            logger.getLogger().warn(err);
            errorPopup.showPopupFromServer(this, err, false);
          });
    },

    /**
     * Populate the graph datasets from backend
     * @param startDate start of period
     * @param endDate end of period
     * @param addToCallStack add this call to the call stack if true
     */
    getSalesGraphData(startDate, endDate, addToCallStack) {
      let granularity = this.granularity;
      api.sales.getSalesGraph(this.businessId, startDate, endDate, this.getGranularity())
          .then(res => {
            this.graphLabels = res.data.labels;
            this.totalSalesDataset = res.data.datasetSale;
            this.totalRevenueDataset = res.data.datasetRevenue;
            this.datasetDates = res.data.dates;
            if (addToCallStack) {
              this.callStack.push({
                startDate,
                endDate,
                granularity
              });
            }

            this.changeDataset();
          })
          .catch(err => {
            logger.getLogger().warn(err);
            errorPopup.showPopupFromServer(this, err, false);
          });
    },

    /**
     * Sets the pagination information to be passed through to the SaleReportTableComponent
     */
    getPaginationInfo(result) {
      this.paginationInfo.totalPages = result["totalPages"];
      this.paginationInfo.totalElements = result["totalElements"];

      if (this.paginationInfo.totalElements !== 0) {
        let bounds = paginationInfo.getPageInfo(this.paginationInfo.page, this.paginationInfo.perPage, this.paginationInfo.totalElements);
        this.paginationInfo.lowerCount = bounds[0]
        this.paginationInfo.upperCount = bounds[1]
      }

      this.paginationInfo.custom = true;
    },

    /**
     * Change the page for pagination. Call the different GET methods depending on what SalesReport period
     * is being used.
     */
    customPageChange(page){
      this.paginationInfo.page = page
      if (this.isCustomDate) {
        this.getSalesReportCustom()
      } else {
        this.getSalesReportDefault()
      }
    },

    /**
     * Get request for sales report data using the default time period selector.
     * Nested if statements check which combination of Year, Month, Week and Day are used
     * and then formats the dates and sets granularity accordingly. An api endpoint is then
     * called to retrieve the corresponding sales report data which is then saved and displayed.
     */
    getSalesReportDefault() {
      if (this.year) {
        let startDate;
        let endDate;
        if (this.month) {
          let month = this.months.findIndex(m => m === this.month);
          if (this.week) {
            // Inputs are year, month and week
            let week = parseInt(this.week[5]);
            startDate = new Date(this.year, month, week * 7 - 5).toISOString().substring(0, 10);
            endDate = new Date(this.year, month, week * 7 + 1).toISOString().substring(0, 10);
          } else if (this.day) {
            // Inputs are year, month, and day
            let day = parseInt(this.day.substring(0, 2))
            startDate = new Date(this.year, month, day + 1).toISOString().substring(0, 10);
            endDate = new Date(this.year, month, day + 1).toISOString().substring(0, 10);
          } else {
            // Inputs are year and month
            startDate = new Date(this.year, month, 2).toISOString().substring(0, 10);
            endDate = new Date(this.year, month + 1, 1).toISOString().substring(0, 10);
          }
        } else {
          // Only input is year, get date from beginning and end of year
          startDate = `${this.year}-01-01`;
          endDate = `${this.year}-12-31`;
        }

        this.getSalesReportData(startDate, endDate);
        this.getSalesGraphData(startDate, endDate, true);
      }
    },

    /**
     * Changes the dataset depending on selected graph tab
     */
    changeDataset() {
      if (this.graphToggle === 1) {
        this.chartData = {
          labels: this.graphLabels,
          datasets: [
            this.totalSalesDataset
          ]
        };

      } else {
        this.chartData = {
          labels: this.graphLabels,
          datasets: [
            this.totalRevenueDataset
          ]
        };
      }
    },

    /**
     * Clear the date range data variable
     */
    clearDate() {
      this.dateRange = [];
      this.reportData = [];
      this.chartData = [];
    },

    /**
     * When bar is clicked show associated lower level of information
     * @param point location of click
     * @param event bar clicked
     */
    drill(point, event) {
      const bar = event[0];
      let newDateRange = this.datasetDates[bar._index];
      this.drillGranularity();
      this.getSalesGraphData(newDateRange.startDate, newDateRange.endDate, true);
      if (this.linkTable) {
        this.getSalesReportData(newDateRange.startDate, newDateRange.endDate)
      }

    },

    /**
     * Increase granularity one step deeper
     */
    drillGranularity() {
      if (this.granularity === "Annually") {
        this.granularity = "Monthly"
      } else if (this.granularity === "Monthly") {
        this.granularity = "Weekly"
      } else if (this.granularity === "Weekly") {
        this.granularity = "Daily"
      }
    },

    /**
     * Go back up call stack (after drilling down in graph)
     */
    popCallStack() {
      if (this.callStack.length > 1) {
        this.callStack.pop();
        let call = this.callStack.at(-1);
        this.granularity = call.granularity
        this.getSalesGraphData(call.startDate, call.endDate, false)
      }
    },

    /**
     * Change dataset granularity when radio button is clicked
     */
    changeGranularity(newGranularity) {
      this.granularity = newGranularity;
      this.isCustomDate ? this.getSalesReportCustom() : this.getSalesReportDefault();
    }
  },

  watch: {
    /**
     * Watch the year variable in the date picker
     */
    year(newVal) {
      if (newVal === null || newVal.length < 1) {
        this.month = "";
        this.week = "";
        this.day = "";
      }
    },

    month(newVal) {
      if (newVal === null || newVal.length < 1) {
        this.week = "";
        this.day = "";
      }
    },

    /**
     * Watch the week variable in the date picker
     */
    week(newVal) {
      if (newVal !== null && newVal.length > 0) {
        this.day = "";
      }
    },

    /**
     * Watch the day variable in the date picker
     */
    day(newVal) {
      if (newVal !== null && newVal.length > 0) {
        this.week = "";
      }
    },

    /**
     * Changes the displaying dataset if the graphToggle value changes
     *
     * @param newVal tab index
     */
    graphToggle(newVal) {
      if (newVal === 1) {
        this.changeDataset();
      } else {
        this.changeDataset();
      }
    }
  },
}
</script>

<style scoped>
@import '../../public/styles/salesReport.css';
</style>
