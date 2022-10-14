const getYears = function (startDate) {
    let start = startDate.getFullYear();
    let years = []
    for (let i = start; i > start - 50 && i >= 2010; i--) {
        years.push(i);
    }
    return years
};

const months = [ "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December" ];

const day_list = ['Friday','Saturday', 'Sunday','Monday','Tuesday','Wednesday','Thursday'];

/**
 * Returns an array of all of the days in the given year/month
 * e.g. year=1998 and month=01 with return and array containing
 * {0, 1, ... , 30, 31}
 *
 * @param year An integer representation of a year
 * @param month An integer representation of a month (0 indexed)
 * @returns {*[]} an array of integers representing all the days in the given month in the given year
 */
const getDaysInMonth = function (year, month) {
    let days = new Date(year, month + 1, 0).getDate();
    let arr = [];
    for (let i = 0; i < days; i++) {
        arr.push(i + 1);
    }
    return arr;
};

/**
 * Constructs the sales report query that is sent to the backend as a SearchSpec
 *
 * @param startDate
 * @param endDate
 * @param businessId
 * @returns {string}
 */
const getSalesReportQuery = function(startDate, endDate, businessId) {
    let query = "?search="
    if (startDate) {
        startDate.setDate(startDate.getDate() - 1);
        query += "saleDate>" + startDate.toISOString().substring(0,10);
    }

    if (endDate) {
        endDate.setDate(endDate.getDate() + 1);
        query += "endDate<" + startDate.toISOString().substring(0,10);
    }

    if (businessId) {
        query += "businessId=" + businessId.toString();
    }

    return query;
}

export default {
    getYears,
    getDaysInMonth,
    getSalesReportQuery,
    months,
    day_list,
}
