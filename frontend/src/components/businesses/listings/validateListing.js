/**
 * Checks that all required listing fields are valid.
 * This means the price and quantity are greater than 0
 * and the closing date is in the future.
 */
const validateRequired = function(quantity, price, closingDate) {
    let errors = [];

    // Checks the quantity is greater than zero
    if (quantity === null || quantity <= 0) {
        errors.push("Quantity must be a positive whole number!");
    }

    // Checks the price is greater than zero
    if (price === null || price <= 0) {
        errors.push("Price must be a positive number!");
    }

    let today = new Date()
    let minClosingDate = new Date(today.getFullYear() , today.getMonth(), today.getDate() + 1);
    // Checks the closing date is not in the past
    if (closingDate === null || closingDate < minClosingDate) {
        errors.push("Closing date must be in the future!")
    }

    return errors
}

export default {
    validateRequired
}
