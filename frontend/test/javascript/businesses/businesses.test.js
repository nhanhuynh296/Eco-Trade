/**
 * businesses.test.js
 *
 * Test business functionality
 */

import businesses from "../../../src/components/businesses/businesses";

//Server URL
const serverURL = process.env.VUE_APP_SERVER_ADD;

/**
 * Test to check field validation when fields are valid
 */
test("Test valid business registration", () => {
    expect(businesses.validateBusinessRegister("McDonalds", "United States", "Retail Trade")).toBe(true)
});

/**
 * Test to check field validation when required fields are empty
 */
test("Test empty required fields in business registration", () => {
    expect(businesses.validateBusinessRegister("McDonalds", "United States", null)).toBe(false)
    expect(businesses.validateBusinessRegister("McDonalds", null, "Retail Trade")).toBe(false)
    expect(businesses.validateBusinessRegister(null, "United States", "Retail Trade")).toBe(false)
});

/**
 * Test to check type valid type validation
 */
test("Test valid type field for business registration", () => {
    expect(businesses.validateBusinessType('Accommodation')).toBe(true)
    expect(businesses.validateBusinessType('Food Services')).toBe(true)
    expect(businesses.validateBusinessType('Retail Trade')).toBe(true)
    expect(businesses.validateBusinessType('Charitable organisation')).toBe(true)
    expect(businesses.validateBusinessType('Non-profit organisation')).toBe(true)
});

/**
 * Test to check invalid type validation
 */
test("Test invalid type field for business registration", () => {
    expect(businesses.validateBusinessType('invalid business type')).toBe(false)
    expect(businesses.validateBusinessType(1)).toBe(false)
});

/**
 * Test to check getting businesses product images thumbnail with invalid business ID
 */
test("Test invalid business ID for getting image", () => {
    expect(businesses.getProductImageLink(null, 50, true)).toEqual(`https://bulma.io/images/placeholders/128x128.png`);
});

/**
 * Test to check getting businesses product images thumbnail with invalid product ID
 */
test("Test to check getting businesses product images thumbnail with invalid product ID", () => {
    expect(businesses.getProductImageLink(20, null, true)).toEqual(`https://bulma.io/images/placeholders/128x128.png`);
});

/**
 * Test to check getting businesses product images thumbnail with valid details
 */
test("Test to check getting businesses product images thumbnail with valid details", () => {
    expect(businesses.getProductImageLink(1, 1, true)).toEqual(`${serverURL}/business/1/products/thumbnail/1`);
});

/**
 * Test to check getting businesses product images with valid details but wrong
 */
test("Test to check getting businesses product images with valid details but wrong", () => {
    expect(businesses.getProductImageLink(-5, -600, true)).toEqual(`${serverURL}/business/-5/products/thumbnail/-600`);
});

/**
 * Test to check getting businesses product images with valid details non thumbnail
 */
test("Test to check getting businesses product images with valid details non thumbnail", () => {
    expect(businesses.getProductImageLink(1, 1, false)).toEqual(`${serverURL}/business/1/products/images/1`);
});

/**
 * Test to check getting businesses product images with valid details but wrong
 */
test("Test invalid product ID for getting image", () => {
    expect(businesses.getListingImageLink(-5,  true)).toEqual(`${serverURL}/thumbnail/-5`);
});

/**
 * Test to check getting businesses product images with valid details non thumbnail
 */
test("Test to check getting businesses product images with valid details non thumbnail", () => {
    expect(businesses.getListingImageLink(1,  false)).toEqual(`${serverURL}/images/1`);
});

/**
 * Test to check getting businesses product images null details non thumbnail
 */
test("Test to check getting businesses product images null details non thumbnail", () => {
    expect(businesses.getListingImageLink(null,  false)).toEqual(`https://bulma.io/images/placeholders/128x128.png`);
});

/**
 * Tests for retrieving listings search query by product name
 */
test("Test retrieving a search query for listings search by product name", () => {
    let searchQuery = 'product "product name" AND store OR mall';
    let checkQuery = '?search=inventoryItem.product.name:*product* AND inventoryItem.product.name:"product name" AND inventoryItem.product.name:*store* OR inventoryItem.product.name:*mall*';
    let finalQuery = businesses.getListingsSearchQuery(searchQuery);

    expect(finalQuery).toStrictEqual(checkQuery);
});

/**
 * Tests for retrieving listings search query by product name with empty query
 */
test("Test retrieving a search query for listings search with empty query", () => {
    let searchQuery = "";
    let checkQuery = '?search=';
    let finalQuery = businesses.getListingsSearchQuery(searchQuery);

    expect(finalQuery).toStrictEqual(checkQuery);
});

/**
 * Tests for retrieving listings search query with filter with business name
 */
test("Test retrieving a search query for listings search with filter business name", () => {
    let searchQuery = '?search=';

    let businessName = 'business';
    let checkQuery = '?search= inventoryItem.product.business.name:*business*';
    let finalQuery = businesses.addBusinessNameSearch(searchQuery, businessName);

    expect(finalQuery).toStrictEqual(checkQuery);

    businessName = 'business name';
    checkQuery = '?search= inventoryItem.product.business.name:*business* AND inventoryItem.product.business.name:*name*';
    finalQuery = businesses.addBusinessNameSearch(searchQuery, businessName);

    expect(finalQuery).toStrictEqual(checkQuery);
});

/**
 * Tests for retrieving listings search query with filter business type
 */
test("Test retrieving a search query for listings search with filter business type", () => {
    let searchQuery = '?search=';

    let businessType = ["TypeOne"];
    let checkQuery = '?search= ( inventoryItem.product.business.businessType:"TypeOne")';
    let finalQuery = businesses.addBusinessTypeSearch(searchQuery, businessType);

    expect(finalQuery).toStrictEqual(checkQuery);

    businessType = ["TypeOne", "TypeTwo"];
    checkQuery = '?search= ( inventoryItem.product.business.businessType:"TypeOne" OR inventoryItem.product.business.businessType:"TypeTwo")';
    finalQuery = businesses.addBusinessTypeSearch(searchQuery, businessType);

    expect(finalQuery).toStrictEqual(checkQuery);
});

/**
 * Tests for retrieving listings search query with filter business address
 */
test("Test retrieving a search query for listings search with filter business address", () => {
    let searchQuery = '?search=';

    // All values are null
    let checkQuery = '?search=';
    let finalQuery = businesses.addBusinessAddressSearch(searchQuery, null, null, null);

    expect(finalQuery).toStrictEqual(checkQuery);

    // Only country has a value
    let country = "Country";
    checkQuery = '?search= inventoryItem.product.business.address.country:"Country"';
    finalQuery = businesses.addBusinessAddressSearch(searchQuery, country, null, null);

    expect(finalQuery).toStrictEqual(checkQuery);

    // Only region has a value
    let region = "Region";
    checkQuery = '?search= inventoryItem.product.business.address.region:"Region"';
    finalQuery = businesses.addBusinessAddressSearch(searchQuery, null, region, null);

    expect(finalQuery).toStrictEqual(checkQuery);

    // Only city has a value
    let city = "City";
    checkQuery = '?search= inventoryItem.product.business.address.city:"City"';
    finalQuery = businesses.addBusinessAddressSearch(searchQuery, null, null, city);

    expect(finalQuery).toStrictEqual(checkQuery);

    // Only country and region have a value
    checkQuery = '?search= inventoryItem.product.business.address.country:"Country" AND inventoryItem.product.business.address.region:"Region"';
    finalQuery = businesses.addBusinessAddressSearch(searchQuery, country, region, null);

    expect(finalQuery).toStrictEqual(checkQuery);

    // Only country and city have a value
    checkQuery = '?search= inventoryItem.product.business.address.country:"Country" AND inventoryItem.product.business.address.city:"City"';
    finalQuery = businesses.addBusinessAddressSearch(searchQuery, country, null, city);

    expect(finalQuery).toStrictEqual(checkQuery);

    // Only region and city have a value
    checkQuery = '?search= inventoryItem.product.business.address.region:"Region" AND inventoryItem.product.business.address.city:"City"';
    finalQuery = businesses.addBusinessAddressSearch(searchQuery, null, region, city);

    expect(finalQuery).toStrictEqual(checkQuery);

    // All values are not null
    checkQuery = '?search= inventoryItem.product.business.address.country:"Country" AND inventoryItem.product.business.address.region:"Region" AND inventoryItem.product.business.address.city:"City"';
    finalQuery = businesses.addBusinessAddressSearch(searchQuery, country, region, city);

    expect(finalQuery).toStrictEqual(checkQuery);
});

/**
 * Tests for retrieving listings search query with filter closing date
 */
test("Test retrieving a search query for listings search with filter closing date", () => {
    let searchQuery = '?search=';
    let closingDate = new Date("2021-07-14");
    let checkQuery = '?search= closes:2021-07-14';
    let finalQuery = businesses.addClosingDateSearch(searchQuery, closingDate);

    expect(finalQuery).toStrictEqual(checkQuery);
});

/**
 * Tests for retrieving listings search query with filter price range
 */
test("Test retrieving a search query for listings search with filter price range", () => {
    let searchQuery = '?search=';
    let range = [400, 235];
    let checkQuery = '?search= (price:400 OR price>400) AND (price:235 OR price<235)';
    let finalQuery = businesses.addPriceRangeSearch(searchQuery, range);

    expect(finalQuery).toStrictEqual(checkQuery);
});

/**
 * Tests for checking weather the search query is empty or not
 * Adds an AND to the start of the query if the query is not empty
 */
test("Test for checking if the query is empty", () => {
    let searchQuery = '?search=';
    let checkQuery = '?search=';
    let finalQuery = businesses.checkSearchQuery(searchQuery);

    expect(finalQuery).toStrictEqual(checkQuery);

    searchQuery = '?search= price:10';
    checkQuery = '?search= price:10 AND';
    finalQuery = businesses.checkSearchQuery(searchQuery);

    expect(finalQuery).toStrictEqual(checkQuery);
});

/**
 * This function adds "..." after a given number of characters and deleted the rest of the characters
 * Used for the table so that large values do not seriously disfigure the buefy tables.
 */
test("Check that maxDisplay returns a truncated string for large input", () => {
    let stringValue = '===================================================' +
        '=================================================================' +
        '=================================================================';
    let result = businesses.maxDisplay(stringValue, 30);

    expect(result.length).toEqual(33);
});

/**
 * This function adds "..." after a given number of characters and deleted the rest of the characters
 * Used for the table so that large values do not seriously disfigure the buefy tables.
 */
test("Check that maxDisplay returns original string for small input", () => {
    let stringValue = 'This is less than 40 characters';
    let result = businesses.maxDisplay(stringValue, 40);

    expect(result).toEqual(stringValue);
});

/**
 * Checks that 2 decimal places are being returned
 */
test("Checks that 2 decimal places are returned for 3 decimal place value", () => {
    let result = businesses.roundDecimalPlaces(45.123);

    expect(result).toStrictEqual(45.12);
});

/**
 * Checks that 2 or less decimal places are being returned
 */
test("Checks that 1 decimal places are returned for 1 decimal place value", () => {
    let result = businesses.roundDecimalPlaces(45.1);

    expect(result).toStrictEqual(45.1);
});

/**
 * Checks that 2 or less decimal places are being returned
 */
test("Checks that 0 decimal places are returned for 0 decimal place value", () => {
    let result = businesses.roundDecimalPlaces(45.);

    expect(result).toStrictEqual(45.);
});

/**
 * Checks that given a null value it returns 0 without breaking
 */
test("Check a null value for roundDecimalPlaces", () => {
    let result = businesses.roundDecimalPlaces(null);

    expect(result).toStrictEqual(0);
});

