/**
 * Converts a JS Date to a string 'dd/mm/yyyy'. Used for checking search query'
 *
 * @param date Date to convert
 *
 * @returns {string} String of format dd/mm/yyyy or an empty string if the date is not parsable
 */
const convertToDateFormatDDMMYYYY = function(date) {

    let newDate = new Date(date)
    if (isNaN(newDate)) {
        return '';
    }

    return newDate.toLocaleString('en-GB').split(',')[0]
};


/**
 * Search the given catalogue data and yield a subset.
 * Used to dynamically change which inventory are present in the catalogue table.
 *
 * @param searchQuery Query to search by
 * @param catalogueData Initial data to search
 *
 * @returns {*[]} a list of filtered data
 */
const catalogueSearch = function(searchQuery, catalogueData) {
    searchQuery = searchQuery.toLowerCase();
    let filteredData = []
    let i;
    for (i in catalogueData) {
        let curRow = catalogueData[i];
        if (curRow.id.toString().includes(searchQuery) ||
            curRow.name.toLowerCase().includes(searchQuery) ||
            curRow.description.toLowerCase().includes(searchQuery) ||
            curRow.recommendedRetailPrice.toString().includes(searchQuery) ||
            curRow.manufacturer.toLowerCase().includes(searchQuery) ||
            convertToDateFormatDDMMYYYY(curRow.created).includes(searchQuery)) {

            filteredData.push(catalogueData[i]);
        }
    }
    return filteredData;
}

export default {
    catalogueSearch,
    convertToDateFormatDDMMYYYY
}
