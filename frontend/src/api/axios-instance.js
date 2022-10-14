/**
 * axios-instance.js
 *
 * File for handling axios API requests
 */
import axios from "axios";

//Server URL
const SERVER_URL = process.env.VUE_APP_SERVER_ADD;

const axiosInstance = axios.create({
    baseURL: SERVER_URL,
    credentials: 'include',     // Used for ensuring set-cookie works on the client side (defaults to 'include' anyway).
    timeout: 20000
});

axiosInstance.interceptors.request.use(
    config => {
        const token = localStorage.getItem("token");
        if (token) {
            config.headers["X-Authorization"] = token;
        }
        return config;
    },
    error => Promise.reject(error)
);

// Export instance to be used within API files
export default axiosInstance;
