/**
 * inventoryHelper.js
 *
 * Provides helper functions for adding and editting inventory items
 */

/**
 * Convert a list of item objects to string representation
 * @param itemObjects List of item objects with at least name and ID attributes
 * @return List of strings
 */
const convertItemsToStrings = function (itemObjects) {
    const strings = [];
    if (itemObjects) {
        for (let item of itemObjects) {
            if (item && item.name && item.id) {
                strings.push(`${item.name} (ID: ${item.id})`);
            }
        }
    }

    return strings;
};

/**
 * Inverse of converting items to strings, extracts the ID from the string
 * @param string Item string
 * @param itemObjects Item objects
 */
const convertStringsToIds = function (string, itemObjects) {
    let id = null;

    if (string && itemObjects) {
        for (let item of itemObjects) {
            //Cut off the (ID: <num>) part
            if (item.name === string.slice(0, item.name.length)) {
                return Number(item.id);
            }
        }
    }

    return id;
}

/**
 * Extracts recommended retail price from string
 * @param string Item string
 * @param itemObjects Item Objects
 */
const convertStringToRPP = function (string, itemObjects) {
    let recommendedRetailPrice = null;
    if (string && itemObjects) {
        for (let item of itemObjects) {
            //Cut off the (ID: <num>) part
            if (item.name === string.slice(0, item.name.length)) {
                if (item.recommendedRetailPrice < 0) {
                    return null;
                }
                return item.recommendedRetailPrice;
            }
        }

    }
    return recommendedRetailPrice;
}

/**
 *
 * @param productId
 * @param quantity
 * @param pricePerItem
 * @param totalPrice
 * @param manufactured
 * @param sellBy
 * @param bestBefore
 * @param expires
 * @returns {[]}
 */
const validateFields = function (productId, quantity, pricePerItem, totalPrice, manufactured, sellBy, bestBefore,
                                 expires) {
    let errors = []

    if (!productId || !quantity || !expires) {
        errors.push("Missing required fields!");
    } else {
        let today = new Date()
        let minExpire = new Date(today.getFullYear() , today.getMonth(), today.getDate() + 1);
        if (expires < minExpire) {
            errors.push("Expire date must be in the future")
        }
    }

    if (quantity && quantity <= 0) {
        errors.push("Please specify a positive number for quantity.")
    }

    if (pricePerItem && pricePerItem <  0) {
        errors.push("Please specify a positive number for the price per item.")
    }

    if (totalPrice && totalPrice < 0) {
        errors.push("Please specify a positive number for the total price.")
    }

    if (manufactured) {
        if (manufactured > expires) {
            errors.push("Manufactured date must be before expired date.")
        }

        if (sellBy) {
            if (manufactured > sellBy) {
                errors.push("Manufactured date must be before sell by date.")
            }
        }

        if (bestBefore) {
            if (manufactured > bestBefore) {
                errors.push("Manufactured date must be before best before date.")
            }
        }
    }

    if (sellBy && sellBy >= expires) {
        errors.push("Sell by date must be before expired date.")
    }

    if (bestBefore && bestBefore >= expires) {
        errors.push("Best before date must be before expired date.")
    }
    return errors
}

export default {
    convertItemsToStrings,
    convertStringsToIds,
    convertStringToRPP,
    validateFields
}
