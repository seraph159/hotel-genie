package com.jeff.hotel_management_system.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenWhitelistRedisCacheClient {

    private final StringRedisTemplate redisTemplate;

    TokenWhitelistRedisCacheClient(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private static final String WHITELIST_PREFIX = "jwt_whitelist:";

    public void set(String email, String token, long expirationTimeInMs) {
        // Store the token in Redis with an expiration time
        this.redisTemplate.opsForValue().set(WHITELIST_PREFIX + email, token, expirationTimeInMs, TimeUnit.MILLISECONDS);
    }

    public String get(String email) {
        return this.redisTemplate.opsForValue().get(WHITELIST_PREFIX + email);
    }

    public void delete(String email) {
        this.redisTemplate.delete(WHITELIST_PREFIX + email);
    }

    public boolean isTokenWhitelisted(String email, String token) {

        String tokenFromRedis = this.get(email);
        // Check if the token exists in Redis
        return tokenFromRedis != null && tokenFromRedis.equals(token);
    }

}