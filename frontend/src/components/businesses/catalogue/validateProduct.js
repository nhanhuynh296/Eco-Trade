/**
 * Checks that all required product fields are not empty
 */
const validateRequired = function(name, recommendedRetailPrice, dropFiles) {
    let valid = true;
    let errors = [];

    if (name === null || name.length < 1) {valid = false;}

    if (!valid) {errors.push("Required field name is empty!");}

    if (typeof recommendedRetailPrice !== "number" || recommendedRetailPrice <= 0 ) {errors.push("RRP cannot be 0!");}

    if (dropFiles) {
        if (dropFiles.length > 10) {

            errors.push('You may upload at most 10 files!');
        }
        for (let i = 0; i < dropFiles.length; i++) {
            // 1048576 is the max size accepted by backend
            if (dropFiles[i].size > 1048576) {
                errors.push(`The file '${dropFiles[i].name}' is greater than 1mb!`)
            }
        }
    }

    return errors
}

export default {
    validateRequired
}
