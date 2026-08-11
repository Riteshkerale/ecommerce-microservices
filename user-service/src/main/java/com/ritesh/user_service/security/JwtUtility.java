
        package com.ritesh.user_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtility {

    private final String SECRET_KEY =
            "my-super-secret-key-for-ecommerce-microservice-123456";

    private final long EXPIRATION_TIME =
            1000 * 60 * 60; // 1 hour

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // Generate JWT
    public String generateToken(Long userId, String username, String role) {

        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + EXPIRATION_TIME)
                )
                .signWith(getSigningKey())
                .compact();
    }

    // Extract username
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Extract role
    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

//    extract id
    public Long extractUserId(String token) {

        return extractClaims(token)
                .get("userId", Long.class);
    }

    // Validate JWT
    public boolean validateToken(String token) {

        try {
            extractClaims(token);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    // Extract all claims
    private Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

