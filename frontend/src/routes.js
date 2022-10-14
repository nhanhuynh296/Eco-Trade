/**
 * routes.js
 *
 * Javascript file for handling routing of frontend application
 */

import VueRouter from 'vue-router';
import api from '@/api/api';
import Main from "@/views/Main";
import Home from "@/views/Home";
import Login from "@/views/Login";
import Profile from "@/views/Profile";
import Register from "@/views/Register";
import UserProfile from "@/views/UserProfile";
import BusinessHome from "@/views/BusinessHome";
import NotFound from "@/views/404";
import RegisterBusiness from "@/views/RegisterBusiness";
import Business from "@/views/Business";
import logger from "@/logger";
import UserSearch from "@/views/UserSearch";
import userSearchService from "@/components/users/search/userSearch"
import BusinessListings from "./views/BusinessListings";
import BusinessSearch from "@/views/BusinessSearch";
import Marketplace from "@/views/Marketplace";
import ListingsSearch from "@/views/ListingsSearch";
import SalesReport from "@/views/SalesReport";
import SalesHistory from "@/views/SalesHistory";

/**
 * List of potential routes to navigate to:
 * Helps to avoid page refreshing.
 */
const viewRoutes = [
    // Special endpoints to not show navbar on
    { name: "login", path: '/login', component: Login, default: true },
    { name: "register", path: '/register', component: Register },

    // Base end points (Show navbar on these)
    { path: '/', component: Main,
        children: [
            { name: "home", path: '/', component: Home, meta: { requiresAuth: true, businessAccessible: true, globalAdminAccessible: true } },
            { name: "marketplace", path: '/marketplace', component: Marketplace, meta: { requiresAuth: true, businessAccessible: false, globalAdminAccessible: true } },
            { name: "profile", path: '/profile', component: Profile, meta: { requiresAuth: true, businessAccessible: true, globalAdminAccessible: true } },

            // Users end points
            { name: "userSearch", path: '/users', component: UserSearch, meta: { requiresAuth: true, businessAccessible: false, globalAdminAccessible: true } },
            { name: "userProfile", path: '/users/:userId', component: UserProfile, meta: { requiresAuth: true, businessAccessible: true, globalAdminAccessible: true } },

            // Business end points
            { name: "businessSearch", path: '/businesses', component: BusinessSearch, meta: { requiresAuth: true, businessAccessible: false, globalAdminAccessible: true } },
            { name: "registerBusiness", path: '/businesses/register', component: RegisterBusiness, meta: { requiresAuth: true, businessAccessible: false, globalAdminAccessible: true } },
            { name: "business", path: '/businesses/:businessId', component: Business, meta: { requiresAuth: true, businessAccessible: true, globalAdminAccessible: true } },
            { name: "businessHome", path: '/businesses/:businessId/home', component: BusinessHome,
                    meta: { requiresAuth: true, businessAccessible: true, userAccessible: false, adminAccessible: true, globalAdminAccessible: true } },
            { name: "businessListings", path: '/businesses/:businessId/listings', component: BusinessListings, meta: { requiresAuth: true, businessAccessible: true, globalAdminAccessible: true}},
            { name: "businessSalesHistory", path: '/businesses/:businessId/sales-history', component: SalesHistory,
                meta: { requiresAuth: true, businessAccessible: true, globalAdminAccessible: true, userAccessible: false}},

            { name: "salesReport", path: '/businesses/:businessId/sales-report', component: SalesReport,
                meta: { requiresAuth: true, businessAccessible: true, userAccessible: false, adminAccessible: true, globalAdminAccessible: true }},
            // Listings end points
            { name: "listingsSearch", path: '/listings', component: ListingsSearch, meta: { requiresAuth: true, businessAccessible: false, globalAdminAccessible: true }}
        ]
    },

    // You can't redirect to `*` path, so this notFound page needs to here
    { name: "notFound", path: '/404', component: NotFound},
    // Ensure this is the last route, essentially if route doesn't exist, redirect to 404 page
    { path: "*", redirect: { name: 'notFound' } },
];

/**
 * Check if we're allowed to go to to this specific route
 *
 * @param metaTags Route tags
 *
 * @return {boolean} Whether or not we can go to this specific route
 */
export const isAllowedToRoute = function (metaTags) {
    let isValid = true;

    //Check if they're acting as business, and page disallows businesses to view, or check if global admin and not allowed to view
    if (!metaTags.businessAccessible && localStorage.getItem("user-type") === "business") {
        logger.getLogger().info("Page is inaccessible to businesses");
        isValid = false;
    } else if (!metaTags.globalAdminAccessible && userSearchService.isAdminRole(localStorage.getItem("role"), true)) {
        logger.getLogger().info("Page is inaccessible to global admins");
        isValid = false;
    } else if (metaTags.userAccessible === false && localStorage.getItem("user-type") === "user" && !userSearchService.isAdminRole(localStorage.getItem("role"), false)) {
        logger.getLogger().info("Page is inaccessible to users");
        isValid = false;
    }

    return isValid;
}

//VueRouter object to handle routing
const router = new VueRouter({
    // mode: 'history',
    routes: viewRoutes
});

const originalPush = router.push;
router.push = function push(location) {
    return originalPush.call(this, location).catch(err => {
        if (err.name !== "NavigationDuplicated") {
            throw err;
        }
    });
}

/**
 * Before each route, check if the route requires authentication, if it does
 * it will check if the user is logged in, if that fails, redirect to /login
 */
router.beforeEach((to, from, next) => {
    if (to.name === "login" || to.name === "register") {
        next();
    } else {
        // First check if the page is not allowed to be accessed
        if (!isAllowedToRoute(to.meta)) {
            next({ name: "home" });
        } else {
            if (to.meta.requiresAuth) {
                // A get request is sent to the server to check if the user's JSESSIONID is legitimate
                api.loginRegister
                    .isAuthorised()
                    .then(() => {
                        //check if user searched for themselves
                        if (to.name === "userProfile") {
                            if (parseInt(to.params.userId) === parseInt(localStorage.id)) {
                                next({
                                    name: 'profile',
                                    query: {redirect: to.fullPath}
                                });
                            }
                        }
                        next();
                    })
                    .catch(err => {
                        logger.getLogger().info("Must be logged in", err);
                        next({
                            name: 'login',
                            query: {redirect: to.fullPath}
                        });
                    });
            } else {
                next();
            }
        }
    }

});

export default router;
