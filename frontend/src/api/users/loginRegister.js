/**
 * loginRegister.js
 *
 * API for handling user login and logout sessions
 *
 * Note: {withCredentials: true} is needed in the config of a axiosInstance request
 * if you want to send the JSESSIONID cookie back the the server.
 */
import axiosInstance from '../axios-instance'

/**
 * Used to determine if the the navBarComponent should display login/register options or user information
 *
 * @returns {boolean}
 */
const isLoggedIn = function () {
    return (localStorage.getItem("id") !== "-1");
};

/**
 * Used to determine if a user is authenticated.
 * If 401 is returned the user should not have access to the page
 * they're trying to access.
 *
 * @returns @returns {Promise<boolean>}
 */
const isAuthorised = function () {
    return axiosInstance.get('/user/loginCheck', {withCredentials: true})
        .then(res => res);
};

/**
 * POST request to /login
 *
 * @param payload @param payload contains all the required data in order to login
 *
 * @returns {Promise<number>} the user id and token
 */
const login = function (payload) {
    // config: must contain withCredentials in order for the cookie to be set client-side
    return axiosInstance.post('/login', payload, {withCredentials: true})
                        .then(res => res);
};

/**
 * Function to send a POST request to /users to register a new account
 *
 * @param payload payload contains all the required data in order to create a new user
 *
 * @return {Promise<number>} the user id and token
 */
const register = function (payload) {
    return axiosInstance.post('/users', payload)
                        .then(res => res);
};

/**
 * Function to send a POST request to /logout to logout a user
 *
 * @return {Promise<number>} the user id and token
 */
const logoutUser = function () {
    axiosInstance.post("/logout", "", {withCredentials: true})
                 .then(res => res)
                 .catch();
};

export default {
    login,
    register,
    isLoggedIn,
    logoutUser,
    isAuthorised,
};
