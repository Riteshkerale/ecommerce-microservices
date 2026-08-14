package com.ritesh.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String secretKey;

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {

        SecretKeySpec secretKeySpec =
                new SecretKeySpec(
                        secretKey.getBytes(StandardCharsets.UTF_8),
                        "HmacSHA256"
                );

        return NimbusReactiveJwtDecoder
                .withSecretKey(secretKeySpec)
                .build();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http) {

        return http
                .csrf(csrf -> csrf.disable())

                .authorizeExchange(exchange -> exchange

                        // Login and register don't require JWT
                        .pathMatchers(
                                "/api/users/login",
                                "/api/users/register"
                        ).permitAll()

                        // Everything else requires JWT
                        .anyExchange().authenticated()
                )

                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> {})
                )

                .build();
    }
}