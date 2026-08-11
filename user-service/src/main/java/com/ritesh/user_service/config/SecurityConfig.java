package com.ritesh.user_service.config;

import com.ritesh.user_service.security.JwtFilter;
import com.ritesh.user_service.security.JwtUtility;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtUtility jwtUtility;

    public SecurityConfig(JwtUtility jwtUtility) {
        this.jwtUtility = jwtUtility;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        JwtFilter jwtFilter = new JwtFilter(jwtUtility);

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // =========================
                        // PUBLIC
                        // =========================

                        .requestMatchers(
                                "/api/users/register",
                                "/api/users/login"
                        ).permitAll()


                        // =========================
                        // ADMIN ONLY
                        // =========================

                        // Get all users
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/users"
                        ).hasRole("ADMIN")

                        // Delete any user
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/users/**"
                        ).hasRole("ADMIN")


                        // =========================
                        // AUTHENTICATED USERS
                        // =========================

                        // Get user by ID
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/users/**"
                        ).authenticated()

                        // Update user
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/users/**"
                        ).authenticated()


                        // Everything else
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}