/**
 * navBar.test.js
 *
 * Testing construction and separation of search functions
 */

import navBar from "../../../src/components/home/navBar";

/**
 * Tests for retrieving business search query
 */
test("Test retrieving a search query for business search", () => {
    let searchQuery = 'business "business name" AND store OR mall';
    let checkQuery = '?search=name:*business* AND name:"business name" AND name:*store* OR name:*mall*';
    let finalQuery = navBar.searchBusinesses(searchQuery, null);

    expect(finalQuery).toStrictEqual(checkQuery);
});

/**
 * Tests for retrieving the split query with different elements
 */
test("Test splitting the business search query with different elements", () => {
    let searchQuery = 'business "business name" AND store OR mall';
    let checkList = ['*business*', '"business name"', 'AND', '*store*', 'OR', '*mall*'];
    let finalList = navBar.splitQuery(searchQuery.split(" "));

    expect(finalList).toStrictEqual(checkList);
});

/**
 * Tests for retrieving the final query with no elements
 */
test("Test constructing a business search query with no elements", () => {
    let checkQuery = '?search=businessType:"Accommodation" OR businessType:"Food Services" OR businessType:"Retail Trade"' +
        ' OR businessType:"Charitable organisation" OR businessType:"Non-profit organisation"';
    let finalQuery = navBar.constructBusinessSearch(['**'], null);

    expect(finalQuery).toStrictEqual(checkQuery);
});

/**
 * Tests for retrieving the final query with business type
 */
test("Test constructing a business search query with only a business type", () => {
    let checkQuery = '?search=businessType:"Accommodation"';
    let finalQuery = navBar.constructBusinessSearch(['**'], "Accommodation");

    expect(finalQuery).toStrictEqual(checkQuery);
});

/**
 * Tests for retrieving the final query with only elements
 */
test("Test constructing a business search query with only elements", () => {
    let searchList = ['*business*', '"business name"', 'AND', '*store*', 'OR', '*mall*'];
    let checkQuery = '?search=name:*business* AND name:"business name" AND name:*store* OR name:*mall*';
    let finalQuery = navBar.constructBusinessSearch(searchList, null);

    expect(finalQuery).toStrictEqual(checkQuery);
});

/**
 * Tests for retrieving the final query with elements and business type
 */
test("Test constructing a business search query with elements and business type", () => {
    let searchList = ['*business*', '"business name"', 'AND', '*store*', 'OR', '*mall*'];
    let checkQuery = '?search=name:*business* AND name:"business name" AND name:*store* OR name:*mall* AND businessType:"Accommodation"';
    let finalQuery = navBar.constructBusinessSearch(searchList, "Accommodation");

    expect(finalQuery).toStrictEqual(checkQuery);
});

/**
 * Tests the function for constructing the user search with only one search term
 */
test("Test constructing a user search query with one search term", () => {
    let splitQuery = ["*John*"];
    let expectedQuery = '?search=(firstName:*John* OR lastName:*John* OR nickname:*John*)';
    let actualQuery = navBar.constructUserSearch(splitQuery);

    expect(actualQuery).toStrictEqual(expectedQuery)
});

/**
 * Tests the function for constructing the user search with multiple search terms
 */
test("Test constructing a user search query with three search terms", () => {
    let splitQuery = ["*John*", "AND", "*Mary*", "OR", "*Swapnil*"];
    let expectedQuery = '?search=(firstName:*John* OR lastName:*John* OR nickname:*John*) AND  ' +
        '(firstName:*Mary* OR lastName:*Mary* OR nickname:*Mary*) OR  ' +
        '(firstName:*Swapnil* OR lastName:*Swapnil* OR nickname:*Swapnil*)';
    let actualQuery = navBar.constructUserSearch(splitQuery);

    expect(actualQuery).toStrictEqual(expectedQuery)
});

/**
 * Tests the function for constructing the user search with an empty search query
 */
test("Test constructing a user search query with an empty search term", () => {
    let splitQuery = ["**"];
    let expectedQuery = '?search=email:*@*';
    let actualQuery = navBar.constructUserSearch(splitQuery);

    expect(actualQuery).toStrictEqual(expectedQuery)
});

/**
 * Tests the function for constructing the user search with an exact search
 */
test("Test constructing a user search query with an exact search term", () => {
    let splitQuery = ['"John Johnson"'];
    let expectedQuery = '?search=((firstName:John OR lastName:John OR nickname:John) AND ' +
        '(firstName:Johnson OR lastName:Johnson OR nickname:Johnson))';
    let actualQuery = navBar.constructUserSearch(splitQuery);

    expect(actualQuery).toStrictEqual(expectedQuery)
});
