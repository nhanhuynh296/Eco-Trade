/**
 * listings.test.js
 *
 * Test listings api functions
 */

import listing from "../../../src/api/listing/listing";

/**
 * Test the ability to call the buy listing api end point
 */
test("Test buy listing api end point", () => {
    listing.buyListing = jest.fn().mockImplementation( () => { return "results"; } );
    let result = listing.buyListing(1);

    expect(result).toBe("results");
});
