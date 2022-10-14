/**
 * Checks the given parameters for faults.
 *
 * @param section String section of created card if not editing
 * @param title String title of the card
 * @param editing Boolean an indicator for if a card is being created or edited
 *
 * @return [a list of errors]
 */
const validateCardDetails = function (section, title, editing) {
    let errors = [];
    const availableSections = ['ForSale', 'Wanted', 'Exchange']
    if ((section == null || !availableSections.includes(section)) && !editing) {
        errors.push('Please choose one of the available sections!');
    }
    if (title === null || title.length < 1) {
        errors.push('Please provide a title!');
    }

    return errors;
}

export default {
    validateCardDetails
}
