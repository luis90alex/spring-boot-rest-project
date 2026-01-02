package com.restlearningjourney.store.auth;

import com.restlearningjourney.store.users.User;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    private final JwtConfig jwtConfig;

    public JwtService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public com.restlearningjourney.store.auth.Jwt generateAccessToken(User user) {
        return generateToken(user, jwtConfig.getAccessTokenExpiration());
    }

    public com.restlearningjourney.store.auth.Jwt generateRefreshToken(User user) {
        return generateToken(user, jwtConfig.getRefreshTokenExpiration());
    }

    private com.restlearningjourney.store.auth.Jwt generateToken(User user, long tokenExpiration) {
        Claims claims = Jwts.claims()
                .subject(user.getId().toString())
                .add("email",  user.getEmail())
                .add("name",  user.getName())
                .add("role",  user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + tokenExpiration * 1000))
                .build();
        return new com.restlearningjourney.store.auth.Jwt(claims, jwtConfig.getSecretKey());
    }

    public com.restlearningjourney.store.auth.Jwt parse(String token){
        try {
            Claims claims =getClaims(token);
            return new Jwt(getClaims(token), jwtConfig.getSecretKey());
        }catch (Exception e){
            return null;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
