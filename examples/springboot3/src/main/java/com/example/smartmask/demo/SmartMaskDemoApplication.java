package com.example.smartmask.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Main application class for the SmartMask demo application.
 * <p>
 * This class demonstrates how to set up a Spring Boot application with SmartMask
 * for sensitive data masking in both JSON responses and logs. It also configures
 * Spring Security to demonstrate role-based masking.
 * </p>
 * <p>
 * Key features demonstrated:
 * </p>
 * <ul>
 *   <li>Integration with Spring Security for role-based access control</li>
 *   <li>Configuration of public and admin-only endpoints</li>
 *   <li>Setup of test users with different roles for demonstration</li>
 * </ul>
 * <p>
 * The security configuration is important for SmartMask's role-based masking feature.
 * Fields annotated with {@code @Sensitive(rolesAllowed = {"ROLE_ADMIN"})} will only
 * be visible to users with the ADMIN role.
 * </p>
 */
@SpringBootApplication
@EnableWebSecurity
public class SmartMaskDemoApplication {

    /**
     * Main method to start the application.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(SmartMaskDemoApplication.class, args);
    }

    /**
     * Configures the security filter chain for the application.
     * <p>
     * This configuration is crucial for SmartMask's role-based masking feature:
     * </p>
     * <ul>
     *   <li>Public endpoints (/api/public/**) are accessible without authentication</li>
     *   <li>Admin endpoints (/api/admin/**) require the ADMIN role</li>
     *   <li>Basic authentication is enabled for testing with the provided test users</li>
     * </ul>
     * <p>
     * SmartMask uses Spring Security's authentication context to determine the
     * current user's roles when applying role-based masking rules.
     * </p>
     * 
     * @param http The HttpSecurity to configure
     * @return The configured SecurityFilterChain
     * @throws Exception If an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .httpBasic(httpBasic -> {});

        return http.build();
    }

    /**
     * Configures test users for the application.
     * <p>
     * This method creates two users for testing:
     * </p>
     * <ul>
     *   <li>A regular user with the USER role</li>
     *   <li>An admin user with both ADMIN and USER roles</li>
     * </ul>
     * <p>
     * The admin user can access the admin endpoints and see unmasked values for
     * fields annotated with {@code @Sensitive(rolesAllowed = {"ROLE_ADMIN"})}.
     * </p>
     * <p>
     * Note: In a production application, you would use a more secure password storage
     * mechanism. The default password encoder is used here for simplicity.
     * </p>
     * 
     * @return A UserDetailsService with the configured users
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
            .username("user")
            .password("password")
            .roles("USER")
            .build();

        UserDetails admin = User.withDefaultPasswordEncoder()
            .username("admin")
            .password("admin")
            .roles("ADMIN", "USER")
            .build();

        return new InMemoryUserDetailsManager(user, admin);
    }
}
