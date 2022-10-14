/**
 * userSearch.test.js
 *
 * Test userSearch functions
 */

import userSearch from "../../../src/components/users/search/userSearch";

/**
 * Tests turning a list of business objects into the correct string representation
 */
test("Tests getting stringified version of businesses", () => {
    const businesses = [
        {
            name: "Lumbridge General Store"
        },
        {
            name: "Varrock Grand Exchange"
        }
    ];

    expect(userSearch.convertBusinessListToString(businesses))
        .toBe(`Businesses Administrated: ${businesses[0].name}, ${businesses[1].name}`);
})

/**
 * Tests turning a list of no businesses into the correct format
 */
test("Tests getting empty version of a business list into right format", () => {
    const businesses = [];

    expect(userSearch.convertBusinessListToString(businesses))
        .toBe('');
})

/**
 * Tests turning a null list into the correct format
 */
test("Tests getting null businesses into the right format", () => {
    const businesses = null;

    expect(userSearch.convertBusinessListToString(businesses))
        .toBe('');
})

/**
 * Tests whether or not checking if a role is admin or not works
 */
test("Tests admin checking based on strings", () => {
    const roles = ["No", "Also No", "ROLE_DEFAULT", "ROLE_ADMIN", "ROLE_DEFAULT_ADMIN", null, ''];
    const bools = [false, false, false, true, true, false, false];
    const bools2 = [false, false, false, false, true, false, false];
    const output = roles.map(role => userSearch.isAdminRole(role));
    const output2 = roles.map(role => userSearch.isAdminRole(role, true));

    expect(bools.length === output.length)
        .toBeTruthy();

    expect(bools.every((value, index) => value === output[index]))
        .toBeTruthy();

    expect(bools2.every((value, index) => value === output2[index]))
        .toBeTruthy();
})