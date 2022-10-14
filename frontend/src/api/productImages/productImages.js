/**
 * productImages.js
 *
 * API for handling creating, getting, and modifying another product images
 */
import axiosInstance from '../axios-instance'

/**
 * Sends an image in a multipart/form data format to /businesses/{businessId}/products/{productId}/images
 * @param businessId the id of the business that the product belongs to
 * @param productId the id of the product that the image belongs to
 * @param image image to send
 * @returns {Promise<AxiosResponse<any>>}
 */
const postImage = function (businessId, productId, image) {
    let formData = new FormData();
    formData.append("file", image);
    return axiosInstance.post(`/businesses/${businessId}/products/${productId}/images/`, formData, {
        headers: {
              'Content-Type': 'multipart/form-data'
          },
        withCredentials: true
  })
};

/**
 * Function to send a PUT request to /businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary
 * to set an existing image to be the new primary image for some product
 *
 * @param businessId the id of the business that the product belongs to
 * @param productId the id of the product that the image belongs to
 * @param imageId the id of the image that is being updated to the primary image
 *
 * @return {Promise<number>} status code indicating success/failure
 */
const putPrimaryProductImage = function (businessId, productId, imageId) {
    return axiosInstance.put(`/businesses/${businessId}/products/${productId}/images/${imageId}/makeprimary`, null, {withCredentials:true})
        .then(res => res);
};

/**
 * Deletes the specified product image using the given parameters
 *
 * @param businessId ID of the business
 * @param productId the id of the product that the image belongs to
 * @param imageId id of the image to be deleted
 *
 * @return {Promise<number>} status code indicating success/failure
 */
const deleteProductImage = function (businessId, productId, imageId) {
    return axiosInstance.delete(`/businesses/${businessId}/products/${productId}/images/${imageId}`, {withCredentials:true})
        .then(res => res);
};

export default {
    postImage,
    putPrimaryProductImage,
    deleteProductImage
};
