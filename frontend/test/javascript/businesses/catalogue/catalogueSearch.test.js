/**
 * catalogueSearch.test.js
 *
 * Test catalogueSearch functions
 */

import catalogueSearch from "../../../../src/components/businesses/catalogue/catalogueSearch"

const sampleData = [
    {
        id: 1,
        name: "TEST",
        description: "TESTBaked Beans as they should be.",
        recommendedRetailPrice: 2.2,
        manufacturer: "Heinz Wattie's Limited",
        created: "2000-04-29",
        images: [
            {
                id: 1234,
                filename: "/media/images/23987192387509-123908794328.png",
                thumbnailFilename: "/media/images/23987192387509-123908794328_thumbnail.png"
            }
        ]
    },
    {
        id: 2,
        name: "b",
        description: "TESTBaked Beans as they should be.",
        recommendedRetailPrice: 3,
        manufacturer: "Heinz Wattie's Limited",
        created: "2021-04-30T01:02:47.046Z",
        images: [
            {
                id: 1234,
                filename: "/media/images/23987192387509-123908794328.png",
                thumbnailFilename: "/media/images/23987192387509-123908794328_thumbnail.png"
            }
        ]
    },
    {
        id: 3,
        name: "TEST",
        description: "Baked Beans as they should be.",
        recommendedRetailPrice: 4,
        manufacturer: "Heinz Wattie's Limited",
        created: "2021-04-25T01:02:47.046Z",
        images: [
            {
                id: 1234,
                filename: "/media/images/23987192387509-123908794328.png",
                thumbnailFilename: "/media/images/23987192387509-123908794328_thumbnail.png"
            }
        ]
    },
    {
        id: 4,
        name: "d",
        description: "TESTBaked Beans as they should be.",
        recommendedRetailPrice: 123,
        manufacturer: "Manufacturer TEST",
        created: "2021-05-30T01:02:47.046Z",
        images: [
            {
                id: 1234,
                filename: "/media/images/23987192387509-123908794328.png",
                thumbnailFilename: "/media/images/23987192387509-123908794328_thumbnail.png"
            }
        ]
    },
    {
        id: 5,
        name: "e",
        description: "Hello",
        recommendedRetailPrice: 40001 ,
        manufacturer: "Heinz Wattie's Limited",
        created: "2001-04-30T01:02:47.046Z",
        images: [
            {
                id: 1234,
                filename: "/media/images/23987192387509-123908794328.png",
                thumbnailFilename: "/media/images/23987192387509-123908794328_thumbnail.png"
            }
        ]
    },
    {
        id: 50001,
        name: "f",
        description: "Baked Beans as they should be.",
        recommendedRetailPrice: 1,
        manufacturer: "Heinz Wattie's Limited",
        created: "2020-04-30T01:02:47.046Z",
        images: [
            {
                id: 1234,
                filename: "/media/images/23987192387509-123908794328.png",
                thumbnailFilename: "/media/images/23987192387509-123908794328_thumbnail.png"
            }
        ]
    },
]


/**
 * Tests that the 'name' and 'description' searching work, with no duplicates
 */
test("Tests Name and description contains search query", () => {
    const searchQuery = 'TEST';
    const expectedResults = [];
    for (let i = 0; i < 4; i++) {expectedResults.push(sampleData[i]);}
    expect(catalogueSearch.catalogueSearch(searchQuery, sampleData))
        .toStrictEqual(expectedResults);
});

/**
 * Tests that the manufacturer searching work
 */
test("Tests manufacturer contains search query", () => {
    const searchQuery = 'Manufacturer TEST';
    const expectedResults = [sampleData[3]];
    expect(catalogueSearch.catalogueSearch(searchQuery, sampleData))
        .toStrictEqual(expectedResults);
});

/**
 * Tests that a result will be an empty string upon giving a query string that is not in any data
 */
test("Tests empty result", () => {
    const searchQuery = 'THISISNOTINANYSTRING';
    const expectedResults = [];
    expect(catalogueSearch.catalogueSearch(searchQuery, sampleData))
        .toStrictEqual(expectedResults);
});

/**
 * Tests that the id is searchable
 */
test("Tests id is searchable", () => {
    const searchQuery = '50001';
    const expectedResults = [sampleData[5]];
    expect(catalogueSearch.catalogueSearch(searchQuery, sampleData))
        .toStrictEqual(expectedResults);
});

/**
 * Tests that the RRP is searchable
 */
test("Tests RRP is searchable", () => {
    const searchQuery = '40001';
    const expectedResults = [sampleData[4]];
    expect(catalogueSearch.catalogueSearch(searchQuery, sampleData))
        .toStrictEqual(expectedResults);
});

/**
 * Tests the date conversion (from JS Date to dd/mm/yyyy string) with invalid input
 */
test("Tests date conversion from JS Date (as a string) to string with invalid input", () => {
    const date = "INVALID"
    const expectedResult = '';
    expect(catalogueSearch.convertToDateFormatDDMMYYYY(date))
        .toBe(expectedResult);
});
