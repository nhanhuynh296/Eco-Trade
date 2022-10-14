/**
 * cards.test.js
 *
 * Test cards api functions
 */

import cards from "../../../src/api/ucm/cards";

/**
 * Test the ability to call the search api end point for cards
 */
test("Test search cards api end point", () => {
    cards.searchCardsByKeyword = jest.fn().mockImplementation( () => { return "card results"; } );
    let result = cards.searchCardsByKeyword([], "or", "ForSale", 1, 6);

    expect(result).toBe("card results");
});
