package com.jeff.hotel_management_system.service;

import com.jeff.hotel_management_system.dto.LoginDto;
import com.jeff.hotel_management_system.utils.AuthUtils;
import com.jeff.hotel_management_system.utils.JwtUtils;
import com.jeff.hotel_management_system.utils.TokenWhitelistRedisCacheClient;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Transactional
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtTokenProvider;
    @Autowired
    private TokenWhitelistRedisCacheClient tokenWhitelistRedisCacheClient;

    @Override
    public String login(LoginDto loginDto) {

        // 01 - AuthenticationManager is used to authenticate the user
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(),
                loginDto.getPassword()
        ));

        /* 02 - SecurityContextHolder is used to allows the rest of the application to know
        that the user is authenticated and can use user data from Authentication object */
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 03 - Get the role of the authenticated user
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User has no roles"));

        // 04 - Generate the token based on username and secret key
        String token = jwtTokenProvider.generateToken(authentication);

        // Whitelist all existing tokens for the user
        String email = AuthUtils.getAuthenticatedEmail();
        long tokenExpirationTime = jwtTokenProvider.getTokenExpirationTime(token) - System.currentTimeMillis();

        tokenWhitelistRedisCacheClient.set(email, token, tokenExpirationTime);

        // 05 - Return the token and role to controller
        return token + "," + role;
    }
}

