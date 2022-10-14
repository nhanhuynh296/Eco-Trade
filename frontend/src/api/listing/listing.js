/**
 * listing.js
 *
 * API for handling interactions with a business's listings
 */
import axiosInstance from '../axios-instance'


/**
 * Getting all listing from a business
 *
 * @param id - id of business
 *
 * @param page - page number
 * @param size - number of listing per page
 * @param sortBy - String parameter to listing by
 * @returns {Promise<AxiosResponse<any>>}
 */
const getListings = function (id, page, size, sortBy) {
  return axiosInstance.get(`/businesses/${id}/listings/`, {params: {page, size, sortBy}, withCredentials: true})
    .then(res => res);
}


/**
 * Creates a new listing for a business 
 * 
 * @param payload contains data to create a new listing
 * @param id - id of a business
 * 
 * @returns {Promise<AxiosResponse<any>>}
 */
const addListing = function (payload, id) {
  return axiosInstance.post(`/businesses/${id}/listings/`, payload, {withCredentials: true})
      .then(res => res);
}

/**
 * Search all listings
 *
 * @param search Search spec for listings (see backend)
 * @param page page to get
 * @param size number of listings per page
 * @param sortBy attribute + ASC/DESC to sort by
 * @returns {Promise<AxiosResponse<any>>}
 */
const searchListings = function (sortBy, search, page, size) {
  let url = search ? `/listings?search=${search}` : '/listings?search=';

  return axiosInstance.get(url, {params: {sortBy, page, size}, withCredentials: true})
      .then(res => res)
}

/**
 * Return one listing with the given id
 * @param id of listing
 * @returns {Promise<AxiosResponse<any>>}
 */
const getOneListing = function (id) {
  return axiosInstance.get(`/listings/${id}`, {withCredentials: true})
      .then(res => res);
}

/**
 * Like a listing
 * @param id Listing id
 * @returns {Promise<AxiosResponse<any>>}
 */
const likeListing = function (id) {
  return axiosInstance.put(`listing/${id}/like`, {},{withCredentials: true})
}

/**
 * Unlike a listing
 * @param id Listing id
 * @returns {Promise<AxiosResponse<any>>}
 */
const unlikeListing = function (id) {
  return axiosInstance.put(`listing/${id}/unlike`,{}, {withCredentials: true})
}
/**
 * Deletes listing from backend and updates the associated business' sales history
 * @param id of listing ot buy
 * @returns {Promise<AxiosResponse<any>>}
 */
const buyListing = function (id) {
  return axiosInstance.put(`/listings/${id}`,{} , {withCredentials: true})
      .then(res => res);
}

export default {
  getListings,
  addListing,
  searchListings,
  getOneListing,
  likeListing,
  unlikeListing,
  buyListing
}
