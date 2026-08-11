package com.ritesh.user_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtility jwtUtility;

    public JwtFilter(JwtUtility jwtUtility) {
        this.jwtUtility = jwtUtility;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Get Authorization header
        String authHeader = request.getHeader("Authorization");

        // 2. Check if token exists
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Remove "Bearer " from token
        String token = authHeader.substring(7);

        // 4. Validate JWT
        if (jwtUtility.validateToken(token)) {

            // 5. Extract username
            String username = jwtUtility.extractUsername(token);

            // 6. Extract role
            String role = jwtUtility.extractRole(token);

            // 7. Extract user ID
            Long userId = jwtUtility.extractUserId(token);

            // 7. Convert role into Spring Security authority
            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority("ROLE_" + role);

            // 8. Create authenticated user
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            Collections.singletonList(authority)
                    );

            // 9. Store authentication in Spring Security
            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);
        }

        // 10. Continue request
        filterChain.doFilter(request, response);
    }
}