/**
 * sales.js
 *
 * API for handling interactions with a business's sales
 */
import axiosInstance from '../axios-instance'

/**
 * Get request to get business's sales history
 *
 * @param businessId id of the business
 * @param page number
 * @param size number of total elements on the page
 * @return {Promise<AxiosResponse<any>>}
 */
const getSalesHistory = function (businessId, page, size) {
    return axiosInstance.get(`/businesses/${businessId}/saleshistory`, {withCredentials: true, params: {page, size}})
        .then(res => res);
}

/**
 * Gets request for getting business's sales report
 *
 * @param businessId id of the business
 * @param granulateBy String granulated by value
 * @param startDateString String start date in string format
 * @param endDateString String end date in string format
 * @param page number
 * @param size number of total elements on the page
 * @return {Promise<AxiosResponse<any>>}
 */
const getSalesReport = function (businessId, granulateBy, startDateString, endDateString, page, size) {
    return axiosInstance.get(`/businesses/${businessId}/salesreport`,
        {withCredentials: true, params: {granulateBy, startDateString, endDateString, page, size}})
        .then(res => res);
}

/**
 * Gets the sale report data for graphs
 *
 * @param businessId the id of the business
 * @param startDateString the start date of the sale report
 * @param endDateString the end date of the sale report
 * @param granulateBy granularity type
 * @return {Promise<AxiosResponse<any>>}
 */
const getSalesGraph = function (businessId, startDateString, endDateString, granulateBy) {
    return axiosInstance.get(`/businesses/${businessId}/salesgraph`,
        {
            withCredentials: true,
            params: {startDateString, endDateString, granulateBy}}
        )
        .then(res => res);
}

export default {
    getSalesHistory,
    getSalesGraph,
    getSalesReport,
}