/**
 * currencyQuery.test.js
 *
 * Test currency query functions
 */
import currencyQuery from "../../../src/api/restcountries/currencyQuery";
import api from "../../../src/api/api";

//Mock the getBusiness API method for this test
api.businesses.getBusinessCountryForCurrency = jest.fn().mockImplementation(async () => { return { data: { } }; });
api.businesses.getBusiness = jest.fn().mockImplementation(async () => { return { data: { address: { country: "Australia" } } } });

let result = {
    0: {
        name: 'New Zealand',
        currencies: [{ code: 'NZD', symbol: '$'}],
    },
    response: { status: 404 }
};

jest.spyOn(currencyQuery.CurrencyQuery.prototype, 'result').mockImplementation(async () => { return result; });

/**
 * Test that when building a string with the value of "New Zealand" that
 * the resulting query string is of a specific format
 */
test("Test simple currency API query", () => {
    const query = new currencyQuery.CurrencyQuery().appendValue("New Zealand");

    expect(query.toString()).toBe("v2/New Zealand");
});

/**
 * Test that when building a string with the value of "name/" and "Australia" that
 * the resulting query string is of a specific format
 */
test("Test currency API query", () => {
    const query = new currencyQuery.CurrencyQuery()
        .appendValue("name/")
        .appendValue("Australia");

    expect(query.toString()).toBe("v2/name/Australia");
});

/**
 * Test that when building a string with the value of null that
 * the resulting query string is of a specific format
 */
test("Test currency API query null", () => {
    const query = new currencyQuery.CurrencyQuery()
        .appendValue(null)
        .appendValue();

    expect(query.toString()).toBe("v2/");
});

/**
 * Test an valid actual query to the "restcountries" API
 */
test("Test valid actual currency API query", async () => {
    const result = await new currencyQuery.CurrencyQuery()
        .appendValue("name/")
        .appendValue("New Zealand")
        .result();

    expect(result).toBeDefined();
    expect(result[0].name).toBe("New Zealand");
    expect(result[0].currencies).toBeDefined();
});

/**
 * Test an invalid actual query to the "restcountries" API
 */
test("Test invalid actual currency API query", async () => {
    const result = await new currencyQuery.CurrencyQuery()
        .appendValue("name/")
        .appendValue("AAAAA")
        .result();

    expect(result.response.status).toBe(404);
});

/**
 * Test actual query to the "restcountries" API with currency for
 *  "New Zealand"
 */
test("Test querying for New Zealand currency", async () => {
    const currency = await currencyQuery.queryCountryCurrency("New Zealand").catch();

    expect(currency.code).toBe("NZD");
    expect(currency.symbol).toBe("$");
});


/**
 * Test actual query to the "restcountries" API with currency for
 *  "Bhutan", it has multiple currencies
 */
test("Test querying for Bhutan currency", async () => {
    result = {
        0: {
            currencies: [{ code: 'BTN', symbol: '$'}],
        },
    };

    const currency = await currencyQuery.queryCountryCurrency("Bhutan").catch();

    expect(currency.code).toBe("BTN");
});

/**
 * Test null country query
 */
test("Test querying for null currency", async () => {
    const currency = await currencyQuery.queryCountryCurrency(null).catch();

    expect(currency).toBeNull();
});

/**
 * Test invalid country query
 */
test("Test querying for invalid currency", async () => {
    result = null;
    const currency = await currencyQuery.queryCountryCurrency("AAAAAAAAAA").catch();

    expect(currency).toBeNull();
});

/**
 * Test getting current business currency
 */
test("Test querying for current business currency", async () => {
    localStorage.setItem("business-id", 1);

    const currency = await currencyQuery.getCurrentBusinessCurrency().catch();

    expect(currency).toBeDefined();
    expect(currency.code).toBeDefined();
    expect(currency.symbol).toBeDefined();

});

/**
 * Test getting invalid current business currency
 */
test("Test querying for invalid current business currency", async () => {
    localStorage.setItem("business-id", null);

    const currency = await currencyQuery.getCurrentBusinessCurrency().catch();

    expect(currency).toBeDefined();
    expect(currency.code).toBe("NZD");
    expect(currency.symbol).toBe("$");
});
