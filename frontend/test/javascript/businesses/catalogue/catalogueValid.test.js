/**
 * catalogueValid.test.js
 *
 * Test validation functions for when adding and modifying a catalogue product
 */


import validateProduct from "../../../../src/components/businesses/catalogue/validateProduct";

/**
 * Tests that error is returned on empty name
 */
test("Tests that error is returned on empty name", () => {

    const expectedResult = ['Required field name is empty!'];
    expect(validateProduct.validateRequired('', 1, []))
        .toStrictEqual(expectedResult)

    expect(validateProduct.validateRequired(null, 1, []))
        .toStrictEqual(expectedResult)
});

/**
 * Tests that error is returned on RRP = 0, RRP < 0, RRP not a number
 */
test("Tests that error is returned on invalid RRP", () => {

    const expectedResult = ['RRP cannot be 0!'];
    expect(validateProduct.validateRequired('Name', 0, []))
        .toStrictEqual(expectedResult)

    expect(validateProduct.validateRequired('Name', -1, []))
        .toStrictEqual(expectedResult)

    expect(validateProduct.validateRequired('Name', 'Not a number', []))
        .toStrictEqual(expectedResult)
});

/**
 * Tests that no error is returned on valid info
 */
test("Tests that no error is returned on valid info", () => {

    const expectedResult = [];
    expect(validateProduct.validateRequired('Name', 1, []))
        .toStrictEqual(expectedResult)

});
