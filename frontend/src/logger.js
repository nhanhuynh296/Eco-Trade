/**
 * logger.js
 *
 * File to handle all logging input and output of frontend
 *
 * Usage: Inject the vue.js $log object
 * Call getLogger() with the following options...
 *
 * .debug("Debug!");
 * .info("Info!");
 * .warn("Warn!");
 * .error("Error!");
 * .fatal("Fatal!");
 */

const isProduction = process.env.VUE_APP_DEV_MODE !== "true"

//Log object, should be dependency injected from the `inject` function
let log = null;

/**
 * Dependency inject the vue instance to access from anywhere, not just
 * vue components
 *
 * @param logInstance
 */
const inject = function (logInstance) {
    log = logInstance;
}

/**
 * Getter for logger object
 *
 * @return vue-logger object
 */
const getLogger = function () {
    return log;
}

/**
 * Options for the logger, log all messages if not in production mode
 */
const options = {
    isEnabled: true,
    logLevel : isProduction ? "error" : "debug",
    stringifyArguments : false,
    showLogLevel : true,
    showMethodName : !isProduction, //Don't expose method name if in production
    separator: '|',
    showConsoleColors: true
};

export default { inject, options, getLogger};
