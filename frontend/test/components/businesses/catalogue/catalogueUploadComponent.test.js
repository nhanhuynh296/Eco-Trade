/**
 * catalogueUploadComponent.test.js
 *
 * Test catalogue vue component and functions
 */
import VueRouter from 'vue-router';
import VueLogger from 'vuejs-logger';
import logger from "../../../../src/logger";
import Buefy from "buefy";
import {createLocalVue, mount} from "@vue/test-utils";
import errorPopup from "../../../../src/components/errorPopup";
import CatalogueUploadComponent from "../../../../src/components/businesses/catalogue/CatalogueUploadComponent";

const localVue = createLocalVue();
localVue.use(VueRouter)
localVue.use(Buefy);

localVue.use(VueLogger, logger.options);
logger.inject(localVue.$log);

const router = new VueRouter();

errorPopup.showPopup = jest.fn();


let wrapper = mount(CatalogueUploadComponent, {
    localVue,
    router,
    stubs: ['router-link'],
});

wrapper.vm.$parent.updateDropFiles = jest.fn();

test('Large files are filtered and error is shown', async () => {
     // One more than max allowable size
    wrapper.vm.dropFiles = [{size: 1048577, name: 'file1'}];
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.dropFiles).toHaveLength(0);
    expect(errorPopup.showPopup).toBeCalledWith(wrapper.vm, [`The file 'file1' is greater than 1mb!`]);
    expect(wrapper.vm.$parent.updateDropFiles).toHaveBeenCalledWith([]);
});

test('Too many files are filtered and error is shown', async () => {
    // One more than max allowable size
    let files = [];
    for (let i = 0; i < 11; i++) {
        files.push({size: 1, name: 'file1'})
    }
    wrapper.vm.dropFiles = files;

    await wrapper.vm.$nextTick();
    expect(wrapper.vm.dropFiles).toHaveLength(10);
    expect(errorPopup.showPopup).toBeCalledWith(wrapper.vm, ['You may upload at most 10 files!']);
    expect(wrapper.vm.$parent.updateDropFiles).toHaveBeenCalledWith(files.splice(0,10));
});

test('Deleting a file removes it from the dropFiles', async () => {
    wrapper.vm.dropFiles = [{size: 1048577, name: 'file1'}];
    expect(wrapper.vm.dropFiles).toHaveLength(1);
    wrapper.vm.deleteDropFile(0);
    expect(wrapper.vm.dropFiles).toHaveLength(0);
});


