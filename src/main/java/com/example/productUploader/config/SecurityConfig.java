package com.example.productUploader.config;

import com.example.productUploader.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**") // Disable CSRF protection for all /api/** routes
                )
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/api/**").permitAll() // Allow all API requests without authentication
                        .requestMatchers("/register", "/login", "/css/**", "/js/**", "/error", "/img/**", "/vendor/**").permitAll() // Public routes
                        .anyRequest().authenticated() // All other routes require authentication
                )
                .formLogin((form) -> form
                        .loginPage("/login")  // Custom login page
                        .permitAll()           // Allow access to the login page
                        .usernameParameter("email") // Use email instead of username
                        .defaultSuccessUrl("/index", true)  // Default URL to redirect after login if no saved request
                )
                .logout((logout) -> logout
                        .logoutUrl("/logout")  // Custom logout URL
                        .logoutSuccessUrl("/login?logout")  // Redirect after successful logout
                        .permitAll()
                        .logoutRequestMatcher(new org.springframework.security.web.util.matcher.AntPathRequestMatcher("/logout", "GET"))  // Allow GET for logout
                )
                .requestCache((cache) -> cache.disable()); // Disable caching of authenticated requests

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
