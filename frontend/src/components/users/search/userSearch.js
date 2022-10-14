import api from '../../../api/api';
import logger from "../../../logger";
import navBar from "../../../components/home/navBar";

/**
 * reformat user search before give to back-end
 *
 * @param searchQuery
 *
 * @returns {string}
 */
const formatSearch = function(searchQuery) {
    const keywords = ["AND", "OR"];
    let temp = searchQuery;
    keywords.forEach(function (value) {
        let index = searchQuery.indexOf(value)
            while (index !== -1) {
            if (!/\s/.test(temp[index + value.length])) {
                temp = [temp.slice(0, index + value.length), ' ', temp.slice(index + value.length)].join('');
            }
            if (!/\s/.test(temp[index - 1])) {

                temp = [temp.slice(0, index), ' ', temp.slice(index)].join('');
                index ++;
            }
            index = temp.indexOf(value, index + value.length);
        }
    });

    return temp;
};

/**
 * Creates an api call for the searchQuery
 * Sets the results and returns the result
 * If error occurs it is displayed
 *
 * @param searchQuery string that contains a search query
 *
 * @return a list of users
 */
const search = async function (searchQuery) {
    searchQuery = navBar.searchUsers(searchQuery);

    let results = null;
    await api.users
        .searchUsers(searchQuery, 1, 4, "FIRST_DESC")
        .then(res => {
            results = res.data.paginationElements;
            logger.getLogger().debug(res.data);
        })
        .catch(error => {
            logger.getLogger().warn(error);
        });
    return results;
}

/**
 * Quick test to check if the current users role is an admin or default admin
 *
 * @param roleString Users role
 * @param defaultOnly Query if they're the global admin
 *
 * @returns {boolean}
 */
const isAdminRole = function (roleString, defaultOnly = false) {
    const adminTypes = defaultOnly ? ["ROLE_DEFAULT_ADMIN"] : ["ROLE_ADMIN", "ROLE_DEFAULT_ADMIN"] ;

    return adminTypes.includes(roleString);
}

/**
 * Convert a list of businesses into an appropriate string format
 *
 * @param businessesList List of businesses
 *
 * @returns {string}
 */
const convertBusinessListToString = function (businessesList) {
    if (businessesList && businessesList.length > 0) {
        let businessString = "Businesses Administrated: ";
        for (let i = 0; i < businessesList.length; i++) {
            let business = businessesList[i];
            if (business) {
                businessString += `${business.name}, `;
            }
        }

        //Remove trailing comma
        businessString = businessString.slice(0, businessString.length - 2);
        return businessString;
    } else {
        return '';
    }
}

export default {
    search,
    formatSearch,
    isAdminRole,
    convertBusinessListToString
}
