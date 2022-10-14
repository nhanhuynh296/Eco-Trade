/**
 * userProfile.test.js
 *
 * Test userProfile functions
 */

import userProfile from "../../../src/components/users/userProfile";
import avatar from "../../../src/components/../../public/assets/avator.png";

//Server URL
const serverURL = process.env.VUE_APP_SERVER_ADD;

/**
 * Test to check date differences
 */
test("tests date minus another date ", () => {
    let date1 = new Date(2017, 1, 10);
    let date2 = new Date( 2020, 3, 10);

    let diff = userProfile.dateDiff(date1, date2);

    expect(diff).toBe("3 year(s) 2 month(s)");
});

/**
 * Test that full address is correctly formatted for profile page
 */
test("Tests getting the full version of a users address", () => {
    let address = {
        city: "Christchurch",
        country: "New Zealand",
        streetNumber: "123",
        streetName: "Smartsville",
        region: "Canterbury",
        postcode: "8041"
    };

    expect(userProfile.formatFullAddress(address))
        .toBe(`${address.streetNumber} ${address.streetName}, ${address.city}, ${address.region}, ${address.country}, ${address.postcode}`);
})

/**
 * Test that full address is correctly formatted for profile page
 */
test("Tests getting the full version of a users address with nulls", () => {
    let address = {
        city: "Christchurch",
        country: null,
        streetNumber: "123",
        streetName: "Smartsville",
        region: null,
        postcode: "8041"
    };

    expect(userProfile.formatFullAddress(address))
        .toBe(`${address.streetNumber} ${address.streetName}, ${address.city}, ${address.postcode}`);
})

/**
 * Check getting user image for a null primary id returns the default avatar image
 */
test("Test to check getting user image for a null primary id returns the default avatar image", () => {
    expect(userProfile.getUserImageLink(null,  null, false)).toEqual(avatar);
});

/**
 * Check getting user thumbnail for a null primary id returns the default avatar image
 */
test("Test to check getting user thumbnail for a null primary id returns the default avatar image", () => {
    expect(userProfile.getUserImageLink(null,  null, true)).toEqual(avatar);
});

/**
 * Check that the correct user normal image link is returned
 */
test("Check that the correct user normal image link is returned", () => {
    expect(userProfile.getUserImageLink(1, 1,  false)).toEqual(`${serverURL}/users/images/1`);
});

/**
 * Check that the correct user thumbnail image link is returned
 */
test("Check that the correct user thumbnail image link is returned", () => {
    expect(userProfile.getUserImageLink(1, 1,  true)).toEqual(`${serverURL}/users/images/1/thumbnail`);
});