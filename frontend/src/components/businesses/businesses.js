import navBar from "../../components/home/navBar";

/**
 * business.js
 *
 * File containing business functions
 */

//Server URL
const serverURL = process.env.VUE_APP_SERVER_ADD;

/**
 * Checks that all required business registration fields are not empty
 *
 * @param name - the user submitted name of the business
 * @param address - the user submitted address of the business
 * @param type - the user submitted type of business
 */
const validateBusinessRegister = function(name, address, type) {
    let valid = true;

    if (name === null) {valid = false;}
    if (address === null) {valid = false;}
    if (type === null) {valid = false;}

    return valid;
}

/**
 * Checks that the type parameter is one of the require values
 *
 * @param type - user submitted type of business
 */
const validateBusinessType = function(type) {
    let validTypes = [
        'Accommodation',
        'Food Services',
        'Retail Trade',
        'Charitable organisation',
        'Non-profit organisation'
    ]

    return validTypes.includes(type);
}

/**
 * Takes a business id and the primary id of a product image and returns the link to the image.
 *
 * @param businessId ID of the business
 * @param primaryId ID of the product
 * @param isThumbnail Retrieve the thumbnail version
 */
const getProductImageLink = function(businessId, primaryId, isThumbnail) {
    if (primaryId === null || businessId === null) {
        return `https://bulma.io/images/placeholders/128x128.png`;
    }

    if (isThumbnail) {
        return `${serverURL}/business/${businessId}/products/thumbnail/${primaryId}`;
    } else {
        return `${serverURL}/business/${businessId}/products/images/${primaryId}`;
    }
}

/**
 * Retrieves link to _publicly_ available image
 *
 * @param imageId ID of the image
 * @param isThumbnail Retrieve the thumbnail version
 */
const getListingImageLink = function(imageId, isThumbnail) {
    if (!imageId) {
        return `https://bulma.io/images/placeholders/128x128.png`;
    }

    if (isThumbnail) {
        return `${serverURL}/thumbnail/${imageId}`;
    } else {
        return `${serverURL}/images/${imageId}`;
    }
}

/**
 * Constructs the main query for searching listings
 *
 * @param query string
 * @return {string} full search query with AND, OR, and "" components
 */
const getListingsSearchQuery = function (query) {
    let splitQuery = navBar.splitQuery(query.split(" "));

    let fullSearch = '?search=';
    let productName = 'inventoryItem.product.name:'
    let index = 0;

    if (!(splitQuery[0] === "**" && splitQuery.length === 1)) {
        for (let searchItem of splitQuery) {
            if (index === 0 && searchItem !== "AND" && searchItem !== "OR") {
                fullSearch += productName + searchItem;
            } else if (splitQuery[index-1] !== "AND" && splitQuery[index-1] !== "OR" && searchItem !== "AND" && searchItem !== "OR") {
                fullSearch += ' AND ' + productName + searchItem;
            } else if (searchItem === "AND") {
                fullSearch += ' AND';
            } else if (searchItem === "OR") {
                fullSearch += ' OR';
            } else {
                fullSearch += ' ' + productName + searchItem;
            }

            index++;
        }
    }

    return fullSearch;
}

/**
 * Adds the business name search to the query
 *
 * @param query string of the main query
 * @param names of the business
 * @return {string} search that contains the business name within it
 */
const addBusinessNameSearch = function (query, names) {
    let splitNames = names.split(" ");
    let index = 1;

    query = this.checkSearchQuery(query);

    for (let name of splitNames) {
        query += ' inventoryItem.product.business.name:*' + name + '*';

        if (index !== splitNames.length) {
            query += ' AND';
        }
        index++;
    }

    return query;
}

/**
 * Adds the business type search to the query
 *
 * @param query string of the main query
 * @param types list of the business types
 * @return {string} search that contains the business type within it
 */
const addBusinessTypeSearch = function (query, types) {
    let index = 1;

    query = this.checkSearchQuery(query);
    query += ' (';

    for (let type of types) {
        query += ' inventoryItem.product.business.businessType:"' + type + '"';

        if (index !== types.length) {
            query += ' OR';
        }
        index++;
    }

    query += ')';

    return query;
}

/**
 * Adds the business country, region, and city search to the query
 *
 * @param query string of the main query
 * @param country of the business
 * @param region of the business
 * @param city of the business
 * @return {*} search that contains the business country, region, and city within it
 */
const addBusinessAddressSearch = function (query, country, region, city) {
    let address = 'inventoryItem.product.business.address.';
    if (country != null && country !== "") {
        query = this.checkSearchQuery(query);
        query += ' ' + address + 'country:"' + country + '"';
    }
    if (region != null && region !== "") {
        query = this.checkSearchQuery(query);
        query += ' ' + address + 'region:"' + region + '"';
    }
    if (city != null && city !== "") {
        query = this.checkSearchQuery(query);
        query += ' ' + address + 'city:"' + city + '"';
    }

    return query;
}

/**
 * Adds the closing date search to the query
 *
 * @param query string of the main query
 * @param date closing date
 * @return {string} search that contains the closing date within it
 */
const addClosingDateSearch = function (query, date) {
    query = this.checkSearchQuery(query);

    return query + ' closes:' + date.toISOString().substring(0,10);
}

/**
 * Adds the price range search to the query
 *
 * @param query string of the main query
 * @param priceRange list of two price values
 * @return {string} search that contains the price range within it
 */
const addPriceRangeSearch = function (query, priceRange) {
    let lowerPrice = priceRange[0];
    let higherPrice = priceRange[1];

    if (lowerPrice !== 0) {
        query = this.checkSearchQuery(query);
        query += ' (price:' + lowerPrice + ' OR price>' + lowerPrice + ')';
    }
    if (higherPrice !== 0) {
        query = this.checkSearchQuery(query);
        query += ' (price:' + higherPrice + ' OR price<' + higherPrice + ')'
    }

    return query;
}

/**
 * Checks if the query is empty
 * If empty then adds an AND to the query
 *
 * @param query string of the main query
 * @return {string} search query
 */
const checkSearchQuery = function (query) {
    if (query !== '?search=') {
        query += ' AND';
    }
    return query;
}

/**
 * Take a string and truncates it using the given length
 *
 * @param value string value of the description
 * @param length int value to truncate by
 *
 * @return returns a modified string to fit the length
 */
const maxDisplay = function(value, length) {
    return value.length > length
        ? value.substr(0, length) + '...'
        : value
}

/**
 * Takes a float and uses the Math import to round it to two or less decimal places
 *
 * @param floatValue a float value with 0 to any number of decimal places
 *
 * @return return float of the original number with only two decimal places or less
 */
const roundDecimalPlaces = function(floatValue) {
    return Math.round((floatValue) * 100) / 100
}

export default {
    validateBusinessRegister,
    validateBusinessType,
    getProductImageLink,
    getListingImageLink,
    getListingsSearchQuery,
    addBusinessNameSearch,
    addBusinessTypeSearch,
    addBusinessAddressSearch,
    addClosingDateSearch,
    addPriceRangeSearch,
    checkSearchQuery,
    maxDisplay,
    roundDecimalPlaces
}
