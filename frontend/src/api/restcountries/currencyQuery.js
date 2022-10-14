/**
 * currencyQuery.js
 *
 * Exposes all the functions required to use restcountries API
 * http://restcountries.eu
 */
import axios from "axios";
import api from "../api";

//Server URL
const SERVER_URL = "https://restcountries.com/";

const currencyAxios = axios.create({
    baseURL: SERVER_URL,
    timeout: 25000
});

/**
 * Class to handle building currency queries
 */
export class CurrencyQuery {
    /**
     * Constructor
     *
     * @return {CurrencyQuery}
     */
    constructor() {
        this.baseURL = "v2/";
        this.query = "";
        return this;
    }

    /**
     * Append a value to query
     *
     * @param value Value for query
     */
    appendValue(value) {
        if (value) {
            this.query += value;
        }
        return this;
    }

    /**
     * Convert to string
     *
     * @return {string}
     */
    toString() {
        return `${this.baseURL}${this.query}`;
    }

    /**
     * Convert to string then get result from public API
     * Returns a list of country objects, currency is contained within
     *  see https://restcountries.eu/rest/v2/name/New%20Zealand for exact format
     *
     * @return {Promise<*>}
     */
    async result() {
        return currencyAxios.get(this.toString())
            .then(response => response.data)
            .catch(err => err);
    }
}

/**
 * Query currency from a country
 *
 * @param country Country to search for
 *
 * @returns Object containing currency information
 */
async function queryCountryCurrency (country) {
    // If no query inputted, don't continue
    if (!country) {
        return null;
    }

    const results = await new CurrencyQuery()
        .appendValue("name/")
        .appendValue(country)
        .result();

    if (!results || !results[0] || !results[0].currencies) {
        return null;
    }

    //Return first currency (Countries can have multiple, such as Bhutan)
    return results[0].currencies[0];
}

/**
 * Get current businesses currency, useful if you don't want to bother with manually getting the
 * users currency then calling the queryCountryCurrency function manually
 *
 *  Note: If fails to get currency (say invalid address) then will return NZD by default
 *
 * @return {Promise<void>} Currency object
 */
async function getCurrentBusinessCurrency(businessId) {
    if (!businessId) {
        businessId = localStorage.getItem("business-id");
    }
    let businessCurrency = {};
    const businessCountryForCurrency = (await api.businesses.getBusinessCountryForCurrency(businessId)).data;
    //Check if we've got a valid business object
    if (businessCountryForCurrency) {
        businessCurrency = await queryCountryCurrency(businessCountryForCurrency);
    }

    //If business is null, or we didn't find a currency default to NZD
    if (!businessCountryForCurrency || !businessCurrency) {
        businessCurrency = {
            code: "NZD",
            symbol: "$"
        }
    }
    return businessCurrency;
}

/**
 * Get the currency of a specific country, set default to NZD if not found.
 *
 * @param country The country for which to get and return the currency
 * @returns {Promise<boolean>}
 */
async function getCurrentCurrencyByCountry(country) {
    let currency = false;
    if (country) {
        currency = await queryCountryCurrency(country);
    }

    //If we didn't find a currency default to NZD
    if (!currency) {
        currency = {
            code: "NZD",
            symbol: "$"
        }
    }

    return currency;
}

export default {
    CurrencyQuery,
    queryCountryCurrency,
    getCurrentCurrencyByCountry,
    getCurrentBusinessCurrency
};
