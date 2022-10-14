/**
 * errorPopup.js
 *
 * File for handling error popups
 */

/**
 * Displays the given string to the  current document, can be closed but
 * will also time-out if left for a few seconds.
 *
 * @param doc Current document object, usually `this`
 * @param errors A string containing a single error
 * */
function snackBarPopup(doc, err) {
    doc.$buefy.snackbar.open({
        message: err,
        type: 'is-danger', // Red action text
        position: 'is-bottom-right',
        actionText: 'X',
        duration: 10000, // Milliseconds
        indefinite: false,
        queue: false // Lets multiple pop-ups appear
    })
}


/**
 * Loops through the list of errors created on the client side to produce popups.
 *
 * @param doc Current document object, usually `this`
 * @param errors List of errors to show
 */
function showPopup(doc, errors) {
    if (errors.length !== 0) {
        for (let err of errors) {
            snackBarPopup(doc, err);
        }
    }
}


/**
 * Splits the error string given by the server and creates one or more snackbar pop-ups if at least one is given.
 *
 * @param doc Current document object, usually `this`
 * @param errors A string of one or more errors returned by the server
 * @param isData Boolean set to true if the string is in the error.response.data, .data.message otherwise.
 */
function showPopupFromServer(doc, errors, isData) {
    try {
        let errorsList;

        if (isData) {
            // Splits the error in the .data of response
            errorsList = errors.response.data.split("\n");
        } else {
            // Splits the error in the .message of response.data
            errorsList = errors.response.data.message.split("\n");
        }

        if (errorsList.length > 0) {
            for (let err of errorsList) {
                if (err !== "") {
                    snackBarPopup(doc, err);
                }
            }
        }
    // If the returned error has no response.data it's likely a 500 server error or a timeout (because server not running).
    } catch (e) {
        snackBarPopup(doc, "A server error has occurred, please try again later.");
    }
}


export default {
    showPopup,
    showPopupFromServer
}
