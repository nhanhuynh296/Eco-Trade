/**
 * autofillQuery.test.js
 *
 * Test autofill functions
 */
import AutofillQuery from "../../../src/api/photon/autofillQuery";

/**
 * Test that when building a string with the value of "New Zealand" that
 * the resulting query string is of a specific format
 */
test("Test simple autofill API query", () => {
    const query = new AutofillQuery.AutofillQuery().appendValue("New Zealand");

    expect(query.toString()).toBe("api/?q=New Zealand");
});

/**
 * Test that when an empty string is provided to the query
 * we get only the api query part
 */
test("Test simple undefined autofill API query", () => {
    const query = new AutofillQuery.AutofillQuery();

    expect(query.toString()).toBe("api/?q=");
});

/**
 * Test that when we append an autofill query tag, that
 * we get the resulting query of a specific format
 */
test("Test only a tag with autofill API query", () => {
    const query = new AutofillQuery.AutofillQuery().appendTag("test", "val");

    expect(query.toString()).toBe("api/?q=test=val");
});

/**
 * Test the builder pattern builds our query in a specific way, complex as we're
 * using multiple values and tags
 */
test("Test slightly more complex autofill API query", () => {
    const query = new AutofillQuery.AutofillQuery()
        .appendValue("Christchurch")
        .appendValue("Canterbury")
        .appendTag("test", "val");

    expect(query.toString()).toBe("api/?q=Christchurch Canterbury&test=val");
});

/**
 * Test actually querying the API and seeing if we can get a country back
 */
test("Test getting New Zealand", async () => {
    const result = await new AutofillQuery.AutofillQuery().appendValue("New Zealand").result().catch();
    expect(result[0].properties.name).toContain("New Zealand");
});

/**
 * Test querying a street address and seeing if we get a match
 */
test("Test converting results into a list", async () => {
    const list = await AutofillQuery
        .queryStreetAddress("New Zealand", "Canterbury", "Christchurch", "23 Ilam Park Place");

    expect(list.length).toBeGreaterThan(0);
});

/**
 * Test querying for regions in a specific country
 */
test("Tests querying regions in a country", async () => {
    const list = await AutofillQuery
        .queryRegions("New Zealand");

    expect(list.length).toBeGreaterThan(0);
    expect(list[0]).toBeDefined();
});

/**
 * Test querying for regions in a specific country and partial region
 */
test("Tests querying regions in a country and partial region", async () => {
    const list = await AutofillQuery
        .queryRegions("New Zealand", "Wel");

    expect(list.length).toBeGreaterThan(0);
    expect(list[0]).toBe("Wellington");
});

/**
 * Test querying for empty region and country
 */
test("Tests querying empty strings", async () => {
    const list = await AutofillQuery
        .queryRegions("", "");

    expect(list.length).toBe(0);
});

/**
 * Test querying for regions in a specific country
 */
test("Tests querying regions in a country", async () => {
    const list = await AutofillQuery
        .queryRegions("New Zealand");

    expect(list.length).toBeGreaterThan(0);
    expect(list[0]).toBeDefined();
});

/**
 * Test querying for regions in a specific country and partial region
 */
test("Tests querying regions in a country and partial region", async () => {
    const list = await AutofillQuery
        .queryRegions("New Zealand", "Wel");

    expect(list.length).toBeGreaterThan(0);
    expect(list[0]).toBe("Wellington");
});

/**
 * Test querying for empty region and country
 */
test("Tests querying empty strings", async () => {
    const list = await AutofillQuery
        .queryRegions("", "");

    expect(list.length).toBe(0);
});

/**
 * Test querying for cities in a specific country
 */
test("Tests querying cities in a country", async () => {
    const list = await AutofillQuery
        .queryCities("United States");

    expect(list.length).toBeGreaterThan(0);
});

/**
 * Test querying for cities in a specific country and region
 */
test("Tests querying cities in a country and region", async () => {
    const list = await AutofillQuery
        .queryCities("New Zealand", "Canterbury");

    expect(list.length).toBeGreaterThan(0);
    expect(list[0]).toBeDefined();
});

/**
 * Test querying for empty region, country and city
 */
test("Tests querying empty strings", async () => {
    const list = await AutofillQuery
        .queryCities("", "", "");

    expect(list.length).toBe(0);
});
