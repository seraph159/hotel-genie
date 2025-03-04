package com.jeff.hotel_management_system.config;

import com.jeff.hotel_management_system.utils.JwtUtils;
import com.jeff.hotel_management_system.utils.TokenWhitelistRedisCacheClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final TokenWhitelistRedisCacheClient redisCacheClient;

    private final JwtUtils jwtUtils;


    public JwtInterceptor(TokenWhitelistRedisCacheClient redisCacheClient, JwtUtils jwtUtils) {
        this.redisCacheClient = redisCacheClient;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Retrieve the Authorization header from the request
        String authorizationHeader = request.getHeader("Authorization");

        // If the Authorization header is null or doesn't start with "Bearer ", skip token validation
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return true; // Public request (e.g., login, register)
        }

        // Extract the JWT token
        String token = authorizationHeader.substring(7);

        // Validate the JWT and extract the userId claim
        if (!jwtUtils.validateToken(token)) {
            throw new BadCredentialsException("Invalid or expired token");
        }

        String emailId = jwtUtils.getUsername(token);

        // Check if the token is whitelisted in Redis
        if (!redisCacheClient.isTokenWhitelisted(emailId, token)) {
            throw new BadCredentialsException("Token is not whitelisted or has been revoked");
        }

        return true;
    }

}