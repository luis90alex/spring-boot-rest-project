package com.restlearningjourney.store.services;

import com.restlearningjourney.store.config.JwtConfig;
import com.restlearningjourney.store.entities.User;
import com.restlearningjourney.store.repositories.UserRepository;
import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class JwtService {

    private final JwtConfig jwtConfig;
    private final UserRepository userRepository;

    public Jwt generateAccessToken(User user) {
        return generateToken(user, jwtConfig.getAccessTokenExpiration());
    }

    public Jwt generateRefreshToken(User user) {
        return generateToken(user, jwtConfig.getRefreshTokenExpiration());
    }

    private Jwt generateToken(User user, long tokenExpiration) {
        Claims claims = Jwts.claims()
                .subject(user.getId().toString())
                .add("email",  user.getEmail())
                .add("name",  user.getName())
                .add("role",  user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + tokenExpiration * 1000))
                .build();
        return new Jwt(claims, jwtConfig.getSecretKey());
    }

    public Jwt parse(String token){
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
