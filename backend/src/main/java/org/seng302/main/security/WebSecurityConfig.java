package org.seng302.main.security;

import org.seng302.main.MainCORSFilter;
import org.seng302.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

/**
 * Configuration class for Spring Security (Web).
 * anyRequest().authenticated() is used to automatically
 * require authentication for API endpoints.
 * This means if no sessionId is provided then the current
 * user isn't logged in and a 401 code is sent back.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    CustomUserDetailsService userDetailsService;

    @Autowired
    UserRepository userRepository;

    /**
     * Create bean that returns a custom user details service, which is used in authorization/login
     *
     * @return CustomUserDetailsService object
     */
    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    /**
     * Create a bean for a BCrypt Password encoder
     *
     * @return BCryptPasswordEncoder Object
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Create bean for authentication manager
     *
     * @return an Authentication Manager object
     * @throws Exception thrown if there is a problem creating the bean
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Configures an authentication manager builder to use daoAuthenticationProvider
     *
     * @param auth authentication manager builder
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    /**
     * Configures the usage of the password encoder bean and user details service bean
     *
     * @return DaoAuthenticationProvider
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService()); // custom user service
        provider.setPasswordEncoder(passwordEncoder()); // custom password encoder
        return provider;
    }

    /**
     * Configure application User authorization and filtering setting
     *
     * @param http allows configuring web based security for specific http requests
     * @throws Exception when there is a problem configuring application
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable();
        http.csrf().disable();
        // Needed in order to set the headers in the http.antMatchers filters
        http.addFilterBefore(new MainCORSFilter(), BasicAuthenticationFilter.class);

        // Stops unauthorized requests from the client returning a 302 redirect to the login form
        http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
            if (authException != null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().print("A user must be logged in to make this request.");
            }
        });

        // Used to stop certain users from accessing certain functions
        http.addFilter(new CustomAuthenticationFilter(authenticationManager(), userRepository))
                .authorizeRequests()
                // Users
                .antMatchers(HttpMethod.GET, "/user/loginCheck").authenticated()
                .antMatchers(HttpMethod.GET, "/users/**").authenticated()
                .antMatchers(HttpMethod.PUT, "/users/{id}/makeadmin").hasAnyRole("DEFAULT_ADMIN", "ADMIN")
                .antMatchers(HttpMethod.PUT, "/users/{id}/revokeadmin").hasAnyRole("DEFAULT_ADMIN", "ADMIN")
                // Business
                .antMatchers(HttpMethod.POST, "/businesses").authenticated()    // Ensures the user must be logged in to post a business
                .antMatchers(HttpMethod.DELETE, "/businesses/{id}").authenticated()
                .antMatchers(HttpMethod.PUT, "/businesses/{id}/makeAdministrator").authenticated()
                .antMatchers(HttpMethod.PUT, "/businesses/{id}/removeAdministrator").authenticated()
                .antMatchers(HttpMethod.DELETE, "/businesses/**").authenticated()
                .antMatchers(HttpMethod.GET, "/businesses?**").authenticated()
                .antMatchers(HttpMethod.GET, "/businesses/{id}").authenticated()
                // Product
                .antMatchers(HttpMethod.POST, "/businesses/{id}/product").authenticated()
                .antMatchers(HttpMethod.GET, "/businesses/{id}/product").authenticated()
                .antMatchers(HttpMethod.POST, "/businesses/{id}/products/{productId}/images").authenticated()
                // Inventory
                .antMatchers(HttpMethod.GET, "/businesses/{id}/inventory").authenticated()
                .antMatchers(HttpMethod.PUT, "/businesses/{id}/inventory/{id}").authenticated()
                // Listing
                .antMatchers(HttpMethod.GET, "/businesses/{id}/listings").authenticated()
                .antMatchers(HttpMethod.POST, "/businesses/{id}/listings").authenticated()
                .antMatchers(HttpMethod.GET, "/listings/{id}").authenticated()
                .antMatchers(HttpMethod.PUT, "/listings/{id}").authenticated()
                // Sales
                .antMatchers(HttpMethod.GET, "/businesses/{id}/saleshistory").authenticated()
                // Keywords
                .antMatchers(HttpMethod.GET, "/keywords/search").authenticated()
                .antMatchers(HttpMethod.POST, "/keywords").authenticated()
                // Notifications
                .antMatchers(HttpMethod.POST, "/notifications/message").authenticated()
                .antMatchers(HttpMethod.PUT, "/notifications/{notificationId}/tag").authenticated()
                // Cards
                .antMatchers(HttpMethod.PUT, "/cards/{id}").authenticated()
                .anyRequest().permitAll()
                .and()
                .httpBasic();

        // Specifies what the built-in logout will do
        http.logout()
                .addLogoutHandler(new CustomLogout(userRepository))
                .logoutSuccessHandler((new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))) // Sends 200 OK to client
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");
    }

}
