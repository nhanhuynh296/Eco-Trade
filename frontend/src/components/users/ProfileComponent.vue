<template>
  <div class="profile-container">
    <div class="profile-image">
      <div class="profile-preview" :style="{ backgroundImage: 'url(' + previewImage + ')' }">
        <div class="profile-preview-buttons">
          <b-field class="file">
            <b-button type="submit is-info is-small" v-on:click="viewProfileImages()">Click to upload</b-button>
          </b-field>
        </div>
      </div>
    </div>

    <div class="profile-member-date">
      Member since {{memberDate}} | {{memberDays}}
    </div>

    <div class="profile-form">
      <div class="profile-section-one">

        <div class="profile-main-name">
          <b-field class="profile-fname" label="First Name*" label-position="on-border">
            <b-input type="text" v-if="!isEdit" v-model="user.firstName" readonly/>
            <b-input maxlength="70" type="text" v-if="isEdit" v-model="editUser.firstName" required/>
          </b-field>
          <b-field class="profile-lname" label="Last Name*" label-position="on-border">
            <b-input type="text" v-if="!isEdit" v-model="user.lastName" readonly/>
            <b-input maxlength="70" type="text" v-if="isEdit" v-model="editUser.lastName" required/>
          </b-field>
        </div>

        <div class="profile-secondary-name">
          <b-field class="profile-mname" label="Middle Name" label-position="on-border">
            <b-input type="text" v-if="!isEdit" v-model="user.middleName" readonly/>
            <b-input maxlength="70" type="text" v-if="isEdit" v-model="editUser.middleName"/>
          </b-field>
          <b-field class="profile-nname" label="Nickname" label-position="on-border">
            <b-input type="text" v-if="!isEdit" v-model="user.nickname" readonly/>
            <b-input maxlength="70" type="text" v-if="isEdit" v-model="editUser.nickname"/>
          </b-field>
        </div>

        <b-field class="profile-dob" label="Date Of Birth*" label-position="on-border">
          <b-input v-model="displayDate" icon="calendar-today" v-if="!isEdit" class="profile-date-readonly" readonly/>
          <b-datepicker v-model="editUser.dateOfBirth" v-if="isEdit" :first-day-of-week="1" :max-date="minDate" placeholder="Click to select.." icon="calendar-today" trap-focus required/>
        </b-field>

        <b-field class="profile-bio" label="Bio" label-position="on-border">
          <b-input type="textarea" v-if="!isEdit" v-model="user.bio" placeholder="About you..." readonly/>
          <b-input maxlength="200" type="textarea" v-if="isEdit" v-model="editUser.bio" placeholder="About you..."/>
        </b-field>
      </div>

      <div class="profile-section-two">
        <b-field label="Email*" label-position="on-border">
          <b-input type="email" icon="email" v-model="user.email" v-if="!isEdit" readonly/>
          <b-input maxlength="40" type="email" icon="email" icon-right="close-circle" id="clear-email"
                   icon-right-clickable @icon-right-click="clearEmailClick"
                   placeholder="user@email.com" v-model="editUser.email" v-if="isEdit" required/>
        </b-field>

        <b-field label="Phone Number" label-position="on-border">
          <b-input type="tel" placeholder="+648 888 888 88" v-model="user.phoneNumber" v-if="!isEdit" readonly/>
          <b-input type="tel" placeholder="+648 888 888 88" v-model="editUser.phoneNumber" v-if="isEdit"/>
        </b-field>

        <b-field label="Address" label-position="on-border" v-if="!isEdit">
          <b-input type="textarea" v-if="!isEdit" v-model="displayAddress" placeholder="10 Street, Town, Country" readonly/>
        </b-field>

        <b-field label="Currency" label-position="on-border" v-if="!isEdit">
          <b-input type="text" v-if="!isEdit && currencyDisplay" v-model="currencyDisplay" placeholder="$ NZD" readonly/>
        </b-field>

        <b-field label="Country*" label-position="on-border" v-if="isEdit">
          <AutofillInputComponent maxlength="50" type="text" v-model="editCountry" :input-list-data=countryList do-filter="true" required/>
        </b-field>

        <b-field label="Region" label-position="on-border" v-if="isEdit">
          <AutofillInputComponent maxlength="50"
                                  type="text" v-model="editRegion"
                                  :input-list-data=queryResults
                                  @input="queryAutofillRegions()"
                                  @focus="queryAutofillRegions()"
                                  do-filter="true"
                                  required/>
        </b-field>

        <div class="address-part-one" v-if="isEdit">
          <b-field label="City/Town" label-position="on-border" class="register-city">
            <AutofillInputComponent maxlength="50"
                                    type="text" v-model="editCity"
                                    :input-list-data=queryResults
                                    @input="queryAutofillCities()"
                                    @focus="queryAutofillCities()"
                                    do-filter="true"
                                    required/>
          </b-field>

          <b-field label="Postal / Zip Code" label-position="on-border" class="register-postcode">
            <b-input maxlength="5" type="text" v-model="editPostcode" required/>
          </b-field>
        </div>

        <div class="address-part-two" v-if="isEdit">
          <b-field label="Street Number" label-position="on-border" class="register-street-number">
            <b-input maxlength="10" type="text" v-model="editStreetNum" required/>
          </b-field>

          <b-field label="Street Name" label-position="on-border" class="register-street-name">
            <AutofillInputComponent maxlength="50"
                                    type="text" v-model="editStreetName"
                                    :input-list-data=queryResults
                                    @input="queryAutofillStreetAddress()"
                                    @focus="queryAutofillStreetAddress()"
                                    required/>
          </b-field>
        </div>

        <div v-if="isEdit">
          Change Password
        </div>
        <br v-if="isEdit"/>
        <b-field label="Password" label-position="on-border" v-if="isEdit">
          <b-input type="password" placeholder="********" v-model="editUser.password" password-reveal required/>
        </b-field>

        <div class="profile-edit" v-if="isEdit">
          <div>
            <b-button id="cancel-button" type="submit is-info" @click="cancel">Cancel</b-button>
          </div>
          <div>
            <b-button id="save-button" type="submit is-success" v-on:click="save">Save</b-button>
          </div>
        </div>

        <b-button id="edit-button" type="is-primary" icon-left="pencil-outline" v-if="!isEdit" @click="edit" >Edit Profile</b-button>
      </div>
    </div>
  </div>
</template>

<script>
import api from '../../api/api';
import avatar from '../../../public/assets/avator.png';
import userProfile from "../../components/users/userProfile";
import logger from "../../logger";
import AutofillInputComponent from "../../components/autofill/AutofillInputComponent";
import autofillQuery from "../../api/photon/autofillQuery";
import countryList from "../../api/photon/countries";
import ProfileImagesComponent from "../../components/users/ProfileImagesComponent";
import errorPopup from "../../components/errorPopup";
import UserChangeCountryComponent from "../../components/users/UserChangeCountryComponent";

const ProfileComponent = {
  name: "profile",
  components: {AutofillInputComponent},
  props: ["isShown"],

  data() {
    return {
      user: {},
      editUser: {},
      isEdit: false,

      // Password
      password: "",

      // Email
      displayDate: null,

      // Dates
      minDate: null,
      memberDate: null,
      memberDays: null,

      // Address
      displayAddress: null,
      initialCountry: null,
      editCountry: null,
      editRegion: null,
      editCity: null,
      editPostcode: null,
      editStreetNum: null,
      editStreetName: null,
      countryForCurrency: null,

      // Currency
      currency: {
        symbol: "$",
        code: "NZD"
      },

      // Images
      images: [],
      errors: [],
      previewImage: null,

      countryList,
      // Results of query
      queryResults: []
    }
  },

  /**
   * When the page is created the minDate is set
   * and the image preview is set
   */
  created() {
    this.minDate = new Date(new Date().getFullYear() - 18, new Date().getMonth(), new Date().getDate());
    this.setPreview();
  },

  /**
   * Mounts the user details to the user variable
   * Sets the editable user details
   * Sets the readonly date
   */
  mounted() {
    this.getUserDetails();
  },

  computed: {
    currencyDisplay() {
      let c = this.currency;
      return c.symbol + ' ' + c.code;
    }
  },

  methods: {

    /**
     * Retrieves the user details by the user id
     */
    getUserDetails() {
      api.users
          .getUser(localStorage.getItem("id"))
          .then(res => {
            this.user = res.data;

            this.user.dateOfBirth = new Date(this.user.dateOfBirth);
            this.user.created = new Date(this.user.created);
            this.editUser = this.user;

            let address = this.editUser.homeAddress;
            this.editCountry = address.country === undefined ? null : address.country;
            this.editRegion = address.region === undefined ? null : address.region;
            this.editCity = address.city === undefined ? null : address.city;
            this.editPostcode = address.postcode === undefined ? null : address.postcode;
            this.editStreetNum = address.streetNumber === undefined ? null : address.streetNumber;
            this.editStreetName = address.streetName === undefined ? null : address.streetName;
            this.initialCountry = address.country;

            this.displayAddress = userProfile.formatFullAddress(this.user.homeAddress)
            this.displayDate = this.user.dateOfBirth.toLocaleDateString("en-GB");
            this.memberDate = this.user.created.toLocaleDateString("en-GB");
            this.memberDays = userProfile.dateDiff(this.user.created, new Date());

            for (let image of this.user.images) {
              this.images.push(image.id)
            }

            this.setPreview();

            this.countryForCurrency = this.user.countryForCurrency;

            api.currency.getCurrentCurrencyByCountry(this.user.countryForCurrency)
                .then(res => {
                  this.currency = res;
                })
                .catch(error => {
                  logger.getLogger().warn(error);
                });
          })
          .catch(err => {
            errorPopup.showPopupFromServer(this, err, false);
          });
      },

      /**
      * Check if user have changed country, if so then pop up confirm, if not the call save() immediately
      */
      openSaveConfirm() {
        this.$buefy.modal.open({
          parent: this,
          component: UserChangeCountryComponent,
          props: {currencyData: this.currency},
          hasModalCard: true,
          customClass: 'custom-class custom-class-2',
          trapFocus: true,
          events: {
            'confirm': () => {
              api.users.updateCountryForCurrency(localStorage.getItem('id'), { country: this.editCountry })
              this.getUserDetails();
            },
            'cancel': () => {
              this.getUserDetails();
            },
            'close': () => {
              this.getUserDetails();
            }
          }
        });
      },

    /**
     * Sets variable isEdit to true
     */
    edit() {
      this.isEdit = true;
    },

    /**
     * Sets variable isEdit to false
     */
    cancel() {
      this.getUserDetails()
      this.isEdit = false;
    },

    /**
     *  Set the edit user field values to the modified entries
     */
    setFields() {
      this.editUser.countryForCurrency = this.editCountry;
      if (this.editCountry != null) {
        this.editUser.homeAddress.country = this.editCountry;
      }
      if (this.editRegion != null) {
        this.editUser.homeAddress.region = this.editRegion;
      }
      if (this.editCity != null) {
        this.editUser.homeAddress.city = this.editCity;
      }
      if (this.editPostcode != null) {
        this.editUser.homeAddress.postcode = this.editPostcode;
      }
      if (this.editStreetNum != null) {
        this.editUser.homeAddress.streetNumber = this.editStreetNum;
      }
      if (this.editStreetName != null) {
        this.editUser.homeAddress.streetName = this.editStreetName;
      }
    },

    /**
     *  Set the users currency based on the given country
     */
    setCurrency(currency) {
      if (currency) {
        this.currency = currency;
      } else {
        api.currency.queryCountryCurrency("New Zealand").then(res => {
          this.currency = res;
        });
        errorPopup.showPopup(this, ["Invalid country, your currency will be set to $NZD."]);
      }
    },

    updateUserDetails() {
      api.users.updateUser(localStorage.getItem("id"), this.editUser)
          .then(() => {
            api.users.updateCountryForCurrency(localStorage.getItem('id'), { country: this.editCountry })
            if (this.initialCountry !== this.editCountry) {
              this.openSaveConfirm();
            }
            this.getUserDetails()
            this.isEdit = false
          })
          .catch(err => {
            errorPopup.showPopupFromServer(this, err, false);
          });
    },

    /**
     * Sets variable isEdit to false
     *
     * Updates all the fields that were modified
     */
    save() {
      api.currency.queryCountryCurrency(this.editCountry)
        .then(res => {
          this.setFields();
          this.setCurrency(res);
          this.updateUserDetails();
        })
        .catch(err => {
          errorPopup.showPopupFromServer(this, err, false);
        });
    },

    /**
     * Sets the background image to default preview image
     */
    setPreview() {
      let image = this.getImageThumbnailLink(this.user.primaryImageId);
      if (image === "https://bulma.io/images/placeholders/128x128.png") {
        image = avatar;
      }
      this.previewImage = image
    },

    /**
     * Takes the primary id of a profile image and returns a link to an existing image.
     * If there is no image (primary id is null) a default image is sent.
     */
    getImageThumbnailLink(primaryId) {
      return userProfile.getUserImageLink(this.user.id, primaryId, true)
    },

    /**
     * Clears the email field
     */
    clearEmailClick() {
      this.editUser.email = '';
    },

    /**
     * Querying street address with parameters
     */
    queryAutofillStreetAddress() {
      autofillQuery.queryStreetAddress (this.editCountry, this.editRegion, this.editCity, this.editStreetNum, this.editStreetName)
          .then(result => this.queryResults = result);
    },

    /**
     * Querying regions in a country
     */
    queryAutofillRegions() {
      return autofillQuery.queryRegions(this.editCountry, this.editRegion)
          .then(result => this.queryResults = result);
    },

    /**
     * Querying cities in a region in a country
     */
    queryAutofillCities() {
      return autofillQuery.queryCities(this.editCountry, this.editRegion, this.editCity)
          .then(result => this.queryResults = result);
    },

    /**
     * Initializes the images carousel for profile
     */
    viewProfileImages() {
      this.$buefy.modal.open({
        parent: this,
        component: ProfileImagesComponent,
        props: {
          data: {images: this.images, primaryImageId: this.user.primaryImageId},
          editable: true,
          user: this.user
        },
        trapFocus: true,
        events: {
          'updatePrimaryImage': (primaryImageId) => {
            this.user.primaryImageId = primaryImageId;
            this.setPreview();
          },

          'addImage': (imageId) => {
            this.images.push(imageId);

            // If first image being uploaded
            if (this.images.length === 1) {
              this.user.primaryImageId = imageId;
              this.setPreview();
            }
          }
        }
      });
    }
  },
};

export default ProfileComponent;
</script>
