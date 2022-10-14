/**
 * admin.js
 *
 * API for handling user Default Global Application Admin sessions
 */
import axiosInstance from '../axios-instance'


/**
 * Sends a request which will change a user's role from user to admin
 *
 * @param userId the id of the user who will gain amin privileges
 *
 * @returns {Promise<number>} Status code indicating success and the user
 */
const makeAdmin = function (userId) {
    return axiosInstance.put(`/users/${userId}/makeadmin`, null, {withCredentials: true})
        .then(res => res);
};

/**
 * Put request to /users/{id}/revokeadmin
 *
 * @param userId the id of the user whose amin privileges will be revoked
 *
 * @returns {Promise<number>} Status code indicating success and the user
 */
const revokeAdmin = function (userId) {
    return axiosInstance.put(`/users/${userId}/revokeadmin`, null, {withCredentials: true})
        .then(res => res);
};

export default {
    makeAdmin,
    revokeAdmin,
};
