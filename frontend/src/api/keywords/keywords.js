/**
 * keywords.js
 *
 * API for handling interactions with keywords
 */
import axiosInstance from '../axios-instance'


/**
 * Creates a new keywords
 *
 * @param payload contains data to create a new keyword
 *
 * @returns {Promise<AxiosResponse<any>>}
 */
const addKeyword = function (payload) {
    return axiosInstance.post(`/keywords`, payload, {withCredentials: true})
        .then(res => res);
}

/**
 * Gets all the keywords that include the string 'searchString'
 * @param searchString string to search by
 * @returns {Promise<AxiosResponse<any>>}
 */
const getKeywords = function (searchString) {
    return axiosInstance.get(`/keywords/search`, {params: {searchQuery: searchString }, withCredentials: true})
        .then(res => res)
}

/**
 * Delete keyword by its id
 * @param keywordId
 * @returns {Promise<AxiosResponse<any>>}
 */
const deleteKeyword = function (keywordId) {
    return axiosInstance.delete(`/keywords/${keywordId}`, {withCredentials: true})
}

export default {
    addKeyword,
    getKeywords,
    deleteKeyword
}
