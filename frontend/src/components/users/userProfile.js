/**
 * userProfile.js
 *
 * File containing user profile functions
 */

//Server URL
const serverURL = process.env.VUE_APP_SERVER_ADD;
import avatar from "@/components/../../public/assets/avator.png";

/**
 * Calculates and returns the difference between 2 dates
 *
 * @param dateOne a date variable
 * @param dateTwo a date variable
 *
 * @return a string that contains rounded number or years and months
 */
const dateDiff = function (dateOne, dateTwo) {
    let days = Math.round((dateTwo - dateOne)/(1000*60*60*24));
    let months = (days / 362) * 12;
    let years = (days / 362);

    while (months >= 12) {
        months = months - 12;

        if (months < 12) {
            break;
        }
    }

    years = years < 0 ? 0 : years;
    months = months < 0 ? 0 : months;

    return Math.floor(years) + " year(s) " + Math.floor(months) + " month(s)";
}

/**
 * Constructs address string from users address object
 *
 * @param addressObject The users address
 *
 * @return a string that does contain the street address and the suburb
 */
const formatFullAddress = function(addressObject) {
    let address = [
        addressObject.streetNumber,
        addressObject.streetName,
        addressObject.city,
        addressObject.region,
        addressObject.country,
        addressObject.postcode
    ]

    let addressString = ""

    for (let i = 0; i < address.length; i++) {
       if (address[i]) {
           addressString += address[i]
           if (i > 0 && i < 6) {
               addressString += ', '
           } else {
               addressString += " "
           }
       }
    }

    return addressString.replace(/,\s*$/, "");
}

/**
 * Takes a business id and the primary id of a product image and returns the link to the image.
 *
 * @param userId ID of the user
 * @param primaryId ID of the image
 * @param isThumbnail Retrieve the thumbnail version
 */
const getUserImageLink = function(userId, primaryId, isThumbnail) {
    if (!primaryId || !userId) {
        return avatar;
    }

    if (isThumbnail) {
        return `${serverURL}/users/images/${primaryId}/thumbnail`;
    } else {
        return `${serverURL}/users/images/${primaryId}`;
    }
}

export default {
    dateDiff,
    formatFullAddress,
    getUserImageLink
}
