/**
 * listingValid.test.js
 *
 * Test validation functions for when adding a listing
 */


import validateListing from "../../../../src/components/businesses/listings/validateListing";

/**
 * Tests that errors are returned for an invalid quantity
 */
test("Tests a listing quantity cannot be zero or null", () => {

    let today = new Date()
    let minClosingDate = new Date(today.getFullYear() , today.getMonth(), today.getDate() + 1);
    const expectedResult = ['Quantity must be a positive whole number!'];
    expect(validateListing.validateRequired(0, 1, minClosingDate))
        .toStrictEqual(expectedResult)

    expect(validateListing.validateRequired(null, 1, minClosingDate))
        .toStrictEqual(expectedResult)
});

/**
 * Tests that errors are returned for an invalid price
 */
test("Tests a listing price cannot be zero or null", () => {
    let today = new Date()
    let minClosingDate = new Date(today.getFullYear() , today.getMonth(), today.getDate() + 1);
    const expectedResult = ['Price must be a positive number!'];
    expect(validateListing.validateRequired(1, 0, minClosingDate))
        .toStrictEqual(expectedResult)

    expect(validateListing.validateRequired(1, null, minClosingDate))
        .toStrictEqual(expectedResult)
});

/**
 * Tests that errors are returned for an invalid closing date
 */
test("Tests a closing date for a listing cannot be in the past or null", () => {
    let today = new Date()
    let invalidClosingDate = new Date(today.getFullYear() , today.getMonth(), today.getDate() + -4);
    const expectedResult = ['Closing date must be in the future!'];
    expect(validateListing.validateRequired(1, 1, invalidClosingDate))
        .toStrictEqual(expectedResult)

    expect(validateListing.validateRequired(1, 1, null))
        .toStrictEqual(expectedResult)
});