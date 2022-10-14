<template>
  <div class="business-register-container">
    <div class="business-register-required-fields-key">
      <p>Required fields are marked with *</p>
    </div>
    <div class="business-register-title">New Business Form</div>
    <div class="business-register-form" @submit.prevent="registerBusiness">
      <div class="business-register-other-info">
        <b-field label="Name*" label-position="on-border">
          <b-input maxlength="50" type="text" v-model="name" required/>
        </b-field>

        <b-field label="Description" label-position="on-border">
          <b-input maxlength="200" type="textarea" v-model="description" placeholder="About the business..."/>
        </b-field>

        <b-field label="Business Type*" label-position="on-border">
          <b-select placeholder="Select a type" v-model="type" required>
            <option value="Accommodation">Accommodation</option>
            <option value="Food Services">Food Services</option>
            <option value="Retail Trade">Retail Trade</option>
            <option value="Charitable organisation">Charitable</option>
            <option value="Non-profit organisation">Non-profit</option>
          </b-select>
        </b-field>
      </div>

      <div class="business-register-address">
        <b-field label="Country*" label-position="on-border">
          <AutofillInputComponent maxlength="50" type="text" v-model="country" :input-list-data=countryList do-filter="true" required/>
        </b-field>

        <b-field label="Region" label-position="on-border">
          <AutofillInputComponent maxlength="50"
                                  type="text" v-model="region"
                                  :input-list-data=queryResults
                                  @input="queryAutofillRegions()"
                                  @focus="queryAutofillRegions()"
                                  do-filter="true" required/>
        </b-field>

        <div class="business-register-address-part-one">
          <b-field label="City/Town" label-position="on-border" class="register-city">
            <AutofillInputComponent maxlength="50"
                                    type="text" v-model="city"
                                    :input-list-data=queryResults
                                    @input="queryAutofillCities()"
                                    @focus="queryAutofillCities()"
                                    do-filter="true"/>
          </b-field>

          <b-field label="Postal / Zip Code" label-position="on-border" class="register-postcode">
            <b-input type="text" v-model="postcode"/>
          </b-field>
        </div>

        <div class="business-register-address-part-two">
          <b-field label="Street Number" label-position="on-border" class="register-street-number">
            <b-input type="text" v-model="streetNumber"/>
          </b-field>

          <b-field label="Street Name" label-position="on-border" class="register-street-name">
            <AutofillInputComponent maxlength="50"
                                    type="text" v-model="streetName"
                                    :input-list-data=queryResults
                                    @input="queryAutofillStreetAddress()"
                                    @focus="queryAutofillStreetAddress()"/>
          </b-field>
        </div>
      </div>
    </div>
    <div class="business-register-buttons">
      <b-button type="submit is-info" tag="router-link" :to="{name: 'profile'}">Cancel</b-button>
      <b-button type="submit is-success" @click="registerBusiness">Register New Business</b-button>
    </div>
  </div>
</template>

<script>
import AutofillInputComponent from "@/components/autofill/AutofillInputComponent";
import autofillQuery from "@/api/photon/autofillQuery";
import countryList from "@/api/photon/countries";
import error from '../errorPopup';
import api from '@/api/api';
import logger from "@/logger";
import businesses from './businesses'

const BusinessRegisterComponent = {
  name: "RegisterBusinessComponent",
  components: {AutofillInputComponent},

  data() {
    return {
      name: null,
      description: null,
      streetNumber: null,
      streetName: null,
      city: null,
      region: null,
      country: null,
      postcode: null,
      type: null,

      countryList,
      //Results of query
      queryResults: [],

      errors: []
    };
  },

  methods: {
    /**
     * Querying street address with parameters
     */
    queryAutofillStreetAddress() {
      autofillQuery.queryStreetAddress (this.country, this.region, this.city, this.streetNumber, this.streetName)
          .then(result => this.queryResults = result);
    },

    /**
     * Querying regions in a country
     */
    queryAutofillRegions() {
      return autofillQuery.queryRegions(this.country, this.region)
          .then(result => this.queryResults = result);
    },

    /**
     * Querying cities in a region in a country
     */
    queryAutofillCities() {
      return autofillQuery.queryCities(this.country, this.region, this.city)
          .then(result => this.queryResults = result);
    },

    validate() {
      if (!businesses.validateBusinessRegister(this.name, this.country, this.type)) {
        this.errors.push("One or more required fields (*) are empty!");
      }

      if (!businesses.validateBusinessType(this.type)) {
        this.errors.push("Please select a business type!");
      }
    },

    /**
     * Carries out validation and submission of business registration form
     */
    registerBusiness() {
      this.errors = [];
      this.validate();

      if (this.errors.length !== 0) {
        error.showPopup(this, this.errors);
      } else {
        let payload = {
          primaryAdministratorId: this.userId,
          name: this.name,
          description: this.description,
          address: {
            streetNumber: this.streetNumber,
            streetName: this.streetName,
            city: this.city,
            region: this.region,
            country: this.country,
            postcode: this.postcode
          },
          businessType: this.type
        };

        api.businesses
          .register(payload)
          .then(res => {
            this.$router.push({name: 'business', params: {businessId: res.data.businessId} });
          })
          .catch(err => {
            logger.getLogger().error(err);
            error.showPopupFromServer(this, err);
          });
      }
    }
  }
};

export default BusinessRegisterComponent;
</script>
