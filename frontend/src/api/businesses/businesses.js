/**
 * businesses.js
 *
 * API for handling user creating, getting, and making another user admin for a businesses
 */
import axiosInstance from '../axios-instance'

/**
 * Function to send a POST request to /businesses to register a new business
 *
 * @param payload contains all the required data in order to create a new business
 *
 * @return {Promise<number>} status code indicating success/failure
 */
const register = function (payload) {
    return axiosInstance.post('/businesses', payload, {withCredentials:true})
        .then(res => res);
};

/**
 * Get request to /businesses/{id}
 *
 * @param id
 *
 * @returns {Promise<number>} Status code indicating success and the business
 */
const getBusiness = function (id) {
    return axiosInstance.get(`/businesses/${id}`, {withCredentials: true})
        .then(res => res);
};

/**
 * Gets the businesses country for currency from the backend
 * @param id
 * @returns {*}
 */
const getBusinessCountryForCurrency = function(id) {
    return axiosInstance.get(`businesses/currency/${id}`, {withCredentials: true})
        .then(res => res);
}

/**
 * Post request to /businesses/{id}/products
 * Function to send a post request to /businesses/{id}/product to add a product to a business catalogue
 *
 * @param payload contains all the required data in order to create a product
 * @param id the business id
 *
 * @return {Promise<number>} status code indicating success/failure
 */
const createProduct = function (payload, id) {
    return axiosInstance.post(`/businesses/${id}/products`, payload, {withCredentials: true})
        .then(res => res);
}

/**
 * Put request to /businesses/{businessId}/products/{productId}
 * Function to send a put request to modify a products details in the product catalogue.
 *
 * @param payload contains all the required data in order to create a product
 * @param businessId id of the business
 * @param productId id of the product we want to edit
 *
 * @returns {Promise<AxiosResponse<any>>} status code indicating success/failure
 */
const modifyProduct = function (payload, businessId, productId) {
    return axiosInstance.put(`/businesses/${businessId}/products/${productId}`, payload, {withCredentials: true})
        .then(res => res);
}

/**
 * Get request to /businesses/${id}/products
 * Function sends a get request to retrieve all inventory from a business
 *
 * @param id of the business
 * @param page page number
 * @param size number of products per page
 * @param sortBy string representing the value to sort the returned product by
 * @return {Promise<AxiosResponse<any>>} status code indicating success/failure with a JSON Array of Products
 */
const getProducts = function (id, page, size, sortBy) {
    return axiosInstance.get(`/businesses/${id}/products`, {params: {page, size, sortBy}, withCredentials: true})
        .then(res => res)
}

/**
 * Put request to /businesses/{id}/makeAdministrator
 *
 * @param businessId Business ID
 * @param userId User ID
 *
 * @returns {Promise<number>} Status code indicating success
 */
const makeBusinessAdministrator = function (businessId, userId) {
    return axiosInstance.put(`/businesses/${businessId}/makeAdministrator`, { "userId": userId },{withCredentials: true})
        .then(response => response);
};

/**
 * Put request to /businesses/{id}/removeAdministrator
 *
 * @param businessId Business ID
 * @param userId User ID
 *
 * @returns {Promise<number>} Status code indicating success
 */
const removeBusinessAdministrator = function (businessId, userId) {
    return axiosInstance.put(`/businesses/${businessId}/removeAdministrator`, { "userId": userId },{withCredentials: true})
        .then(response => response);
};

/**
 * Put request to /businesses${search}
 *
 * @param search is the query of the search input
<<<<<<< HEAD
 * @param page Page number
 * @param size Size of page
 * @param sortBy string that contains sort by value
=======
 * @param page Current Page
 * @param size Number of results per page
>>>>>>> dev
 *
 * @return {Promise<AxiosResponse<any>>} Status code indicating success
 */
const searchBusiness = function (search, page, size, sortBy) {
    return axiosInstance.get(`/businesses${search}`, {
            params: {page, size, sortBy}, withCredentials: true
        })
        .then(response => response)
}

/**
 * Get request to /businesses${businessId}/${search}/salesReport
 *
 * @param search search query used to construct the SearchSpec
 * @param businessId
 * @param startDate custom start date entered by the user
 * @param endDate custom end date entered by the user
 * @param page Current Page
 * @param size Number of results per page
 * @returns {Promise<AxiosResponse<any>>} Status code
 */
const searchSaleReport = function (search, businessId, startDate, endDate, page, size) {
    return axiosInstance.get(`/businesses${businessId}/${search}/salesReport`, {
        params: {startDate, endDate, page, size}, withCredentials: true
    })
        .then(response => response)
}

export default {
    register,
    getBusiness,
    getBusinessCountryForCurrency,
    makeBusinessAdministrator,
    removeBusinessAdministrator,
    createProduct,
    modifyProduct,
    getProducts,
    searchBusiness,
    searchSaleReport
};
