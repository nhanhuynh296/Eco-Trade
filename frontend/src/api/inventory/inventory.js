/**
 * inventory.js
 *
 * API for handling interactions with a business inventory
 */
import axiosInstance from '../axios-instance'
/**
 * Getting all inventory from a business
 * @param id - id of business
 * @param page - page number
 * @param size - page size
 * @param sortBy - parameter to sort by
 * @returns {Promise<AxiosResponse<any>>}
 */
const retrieveInventory = function (id, page, size, sortBy) {
    return axiosInstance.get(`/businesses/${id}/inventory/`, {params: {page, size, sortBy}, withCredentials: true})
        .then(res => res);
}

/**
 *
 * @param payload contains data to create a new inventory item
 * @param id
 * @returns {Promise<AxiosResponse<any>>}
 */
const addToInventory = function (payload, id) {
    return axiosInstance.post(`/businesses/${id}/inventory/`, payload, {withCredentials: true})
        .then(res => res);
}

/**
 *
 * @param payload contains data to update an inventory item
 * @param busId business id
 * @param invId inventory id
 * @returns {Promise<AxiosResponse<any>>}
 */
const updateInventoryItem = function (payload, busId, invId) {
    return axiosInstance.put(`/businesses/${busId}/inventory/${invId}`, payload, {withCredentials: true})
        .then(res => res);
}

export default {
    addToInventory,
    retrieveInventory,
    updateInventoryItem
}
