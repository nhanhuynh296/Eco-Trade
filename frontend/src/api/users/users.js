/**
 * users.js
 *
 * API for handling user login and logout sessions
 */
import axiosInstance from '../axios-instance'

/**
 * Get request to /users/{id}
 *
 * @param id of the user
 * @returns {Promise<number>} Status code indicating success and the user
 */
const getUser = function (id) {
    return axiosInstance.get(`/users/${id}`, {withCredentials: true})
                        .then(res => res);
};

/**
 * Get request to /users/search
 *
 * @param search The formatted user search
 * @param page The page that the user is on
 * @param size How many results the user wants to return
 * @param sortBy Direction to sort results by, e.g FIRST_ASC, FIRST_DESC
 * @returns {Promise<number>} a list of users from the search query
 */
const searchUsers = function (search, page, size, sortBy) {
    return axiosInstance.get(`/users${search}`, {
        params: {page, size, sortBy}, withCredentials: true
    })
        .then(response => response)
};

/**
 * PUT request to /users/:id
 * Update a user's attributes
 *
 * @param userId to to update
 * @param payload JSON of user attributes
 * @returns {Promise<AxiosResponse<any>>}
 */
const updateUser = function (userId, payload) {
    return axiosInstance.put(`/users/${userId}`, payload, {withCredentials: true})
        .then(res => res)
};

/**
 * PUT request to /users/currency/:id
 * Update a user's country for currency
 *
 * @param userId
 * @param country
 * @returns {*}
 */
const updateCountryForCurrency = function (userId, country) {
    return axiosInstance.put(`/users/currency/${userId}`, country, {withCredentials: true})
        .then(res => res)
};

/**
 * PUT request to /users/image/:imageId/makeprimary
 * Updates the user's primary image
 *
 * @param imageId to use for the primary image
 * @returns {Promise<AxiosResponse<any>>}
 */
const updatePrimaryImage = function (imageId) {
    return axiosInstance.put(`/users/images/${imageId}/makeprimary`, null, {withCredentials: true})
        .then(res => res)
};

/**
 * Sends an image in a multipart/form data format to /users/${userId}/images/
 *
 * @param userId the id of the user that the product belongs to
 * @param image image to send
 * @returns {Promise<AxiosResponse<any>>}
 */
const postImage = function (userId, image) {
    let formData = new FormData();
    formData.append("file", image);
    return axiosInstance.post(`/users/${userId}/images/`, formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        },
        withCredentials: true
    })
}

export default {
    getUser,
    searchUsers,
    updateUser,
    updatePrimaryImage,
    updateCountryForCurrency,
    postImage,
};
