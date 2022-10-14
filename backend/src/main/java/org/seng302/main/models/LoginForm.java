package org.seng302.main.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A mapping of the incoming JSON message from a login request
 */
public class LoginForm {

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

    public LoginForm(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public LoginForm() {
    }

    /**
     * Get email
     *
     * @return String of email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Get password
     *
     * @return String of password
     */
    public String getPassword() {
        return password;
    }

}
