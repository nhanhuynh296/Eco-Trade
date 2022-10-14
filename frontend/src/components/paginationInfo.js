/**
 * paginationInfo.js
 *
 * File for getting pagination info for different components
 */

/**
 * Calculates the lower and upper bound to be displayed in the pagination information
 */
function getPageInfo(page, perPage, total) {
    if (total === 0) {
        return [0,0]
    }
    let lower = page * perPage - (perPage - 1);
    let upper = page * perPage;

    if (upper > total) {
        upper = total;
    }

    return [lower, upper];
}

export default {
    getPageInfo
}
