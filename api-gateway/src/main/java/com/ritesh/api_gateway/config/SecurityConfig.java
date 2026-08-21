package com.ritesh.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String secretKey;

    // 1. JWT Decoder
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

    // 2. Convert JWT role → Spring Security authority
    @Bean
    public ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {

            String role = jwt.getClaimAsString("role");

            return java.util.List.of(
                    new org.springframework.security.core.authority.SimpleGrantedAuthority(
                            "ROLE_" + role
                    )
            );
        });

        return new ReactiveJwtAuthenticationConverterAdapter(converter);
    }

    // 3. Security configuration
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter) {

        return http
                .csrf(csrf -> csrf.disable())

                .authorizeExchange(exchange -> exchange

                        // Public endpoints
                        .pathMatchers(
                                "/api/users/login",
                                "/api/users/register"
                        ).permitAll()

                        // ADMIN only
                        .pathMatchers(
                                "/api/products/admin/**"
                        ).hasRole("ADMIN")

                        // USER or ADMIN
                        .pathMatchers(
                                "/api/products/**",
                                "/api/cart/**",
                                "/api/orders/**"
                        ).hasAnyRole("USER", "ADMIN")

                        // Everything else requires login
                        .anyExchange().authenticated()
                )

                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        jwtAuthenticationConverter
                                )
                        )
                )

                .build();
    }
}