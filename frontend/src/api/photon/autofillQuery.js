/**
 * autofillQuery.js
 *
 * Exposes all the functions required to use photon geocoder API
 * https://github.com/komoot/photon
 */
import axios from "axios";

//Server URL
const SERVER_URL = "https://photon.komoot.io/";

const photonAxios = axios.create({
    baseURL: SERVER_URL,
    timeout: 25000
});

/**
 * Class to handle building autofill queries from photon geocoder API
 */
export class AutofillQuery {

    /**
     * Constructor
     *
     * @return {AutofillQuery}
     */
    constructor() {
        this.baseURL = "api/?q=";
        this.query = "";
        return this;
    }

    /**
     * Need a check if query parameter is first or within a chain
     *
     * @param value Value for query
     */
    appendValue(value) {
        if (value) {
            if (this.query !== "") {
                this.query += ` ${value}`;
            } else {
                this.query = `${value}`;
            }
        }
        return this;
    }

    /**
     * Add tag to query
     *
     * @param tag Tag for query
     * @param value Value for query
     */
    appendTag(tag, value) {
        if (this.query !== "") {
            this.query += `&${tag}=${value}`;
        } else {
            this.query = `${tag}=${value}`;
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
     * Returns a list of GeoJSON objects
     *  see https://photon.komoot.io/api/?q=Christchurch for exact format
     *
     * @return {Promise<AxiosResponse<any>>}
     */
    async result() {
        return photonAxios.get(this.toString())
            .then(res => res.data.features)
            .catch();
    }
}

/**
 * Determine whether property retrieved has the valid attributes
 *
 * @param property Resulting property object
 *
 * @returns {boolean} True or false whether it meets criteria
 */
function isValidProperty (property) {
    return property.housenumber !== undefined &&
           property.street !== undefined &&
           property.district !== undefined &&
           property.state !== undefined &&
           property.country !== undefined;
}

/**
 * Query street address from specific parameters
 *
 * @param country Country to search in
 * @param region Region to search in
 * @param city City to search in
 * @param streetNum Street Number
 * @param streetName Partial or whole street name
 *
 * @returns List of streets
 */
async function queryStreetAddress (country, region, city, streetNum, streetName) {
    let queryResults = new Set();

    // If no query inputted, don't continue
    if (!country && !region && !city && !streetNum && !streetName) {
        return [];
    }

    const results = await new AutofillQuery()
        .appendValue(country)
        .appendValue(region)
        .appendValue(city)
        .appendValue(streetNum)
        .appendValue(streetName)
        .result();

    //Loop over results and take the ones that have a house number, street, district
    for (let result of results) {
        let property = result.properties;

        if (isValidProperty(property)) {
            queryResults.add(`${property.street}`);
        }
    }

    return Array.from(queryResults);
}

/**
 * Query regions from a country
 *
 * @param country Country to search for
 * @param region Whole or part of region search
 *
 * @returns List of regions
 */
async function queryRegions (country, region) {
    let queryResults = new Set();

    // If no query inputted, don't continue
    if (!country && !region) {
        return [];
    }

    const results = await new AutofillQuery()
        .appendValue(country)
        .appendValue(region)
        .appendTag("osm_tag", ":region")
        .appendTag("osm_tag", ":city")
        .result();

    //Loop over results and take the ones that have a name
    for (let result of results) {
        let property = result.properties;

        if (property.state) {
            queryResults.add(`${property.state}`);
        }
    }

    return Array.from(queryResults);
}

/**
 * Query cities from a country and region
 *
 * @param country Country to search for
 * @param region Region to search for
 * @param city Whole or partial city name to search
 *
 * @returns List of cities
 */
async function queryCities (country, region, city) {
    let queryResults = new Set();

    // If no query inputted, don't continue
    if (!country && !region && !city) {
        return [];
    }

    const results = await new AutofillQuery()
        .appendValue(country)
        .appendValue(region)
        .appendValue(city)
        .appendTag("osm_tag", ":city")
        .result();

    //Loop over results and take the ones that have a name
    for (let result of results) {
        let property = result.properties;

        if (property.name) {
            queryResults.add(`${property.name}`);
        }
    }

    return Array.from(queryResults);
}

export default {
    AutofillQuery,
    queryStreetAddress,
    queryRegions,
    queryCities
};
