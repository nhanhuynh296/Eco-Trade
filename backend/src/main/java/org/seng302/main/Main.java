/*
 * Created on Wed Feb 10 2021
 *
 * The Unlicense
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or distribute
 * this software, either in source code form or as a compiled binary, for any
 * purpose, commercial or non-commercial, and by any means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors of this
 * software dedicate any and all copyright interest in the software to the public
 * domain. We make this dedication for the benefit of the public at large and to
 * the detriment of our heirs and successors. We intend this dedication to be an
 * overt act of relinquishment in perpetuity of all present and future rights to
 * this software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <https://unlicense.org>
 */

package org.seng302.main;

import lombok.extern.log4j.Log4j2;
import org.seng302.main.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This is the main entry point for Spring Boot application. This class's main
 * method will boot up the web application and can contain other sort of
 * configuration.
 */
@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
@Log4j2
public class Main {

    /**
     * Main method. Startup the Spring application with given args
     *
     * @param args arguments to be passed to spring (none expected)
     */
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        log.info("Spring Boot Backend Started Up");
    }

    /**
     * This method specifies the allowed origins (localhost and your VM), HTTP
     * methods and url mappings (all, denoted by "/**")
     *
     * @return the configurer that check incoming origins for CORS purposes
     * @see https://spring.io/guides/gs/rest-service-cors/
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                        .allowedOrigins("http://localhost:9500", "https://csse-s302g8.canterbury.ac.nz");
            }
        };
    }

}
