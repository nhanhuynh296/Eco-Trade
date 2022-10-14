/**
 * api.js
 *
 * exports other api files
 */
import loginRegister from "../api/users/loginRegister";
import users from "../api/users/users";
import businesses from "../api/businesses/businesses";
import admin from "../api/admin/admin";
import currencyQuery from "../api/restcountries/currencyQuery";
import inventory from "./inventory/inventory"
import listing from "./listing/listing";
import cards from "../api/ucm/cards";
import productImages from "./productImages/productImages";
import notifications from "../api/notifications/notifications"
import keywords from "../api/keywords/keywords";
import sales from "../api/sales/sales";

export default {
  loginRegister: loginRegister,
  users: users,
  businesses: businesses,
  admin: admin,
  currency: currencyQuery,
  inventory: inventory,
  listing: listing,
  cards: cards,
  productImages: productImages,
  notifications: notifications,
  keywords: keywords,
  sales: sales,
}
