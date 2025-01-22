package com.jeff.hotel_management_system.utils;

import com.jeff.hotel_management_system.exception.PasswordChangeIllegalArgumentException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;


// Helper/Utility Class
@Component
public class JwtUtils {

    @Value("${security.jwt.secret-key}")
    private String jwtSecretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpirationMs;

    public String generateToken(Authentication authentication) {

        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationMs);

        String token = Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(expireDate)
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();

        return token;
    }

    private Key key(){

        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretKey));
    }

    // extract username from JWT token
    public String getUsername(String token){

        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // validate JWT token
    public boolean validateToken(String token){
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parse(token);
            return true;
        } catch (Exception e) {
            System.out.println("Invalid or expired token: " + e.getMessage());
            return false;
        }
    }

    // Extract expiration time from JWT token
    public long getTokenExpirationTime(String token) {
        try {
            return Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration()
                    .getTime(); // Converts Date to long (epoch milliseconds)
        } catch (Exception e) {
            throw new RuntimeException( "Could not extract expiration time: " + e.getMessage());
        }
    }

    public String getClaimFromToken(String token, String claimKey) {
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get(claimKey, String.class);
    }

}
