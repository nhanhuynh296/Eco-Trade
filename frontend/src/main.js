import Vue from 'vue';
import App from './App';
import Buefy from 'buefy';

import VueLogger from 'vuejs-logger';
import VueRouter from "vue-router";

import '../public/styles/style.css';
import '@mdi/font/css/materialdesignicons.min.css'

import router from '@/routes'
import logger from "@/logger";
import titleMixin from "@/components/titleMixin";

/**
 * Main entry point for your Vue app
 */

Vue.config.productionTip = false;

Vue.use(VueRouter);

Vue.use(VueLogger, logger.options);
logger.inject(Vue.$log); //Dependency inject the logger for use globally

Vue.use(Buefy);
Vue.mixin(titleMixin);


new Vue({
    el: '#app',
    router,
    render: h => h(App)
});