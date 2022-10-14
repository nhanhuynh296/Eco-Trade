/**
 * notification.js
 *
 * API for handling notifications for users
 */
import axiosInstance from '../axios-instance'

/**
 * Get users notifications
 *
 * @param payload Payload potentially with tab and type
 */
const getNotification = function (payload) {
    if (payload) {
        const request = Object.keys(payload).map(key => `${key}=${encodeURIComponent(payload[key])}`).join('&');
        return axiosInstance.get(`/notifications?${request}`,{withCredentials: true})
            .then(res =>res);
    }
    return axiosInstance.get(`/notifications`,{withCredentials: true})
        .then(res =>res);
};

/**
 * Delete a notification from the database and cache it for undo functionality.
 * @param notificationId - The id of the notification
 */
const deleteNotification = function (notificationId) {
    return axiosInstance.delete(`/notifications/${notificationId}`, {withCredentials:true})
        .then(res => res);
};

/**
 * Restore a cached notification (that has been most recently been deleted)
 * @param deletedNotificationId notification to restore
 */
const restoreNotification = function (deletedNotificationId) {
    return axiosInstance.put(`/notifications/${deletedNotificationId}/restore`, {},{withCredentials:true})
        .then(res => res);
};

/**
 * Creates a message notification
 * @param payload contains recipientId, cardId and the message
 */
const postMessage = function (payload) {
    return axiosInstance.post(`/notifications/message`, payload, {withCredentials: true})
        .then(res => res);
};

/**
 * Changes the tag on the notification
 *
 * @param notificationId Notification's ID
 * @param tag String representation of tag
 */
const updateTag = function (notificationId, tag) {
    return axiosInstance.put(`/notifications/${notificationId}/tag`, { notificationTagType: tag }, {withCredentials: true})
        .then(res => res);
}

/**
 * Updates the notification with the given category.
 *
 * @param notificationId Notification's ID
 * @param category string representation of the category
 */
const updateCategory = function (notificationId, category) {
    return axiosInstance.put(`/notifications/${notificationId}/category`, { notificationCategory: category }, {withCredentials: true})
        .then(res => res);
}

export default {
    getNotification,
    deleteNotification,
    restoreNotification,
    postMessage,
    updateTag,
    updateCategory
}
