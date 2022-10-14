/**
 * Searches for users from the navbar
 *
 * @param searchQuery String search query
 *
 * @return {string} of the final formatted query
 */
const searchUsers = function (searchQuery) {
    const split = this.splitQuery(searchQuery.split(" "));
    return this.constructUserSearch(split);
}

/**
 * Searches for business from the navbar
 *
 * @param searchQuery String search query
 * @param type String type of search
 *
 * @return {string} of the final formatted query
 */
const searchBusinesses = function (searchQuery, type) {
    const split = this.splitQuery(searchQuery.split(" "));
    return this.constructBusinessSearch(split, type);
}

/**
 * Splits the original query String to format each search item
 * Each item is checked and a search item with "" is added together
 * If the item is not an AND or an OR then * is added to the item to the start and the end
 *
 * @param searchQuery String of the search query
 *
 * @return {[]} List of the final set of search items
 */
const splitQuery = function (searchQuery) {
    const split = [];
    let index = 0;
    let indexStart = -1;
    let indexEnd = -1;

    for (let searchItem of searchQuery) {
        let end = searchItem.length - 1;

        if (searchItem[0] === '"') {
            indexStart = index;
        } else if (indexStart === -1) {
            if (searchItem !== "AND" && searchItem !== "OR") {
                split.push('*' + searchItem + '*');
            } else {
                split.push(searchItem);
            }

        }

        if (searchItem[end] === '"') {
            indexEnd = index;
            let item = searchQuery[indexStart];

            while (indexStart !== indexEnd) {
                indexStart++;
                item += " " + searchQuery[indexStart];
            }

            split.push(item);
            indexStart = -1;
            indexEnd = -1;
        }

        index++;
    }

    return split;
}

/**
 * Formats the final search query to fit the formatting of Spring-search adapter for Business Search
 *
 * @param split String of the search query
 * @param type String of the type of business search
 *
 * @return {string} final search query
 */
const constructBusinessSearch = function (split, type) {
    let fullSearch = '?search=';
    let index = 0;

    if (split[0] === "**" && split.length === 1) {
        if (type !== null) {
            fullSearch += 'businessType:"' + type + '"';
        } else {
            fullSearch += 'businessType:"Accommodation" OR businessType:"Food Services" OR businessType:"Retail Trade"' +
                ' OR businessType:"Charitable organisation" OR businessType:"Non-profit organisation"';
        }
    } else {
        for (let searchItem of split) {
            if (index === 0 && searchItem !== "AND" && searchItem !== "OR") {
                fullSearch += 'name:' + searchItem;
            } else if (split[index-1] !== "AND" && split[index-1] !== "OR" && searchItem !== "AND" && searchItem !== "OR") {
                fullSearch += ' AND name:' + searchItem;
            } else if (searchItem === "AND") {
                fullSearch += ' AND';
            } else if (searchItem === "OR") {
                fullSearch += ' OR';
            } else {
                fullSearch += ' name:' + searchItem;
            }

            index++;
        }

        if (type !== null) {
            fullSearch += ' AND businessType:"' + type + '"';
        }
    }

    return fullSearch;
}

/**
 * Called from constructFields function in the case that the search term is an exact search (contains '"')
 * Creates a subset of the search such that each term in the quotation marks must match one of the fields.
 *
 * @param searchItem The item contained in the quotation marks that is being searched for
 * @returns {string} The constructed search query for the items in the quotation marks
 */
const constructExactSearch = function (searchItem) {
    searchItem = searchItem.substr(1, searchItem.length-2);
    let searchItems = searchItem.split(" ");
    let result = "";

    for (let i = 0; i < searchItems.length ; i++) {
        if (i === (searchItems.length - 1)) {
            result += constructFields(searchItems[i]) + ')';
        } else {
            result += constructFields(searchItems[i]) + ' AND ';
        }
    }

    return result;
}

/**
 * Called from the constructFields function in the case that the search term is NOT an exact search
 * For the given search item it constructs a query wherein the search item is checked against each
 * of the search fields aka firstName:James OR lastName:James OR nickname:James
 *
 * @param searchItem The not exact search item that is being searched for
 * @returns {string} The constructed search query for the non-exact item
 */
const constructNonExactSearch = function (searchItem) {
    let searchFields = ["firstName:", "lastName:", "nickname:"];
    let index = 0;
    let result = "";

    for (let field of searchFields) {
        if (index === 2) {
            result += field + searchItem + ')';
        } else {
            result += field + searchItem + ' OR ';
        }
        index += 1
    }

    return result;
}

/**
 * Takes the search item that was provided by the constructUserSearch function and applies different
 * rules for constructing the search query based on if the item is an exact search or not
 *
 * @param searchItem The search item provided by the constructUserSearch function
 * @returns {string} The constructed search query for that item
 */
const constructFields = function (searchItem) {
    let searchQuery = "(";

    if (searchItem.includes('"')) {
        searchQuery += constructExactSearch(searchItem);
    } else {
        searchQuery += constructNonExactSearch(searchItem);
    }

    return searchQuery;
}

/**
 * Formats the final search query to fit the formatting of Spring-search adapter for User Search
 *
 * @param split String of the search query
 *
 * @return {string} final search query
 */
const constructUserSearch = function (split) {
    let fullSearch = '?search=';
    let index = 0;

    // If the user enters an empty search then we want to return everything, and all users have an email with an @ in them
    if (split[0] === "**") {
        fullSearch += "email:*@*";
    } else {
        // For each searchItem in the query we want to check if it matches any of the searchFields
        for (let searchItem of split) {
            if (index === 0 && searchItem !== "AND" && searchItem !== "OR") {
                fullSearch += this.constructFields(searchItem);
            } else if (split[index - 1] !== "AND" && split[index - 1] !== "OR" && searchItem !== "AND" && searchItem !== "OR") {
                fullSearch += ' OR ' + this.constructFields(searchItem);
            } else if (searchItem === "AND") {
                fullSearch += ' AND ';
            } else if (searchItem === "OR") {
                fullSearch += ' OR ';
            } else {
                fullSearch += ' ' + this.constructFields(searchItem);
            }

            index++;
        }
    }

    return fullSearch;
}


export default {
    searchBusinesses,
    searchUsers,
    splitQuery,
    constructBusinessSearch,
    constructFields,
    constructUserSearch,
}
