/**
 * inventoryHelper.test.js
 *
 * Test inventory helper functionality
 */
import inventoryHelper from "../../../../src/components/businesses/inventory/inventoryHelper";


/**
 * Test to check converting valid item objects to strings
 */
test("Test valid item objects to strings", () => {
    const itemObjects = [
        {name: "TestObject", id: 1},
        {name: "Hello", id: 2},
        {name: "World", id: 3},
        {name: "Help me", id: 1000},
        {name: "H", id: 69},
        {name: "I", id: 420},
    ];

    const expected = [
        "TestObject (ID: 1)",
        "Hello (ID: 2)",
        "World (ID: 3)",
        "Help me (ID: 1000)",
        "H (ID: 69)",
        "I (ID: 420)",
    ];

    const result = inventoryHelper.convertItemsToStrings(itemObjects);

    for (let i = 0; i < result.length; i++) {
        expect(result[i]).toBe(expected[i]);
    }
});

/**
 * Test to check converting partial invalid item objects to strings
 * results in only valid strings being added
 */
test("Test partial invalid item objects to strings", () => {
    const itemObjects = [
        {name: "TestObject", id: 1},
        {name: "", id: 2},
        {name: null, id: 3},
        {name: "Help me", id: null},
        {name: null, id: null},
        {name: "I", id: 420},
    ];

    const expected = [
        "TestObject (ID: 1)",
        "I (ID: 420)",
    ];

    const result = inventoryHelper.convertItemsToStrings(itemObjects);

    for (let i = 0; i < result.length; i++) {
        expect(result[i]).toBe(expected[i]);
    }
});

/**
 * Test converting completely invalid item objects to strings
 */
test("Test completely invalid item objects to strings", () => {
    const itemObjects = [
        {},
        null,
    ];

    let result = inventoryHelper.convertItemsToStrings(itemObjects);
    expect(result).toStrictEqual([]);

    result = inventoryHelper.convertItemsToStrings(null);
    expect(result).toStrictEqual([]);
});

/**
 * Test converting valid item strings to ids
 */
test("Test converting item strings back to the respective ID", () => {
    const itemObjects = [
        { name: "TestObject", id: 1 },
        { name: "Hello", id: 2 },
        { name: "World", id: 3 },
        { name: "Help me", id: 1000 },
        { name: "H", id: 69 },
        { name: "I", id: 420 },
    ];

    const itemStrings = [
        "TestObject (ID: 1)",
        "Hello (ID: 2)",
        "World (ID: 3)",
        "Help me (ID: 1000)",
        "H (ID: 69)",
        "I (ID: 420)",
    ];

    const expected = [
        1,
        2,
        3,
        1000,
        69,
        420
    ];

    for (let i = 0; i < itemStrings.length; i++) {
        expect(inventoryHelper.convertStringsToIds(itemStrings[i], itemObjects)).toBe(expected[i]);
    }
});

/**
 * Test converting partial invalid item strings to IDs
 */
test("Test partial converting invalid item strings back to the respective ID", () => {
    const itemObjects = [
        { name: "Item", id: 1 }
    ];

    const itemStrings = [
        null,
        "Item (ID: 1)",
        ""
    ];

    expect(inventoryHelper.convertStringsToIds(itemStrings[0], itemObjects)).toBeNull();
    expect(inventoryHelper.convertStringsToIds(itemStrings[1], itemObjects)).toBe(1);
    expect(inventoryHelper.convertStringsToIds(itemStrings[2], itemObjects)).toBeNull();
});

/**
 * Test getting valid RRP of product
 */
test("Test getting recommended retail price from product correctly", () => {
    const itemObjects = [
        {name: "TestObject", id: 1, recommendedRetailPrice: 2.2},
        {name: "Hello", id: 2, recommendedRetailPrice: 3.6},
        {name: "World", id: 3, recommendedRetailPrice: 4.9},
    ];

    const expected = [
        2.2,
        3.6,
        4.9
    ];
    const result = inventoryHelper.convertItemsToStrings(itemObjects);

    for (let i = 0; i < result.length; i++) {
        expect(inventoryHelper.convertStringToRPP(itemObjects[i].name, itemObjects)).toBe(expected[i]);
    }
});

/**
 * Test getting invalid RRP of product
 */
test("Test not getting recommended retail price from product correctly", () => {
    const itemObjects = [
        {name: "TestObject", id: 1, recommendedRetailPrice: -100},
        {name: "Hello", id: 2, recommendedRetailPrice: null},
        {name: "World", id: 3, recommendedRetailPrice: -0.01},
        {name: "Help me", id: 10, recommendedRetailPrice: 10},
    ];

    const expected = [
        null,
        null,
        null,
        10
    ];
    const result = inventoryHelper.convertItemsToStrings(itemObjects);

    for (let i = 0; i < result.length; i++) {
        expect(inventoryHelper.convertStringToRPP(itemObjects[i].name, itemObjects)).toBe(expected[i]);
    }
});


/**
 * Test missing fields
 */
test("Test missing productId, quantity, or expires", () => {
   let errors = inventoryHelper.validateFields(
       null,
       null,
       null,
       null,
       null,
       null,
       null,
       null,
   )
   expect(errors).toStrictEqual(["Missing required fields!"]);

   let date = new Date();
   date.setDate(date.getDate() + 1)
});

/**
 * Test that expire date is in future
 */
test("test that the expire date is correctly validate to be in the future", () => {
    let date = new Date();

    let errors = inventoryHelper.validateFields(
        2,
        2,
        1,
        2,
        null,
        null,
        null,
        date
    )
    expect(errors).toStrictEqual(["Expire date must be in the future"])

    date.setDate(date.getDate() + 1)

    let errors2 = inventoryHelper.validateFields(
        2,
        2,
        2,
        2,
        null,
        null,
        null,
        date
    )
    expect(errors2).toHaveLength(0);
})

/**
 * Tests that quantity has to be non-negative
 * Note: quantity = 0 returns a 'missing requirements' error
 */
test("test that a negative quantity returns errors", () => {
    let date = new Date();
    date.setDate(date.getDate() + 1);

    let errors = inventoryHelper.validateFields(
        2,
        -1,
        1,
        2,
        null,
        null,
        null,
        date
    )

    expect(errors).toStrictEqual(["Please specify a positive number for quantity."])
})

/**
 * Tests that price per item has to be non-negative
 */
test("test that a negative price per item returns errors", () => {
    let date = new Date();
    date.setDate(date.getDate() + 1);

    let errors = inventoryHelper.validateFields(
        2,
        1,
        -1,
        1,
        null,
        null,
        null,
        date
    )

    expect(errors).toStrictEqual(["Please specify a positive number for the price per item."])
})

/**
 * Tests that total price has to be non-negative
 */
test("test that a negative total price returns errors", () => {
    let date = new Date();
    date.setDate(date.getDate() + 1);

    let errors = inventoryHelper.validateFields(
        2,
        1,
        1,
        -1,
        null,
        null,
        null,
        date
    )

    expect(errors).toStrictEqual(["Please specify a positive number for the total price."])
})

/**
 * Tests that an invalid (too far in the future) manufactured date returns errors
 */
test("test errors returned when manufactured date is greater than all other dates", () => {
    let manufactureDate = new Date();
    let expireDate = new Date();
    let sellByDate = new Date();
    let bestBeforeDate = new Date();
    // best before < sell by < expire < manufacture
    bestBeforeDate.setDate(bestBeforeDate.getDate() + 1);
    sellByDate.setDate(sellByDate.getDate() + 2);
    expireDate.setDate(expireDate.getDate() + 3);
    manufactureDate.setDate(manufactureDate.getDate() + 4);

    let errors = inventoryHelper.validateFields(
        1,
        1,
        1,
        1,
        manufactureDate,
        sellByDate,
        bestBeforeDate,
        expireDate
    )

    let expectedErrors = [
        'Manufactured date must be before expired date.',
        'Manufactured date must be before sell by date.',
        'Manufactured date must be before best before date.'
    ]
    expect(errors).toStrictEqual(expectedErrors);

})

/**
 * Test that error is returned if sell by >= expire
 */
test("Test that error is returned if sell by >= expire", () => {
    let expireDate = new Date();
    let sellByDate = new Date();
    // best before < sell by < expire < manufacture
    sellByDate.setDate(sellByDate.getDate() + 3);
    expireDate.setDate(expireDate.getDate() + 2);

    let errors = inventoryHelper.validateFields(
        1,
        1,
        1,
        1,
        null,
        sellByDate,
        null,
        expireDate
    )
    expect(errors).toStrictEqual(["Sell by date must be before expired date."])
})

/**
 * Test that error is returned if best before >= expire
 */
test("Test that error is returned if best before >= expire", () => {
    let expireDate = new Date();
    let bestBefore = new Date();
    // best before < sell by < expire < manufacture
    bestBefore.setDate(bestBefore.getDate() + 3);
    expireDate.setDate(expireDate.getDate() + 2);

    let errors = inventoryHelper.validateFields(
        1,
        1,
        1,
        1,
        null,
        null,
        bestBefore,
        expireDate
    )
    expect(errors).toStrictEqual(["Best before date must be before expired date."])
})
