<template>
  <div class="register-container" @submit.prevent="register">
    <div class="tile is-ancestor register-ancestor">
      <div class="tile is-5">
        <div class="register-section-one">
          <div class="required-fields-key">
            <p>Required fields are marked with *</p>
          </div>

          <div class="register-section-name-image">
            <b-field class="preview-field">
              <div class="profile-image-preview">
                <div class="file-upload">
                  <b-field class="file" accept="image/*">
                    <b-upload type="is-link" v-model="image" accept=".jpg, .png, .jpeg">
                      <a class="button is-primary">
                        <b-icon icon="upload"/>
                        <span>Upload</span>
                      </a>
                    </b-upload>
                    <span class="file-name" v-if="image">{{ image.name }}</span>
                  </b-field>
                </div>
              </div>
            </b-field>

            <div class="register-section-name">
              <b-field label="First Name* " label-position="on-border">
                <b-input maxlength="70" type="text" v-model="fname" required/>
              </b-field>
              <b-field label="Middle Name" label-position="on-border">
                <b-input maxlength="70" type="text" v-model="mname"/>
              </b-field>
              <b-field label="Last Name*" label-position="on-border">
                <b-input maxlength="70" type="text" v-model="lname" required/>
              </b-field>
              <b-field label="Nickname" label-position="on-border">
                <b-input maxlength="70" type="text" v-model="nname"/>
              </b-field>
            </div>
          </div>

          <b-field label="Bio" label-position="on-border" class="register-bio">
            <b-input maxlength="200" type="textarea" v-model="bio" placeholder="About you..." class="register-bio-input"/>
          </b-field>
        </div>

      </div>
      <div class="tile">
        <div class="register-section-two">
          <b-field label="Email*" label-position="on-border">
            <b-input maxlength="40"
                     type="email" icon="email" icon-right="close-circle"
                     icon-right-clickable @icon-right-click="clearEmailClick"
                     placeholder="user@email.com" v-model="email" required/>
          </b-field>
          <b-field label="Confirm Email*" label-position="on-border">
            <b-input maxlength="40"
                     type="email" icon="email" icon-right="close-circle"
                     icon-right-clickable @icon-right-click="clearConEmailClick"
                     placeholder="user@email.com" v-model="conEmail" required/>
          </b-field>

          <div class="is-flex is-flex-direction-row is-justify-content-space-between">
            <b-field label="Date Of Birth*" label-position="on-border" class="register-dob">
              <b-datepicker v-model="dob" :first-day-of-week="1" :max-date="minDate" placeholder="Click to select..."
                            icon="calendar-today" trap-focus required/>
            </b-field>
            <b-field label="Phone Number" label-position="on-border" class="register-phone-number">
              <b-input type="tel" placeholder="e.g. 0275055050" v-model="phone"
                       validation-message="Input must be a valid phone number"/>
            </b-field>
          </div>

          <b-field label="Country*" label-position="on-border">
            <AutofillInputComponent maxlength="50"
                                    type="text" v-model="country"
                                    :input-list-data=countryList
                                    do-filter="true"
                                    required/>
          </b-field>

          <b-field label="Region" label-position="on-border">
            <AutofillInputComponent maxlength="50"
                                    type="text" v-model="region"
                                    :input-list-data=queryResults
                                    @input="queryAutofillRegions()"
                                    @focus="queryAutofillRegions()"
                                    do-filter="true"
                                    />
          </b-field>

          <div class="address-part-one">
            <b-field label="City/Town" label-position="on-border" class="register-city">
              <AutofillInputComponent type="text" v-model="city"
                                      :input-list-data=queryResults
                                      @input="queryAutofillCities()"
                                      @focus="queryAutofillCities()"
                                      do-filter="true"
                                      />
            </b-field>

            <b-field label="Postal / Zip Code" label-position="on-border" class="register-postcode">
              <b-input type="text" v-model="postcode"/>
            </b-field>
          </div>

          <div class="address-part-two">
            <b-field label="Street Number" label-position="on-border" class="register-street-number">
              <b-input type="text" v-model="streetNumber" />
            </b-field>

            <b-field label="Street Name" label-position="on-border" class="register-street-name">
              <AutofillInputComponent maxlength="50"
                                      type="text" v-model="streetName"
                                      :input-list-data=queryResults
                                      @input="queryAutofillStreetAddress()"
                                      @focus="queryAutofillStreetAddress()"
                                      />
            </b-field>
          </div>

          <b-field label="Password*" label-position="on-border" message="Password must be atleast 8 characters long">
            <b-input type="password" placeholder="********" v-model="password" password-reveal required/>
          </b-field>
          <b-field label="Confirm Password*" label-position="on-border">
            <b-input type="password" placeholder="********" v-model="conPassword" password-reveal required/>
          </b-field>

          <div class="register-buttons">
            <div class="register-button-signup">
              <b-button type="submit is-success" @click="register">Sign Up</b-button>
            </div>
            <div class="register-button-login">
              <b-button type="submit is-success" tag="router-link" :to="{ name: 'login' }">Go to Log In</b-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from '@/api/api';
import avator from '../../../public/assets/avator.png';
import AutofillInputComponent from "@/components/autofill/AutofillInputComponent";
import autofillQuery from "@/api/photon/autofillQuery";
import countryList from "@/api/photon/countries";
import error from '../errorPopup';
import logger from "@/logger";

const RegisterComponent = {
  name: "register",
  components: {AutofillInputComponent},

  data() {
    return {
      image: null,
      imageURL: null,
      fname: null,
      mname: null,
      lname: null,
      nname: null,
      bio: null,
      email: "",
      conEmail: "",
      dob: null,
      phone: null,
      streetNumber: null,
      streetName: null,
      city: null,
      region: null,
      country: null,
      postcode: null,
      password: "",
      conPassword: "",
      minDate: null,
      errors: [],

      countryList,
      //Results of query
      queryResults: []
    };
  },

  /**
   * When the page is created the minDate is set
   * and the image preview is set to default
   */
  created() {
    this.minDate = new Date(new Date().getFullYear() - 18, new Date().getMonth(), new Date().getDate());

    fetch(document.querySelector(".profile-image-preview")).then(() => {
      this.setPreview(avator);
    });
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

    /**
     * Routes to the profile page
     */
    home() {
      let payload = {
        email: this.email.toLocaleLowerCase(),
        password: this.password
      }

      api.loginRegister
          .login(payload)
          .then(res => {
            const userId = res.data.userId;
            this.uploadImage(userId);

            localStorage.setItem("id", userId);
            localStorage.setItem("role", "ROLE_USER");
            localStorage.setItem("business-id", -1);
            localStorage.setItem("user-type", "user");

            this.$router.push({ name: 'home' });
          })
          .catch(error => {
            logger.getLogger().warn(error);
          });
    },

    //Validation methods
    /**
     * Checks to see if the user input email matches the confirmation email
     */
    validateEmail() {
      if (this.email.toLowerCase() !== this.conEmail.toLowerCase()){
        this.errors.push("Emails do not match!")
      }
    },

    /**
     * Runs through password validation checks
     * password length must be atleast 8 characters
     * password must match confirmation password
     */
    validatePassword() {
      if (this.password !== this.conPassword){
        this.errors.push("Passwords do not match!")
      }
      if (this.password.length < 8) {
        this.errors.push("Password too short!")
      }
    },

    /**
     * Checks that all required registration fields are not empty
     */
    validateRequired() {
      let valid = true;

      if (this.fname === null || this.fname.trim() === "") {valid = false;}
      if (this.lname === null || this.lname.trim() === "") {valid = false;}
      if (this.email.length === 0) {valid = false;}
      if (this.conEmail.length === 0) {valid = false;}
      if (this.country === null) {valid = false;}
      if (this.dob === null) {valid = false;}
      if (this.address === null) {valid = false;}
      if (this.password.length === 0) {valid = false;}
      if (this.conPassword.length === 0) {valid = false;}
      if (!valid) {this.errors.push("One or more required fields (*) are empty!")}
    },

    /**
     * Checks that phone number is valid if it is input
     */
    validatePhone() {
      if (this.phone) {
        const reg = new RegExp('\\+?[0-9]{3,15}');
        if(!reg.test(this.phone)) {
          this.errors.push("Invalid Phone Number!")
        }
      }
    },

    /**
     * Carries out validation and submission of registration form
     */
    register() {
      this.errors = [];
      this.validateRequired();
      this.validateEmail();
      this.validatePassword();
      this.validatePhone();

      if (this.errors.length !== 0) {
        error.showPopup(this, this.errors)
      } else {
        let payload = {
          firstName: this.fname,
          lastName: this.lname,
          middleName: this.mname,
          nickname: this.nname,
          bio: this.bio,
          email: this.email,
          dateOfBirth: this.dob,
          phoneNumber: this.phone,
          homeAddress: {
            streetNumber: this.streetNumber,
            streetName: this.streetName,
            city: this.city,
            region: this.region,
            country: this.country,
            postcode: this.postcode
          },
          password: this.password
        };

        api.loginRegister
          .register(payload)
          .then(res => {
            this.home(res.data.userId);
          })
          .catch(err => {
            logger.getLogger().warn(err);
            error.showPopupFromServer(this, err);
          });
      }
    },

    uploadImage(userId) {
      if (this.image) {
        api.users.postImage(userId, this.image)
            .catch(err => {
              logger.getLogger().warn(err);
              error.showPopupFromServer(this, err);
            });
      }
    },

    /**
     * Clears the email field
     */
    clearEmailClick() {
      this.email = '';
    },

    /**
     * Clears the confirm email field
     */
    clearConEmailClick() {
      this.conEmail = '';
    },

    /**
     * Sets the background image to default preview image
     */
    setPreview(image) {
      document.querySelector(".profile-image-preview").style.backgroundImage = `url(${image})`;
    }
  },

  watch: {

    /**
     * Once a new image is uploaded
     * checks that the size is not bigger than 1mb
     * sets the imageURL
     * sets the background image to new preview image
     */
    image() {
      if (this.image.size > 1048576) {
        this.errors.push(`The file '${this.image.name}' is greater than 1mb!`);
        error.showPopup(this, this.errors);

        this.image = null;
        this.imageURL = null;
      } else {
        this.imageURL = URL.createObjectURL(this.image);

        let reader = new FileReader();
        reader.readAsDataURL(this.image);

        reader.onloadend = function () {
          document.querySelector(".profile-image-preview").style.backgroundImage = `url(${reader.result})`;
        }
      }
    }
  }
};

export default RegisterComponent;
</script>
