/**
 * cards.js
 *
 * API for handling interactions with the community market cards
 */
import axiosInstance from '../axios-instance'

/**
 * Creates a new card on the user community market
 *
 * @param payload contains data to create a new card
 *
 * @returns {Promise<AxiosResponse<any>>}
 */
const createNewCard = function (payload) {
    return axiosInstance.post(`/cards`, payload, {withCredentials: true})
        .then(res => res);
}

/**
 * Get the cards created by user with the given ID
 * @param id - The ID of the user
 * @param page - The page number
 * @param size - The number of cards per page
 * @returns {Promise<<any>>} (blue-sky) a JSON of the card
 */
const getUsersCards = function (id, page, size) {
    return axiosInstance.get(`users/${id}/cards`, {params: {page, size}, withCredentials: true})
    .then(res => res);
}

/**
 * Get a specific card given by its id
 * @param cardId the card-to-get its id
 * @returns {Promise<AxiosResponse<any>>} (blue-sky) a JSON of the card
 */
const getCard = function (cardId) {
    return axiosInstance.get(`/cards/${cardId}`, {withCredentials: true})
        .then(res => res);
}

/**
 * Delete a card with the given card Id (only possible if you are the card owner, GAA, or DGAA)
 * @param cardId
 * @returns {Promise<AxiosResponse<any>>} HTTP 200 if successful
 */
const deleteCard = function (cardId) {
    return axiosInstance.delete(`/cards/${cardId}`, {withCredentials: true})
        .then(res => res);
}

/**
 * Sets the expiration date of the card of the given id to 2 weeks in the future
 *
 * @param cardId - id of the card being updated
 * @returns {Promise<AxiosResponse<any>>}
 */
const renewCard = function (cardId) {
    return axiosInstance.put(`/cards/${cardId}/extenddisplayperiod`, null, {withCredentials: true})
        .then(res => res);
}

/**
 * Gets the cards by searching via keywords
 *
 * @param keywords list of keywords names
 * @param type search type either and OR or
 * @param section which section is being searched within marketplace
 * @param sortBy string that denotes a specific sorting to be applied
 * @param country name
 * @param region name
 * @param city name
 * @param page the number of the page
 * @param size the number of items on the page
 * @return {Promise<AxiosResponse<any>>}
 */
const searchCards = function (keywords, type, section, sortBy, country, region, city, page, size) {
    let keywordsParam = new URLSearchParams();
    for (let keyword of keywords) {
        keywordsParam.append("keywords", keyword)
    }

    country = (country != null && country.length < 1) ? null : country
    region = (region != null && region.length < 1) ? null : region
    city = (city != null && city.length < 1) ? null : city

    return axiosInstance.get(`/cards/search?keywords=${keywords}`,{
            params: {keywordsParam, type, section, sortBy, country, region, city, page, size},
            withCredentials: true
        })
        .then(res => res);
}

/**
 * PUT request to /cards/:id
 * Update a cards's attributes
 * @param cardId to to update
 * @param payload JSON of card attributes
 * @returns {Promise<AxiosResponse<any>>}
 */
const updateCard = function (cardId, payload) {
    return axiosInstance.put(`/cards/${cardId}`, payload, {withCredentials: true})
        .then(res => res)
};

export default {
    createNewCard,
    getUsersCards,
    getCard,
    deleteCard,
    renewCard,
    updateCard,
    searchCards
}
