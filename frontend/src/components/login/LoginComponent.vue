<template>
    <div class="login-container">
        <div class="login-form" @submit.prevent="login">
          <div class="login-header"><img class="login-logo" src="../../../public/assets/logo2.png" alt="Logo"> Kiwifruit</div>

          <div class="login-input">
            <b-field  label="Email" label-position="on-border">
              <b-input type="email" icon="email" icon-right="close-circle"
                       placeholder="user@email.com" v-model="email" required/>
            </b-field>
            <b-field label="Password" label-position="on-border" required>
              <b-input type="password" placeholder="********" v-model="password" @keydown.native.enter="login" password-reveal required/>
            </b-field>
          </div>

          <div class="login-buttons">
            <div class="login-button-login">
              <b-button type="submit is-success" @click="login">Log In</b-button>
            </div>
            <div class="login-button-signup">
              <b-button type="submit is-success" tag="router-link" :to="{ name: 'register' }">Sign Up</b-button>
            </div>
          </div>
      </div>
    </div>
</template>

<script>
import api from '../../api/api';
import error from '../errorPopup';
import logger from "../../logger";
import logo from '../../../public/assets/logo2.png';

const LoginComponent = {
  name: "login",

  data() {
    return {
      email: "",
      password: "",
      error: [],
      isLogged: false,
      logoPreview: null
    };
  },

  created() {
    this.logoPreview = logo;
    this.isLogged = false;
    localStorage.clear();
    api.loginRegister.logoutUser();
  },

  methods: {

    /**
     * Checks that the email contains the right symbols and order
     */
    validateEmail() {
      var emailRegEx = /[a-zA-Z0-9]+@[a-zA-Z0-9]+.[a-zA-Z]+/;

      if (!emailRegEx.test(this.email)) {
        this.errors.push("Wrong email format");
      }
    },

    /**
     * Function when login button press (or enter pushed in password box)
     */
    login() {
      this.errors = [];
      this.validateEmail();

      if (this.errors.length !== 0) {
        error.showPopup(this, this.errors);
        return;
      }

      let payload = {
        email: this.email.toLowerCase(),
        password: this.password
      };

      api.loginRegister
          .login(payload)
          .then(res => {
            const userId = res.data.userId;
            localStorage.setItem("id", userId);
            localStorage.setItem("business-id", -1);
            localStorage.setItem("user-type", "user");

            this.setRole(userId);
          })
          .catch(err => {
            logger.getLogger().warn(err);
            error.showPopupFromServer(this, err, true);
          });
    },

    /**
     * Set users role
     *
     * @param userId Users ID
     */
    setRole(userId) {
      api.users
        .getUser(userId)
        .then(res => {
          const userRole = res.data.role;
          localStorage.setItem("role", userRole);

          this.$router.push({name: 'home'});
        })
        .catch(err => {
          logger.getLogger().warn(err);
        });
    }
  }
};

export default LoginComponent;
</script>
