package com.restlearningjourney.store.services;

import com.restlearningjourney.store.config.JwtConfig;
import com.restlearningjourney.store.entities.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class JwtService {

    private final JwtConfig jwtConfig;

    public String generateAccessToken(User user) {
        return generateToken(user, jwtConfig.getAccessTokenExpiration());
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, jwtConfig.getRefreshTokenExpiration());
    }

    private String generateToken(User user, long tokenExpiration) {
        return Jwts.builder()
                .subject(user.getId().toString())  //sub property of jwt
                .issuedAt(new Date())
                .claim("name", user.getName())
                .claim("email", user.getEmail())
                .expiration(new Date(System.currentTimeMillis() + tokenExpiration * 1000))
                .signWith(jwtConfig.getSecretKey())
                .compact();
    }

    public boolean validateToken(String token) {
       try {
           Claims claims = getClaims(token);
           return claims.getExpiration().after(new Date());
       }catch (JwtException e){
           return false;
       }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserIdFromToken(String token) {
        return Long.valueOf(getClaims(token).getSubject());
    }

}
