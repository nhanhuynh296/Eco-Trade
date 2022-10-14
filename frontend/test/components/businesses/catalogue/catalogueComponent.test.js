/**
 * catalogueComponent.test.js
 *
 * Test catalogue vue component and functions
 */
import VueRouter from 'vue-router';
import VueLogger from 'vuejs-logger';
import logger from "../../../../src/logger";
import Buefy from "buefy";
import {createLocalVue, mount} from "@vue/test-utils";
import api from "../../../../src/api/api";
import CatalogueComponent from "../../../../src/components/businesses/catalogue/CatalogueComponent";
import catalogueSearch from "../../../../src/components/businesses/catalogue/catalogueSearch";
import businesses from "../../../../src/components/businesses/businesses";


const localVue = createLocalVue();
localVue.use(VueRouter)
localVue.use(Buefy);

localVue.use(VueLogger, logger.options);
logger.inject(localVue.$log);

const router = new VueRouter();

catalogueSearch.catalogueSearch = jest.fn().mockImplementation(() => { return "OK!"; });
businesses.getProductImageLink = jest.fn().mockImplementation(() => { return "OK!"; });
businesses.maxDisplay = jest.fn().mockImplementation(() => { return 'test';});

let mockProducts = {
    data: {
        paginationElements: [
            {id:1,businessId:1,name:"Watties Baked Beans - 420g can",description:"Baked Beans as they should be.",manufacturer:"Heinz Wattie's Limited",recommendedRetailPrice:2.2,primaryImageId:null,created:"2020-07-14",images:[{id: 1}]},
            {id:2,businessId:1,name:"Indomie Instant Noodles",description:"Spicy BBQ noodles",manufacturer:"IndoFood",recommendedRetailPrice:3.5,primaryImageId:null,created:"2020-07-14",images:[]}
            ],
        totalElements: 1,
        totalPages: 1,
    }
}

let currencyResult = {
    0: {
        name: 'New Zealand',
        currencies: [{ code: 'NZD', symbol: '$'}],
    },
    response: { status: 404 }
};

api.businesses.getProducts = jest.fn().mockImplementation(async () => { return mockProducts; });
api.currency.getCurrentBusinessCurrency = jest.fn().mockImplementation(async () => { return currencyResult; });

let wrapper = mount(CatalogueComponent, {
    localVue,
    router,
    stubs: ['router-link'],
    data() {
        return {
            catalogueData: [],
            checkedRows: [],
            page: 1,
            perPage: 10,
            paginationInfo: {
                totalPages: 0,
                totalElements: 0
            },
            searchQuery: '',
            businessId: 1,
            // Business currency, default to New Zealand if can't find currency
            businessCurrency: {},
            primarySrc: "",
            lowerCount: 0,
            upperCount: 0,
            sortBy: "NAME_DESC",
        }
    },
    propsData: {},
});

const openCreateButton = wrapper.find('#openCreateProductBtn');


test('Test products are retrieved on initialisation', () => {
    expect(api.businesses.getProducts).toHaveBeenCalledWith(
        wrapper.vm.$data.businessId, wrapper.vm.$data.page, wrapper.vm.$data.perPage, wrapper.vm.$data.sortBy);
    expect(wrapper.vm.$data.paginationInfo.totalPages).toEqual(mockProducts.data.totalPages);
    expect(wrapper.vm.$data.paginationInfo.totalElements).toEqual(mockProducts.data.totalElements);
    expect(wrapper.vm.$data.catalogueData).toEqual(mockProducts.data.paginationElements);
});

test('Page is changed when onPageChange is invoked', async () => {
    await wrapper.vm.onPageChange(2);
    expect(wrapper.vm.$data.page).toEqual(2);
    expect(api.businesses.getProducts).toHaveBeenCalledWith(
        wrapper.vm.$data.businessId, 2, wrapper.vm.$data.perPage, wrapper.vm.$data.sortBy);
});

test('Modal opens when button is clicked', async () => {
    wrapper.vm.$buefy.modal.open = jest.fn().mockImplementation();
    await openCreateButton.trigger('click');
    expect(wrapper.vm.$buefy.modal.open).toBeCalled();
});

test('Edit product dialog is opened when table is clicked', async () => {
    wrapper.vm.$buefy.modal.open = jest.fn().mockImplementation(async () => {});
    await wrapper.vm.editProductCardModal();
    expect(wrapper.vm.$buefy.modal.open).toHaveBeenCalled();
});

test('Get products API is called on refresh', async () => {
    await wrapper.vm.refreshTable();
    expect(api.businesses.getProducts).toHaveBeenCalledWith(
        wrapper.vm.$data.businessId, wrapper.vm.$data.page, wrapper.vm.$data.perPage, wrapper.vm.$data.sortBy);
});

test('Updating primary image id for a given product', () => {
   wrapper.vm.$data.catalogueData = [{id: 1, primaryImageId: 2, recommendedRetailPrice: 1}]
   wrapper.vm.updatePrimaryImage(1, 1);
   expect(wrapper.vm.$data.catalogueData.find(x => x.id === 1).primaryImageId).toBe(1);
});

test('Image is removed from a product in the catalogue list', () => {
   wrapper.vm.$data.catalogueData = [{id: 1, primaryImageId: 1, images: [{id: 1}], recommendedRetailPrice: 1}]
   wrapper.vm.removeImage(1, 1);
   expect(wrapper.vm.$data.catalogueData.find(x => x.id === 1).primaryImageId).toBe(null);
   expect(wrapper.vm.$data.catalogueData.find(x => x.id === 1).images).toHaveLength(0);
});

test('Product image link is fetched when function is called', async () => {
    await wrapper.vm.getProductThumbnailLink(1);
    expect(businesses.getProductImageLink).toBeCalledWith(wrapper.vm.$data.businessId, 1, true);
});

test('Default image is sent on error', () => {
   let mockEvent = {target: {src: ''}};
   wrapper.vm.getAltImage(mockEvent);
   expect(mockEvent.target.src).toBe("https://bulma.io/images/placeholders/128x128.png");
});
