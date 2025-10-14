package com.restlearningjourney.store.services;

import com.restlearningjourney.store.config.JwtConfig;
import com.restlearningjourney.store.entities.Role;
import com.restlearningjourney.store.entities.User;
import com.restlearningjourney.store.repositories.UserRepository;
import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.restlearningjourney.store.utils.Jwt;
import java.util.Date;

@Service
@AllArgsConstructor
public class JwtService {

    private final JwtConfig jwtConfig;
    private final UserRepository userRepository;

    public String generateAccessToken() {
        User user = getUser();
        return generateToken(user, jwtConfig.getAccessTokenExpiration());
    }

    public String generateRefreshToken() {
        User user = getUser();
        return generateToken(user, jwtConfig.getRefreshTokenExpiration());
    }

    private String generateToken(User user, long tokenExpiration) {
        return Jwts.builder()
                .subject(user.getId().toString())  //sub property of jwt
                .issuedAt(new Date())
                .claim("name", user.getName())
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .expiration(new Date(System.currentTimeMillis() + tokenExpiration * 1000))
                .signWith(jwtConfig.getSecretKey())
                .compact();
    }

    public Jwt parse(String token){
        Claims claims =getClaims(token);
        return new Jwt(getClaims(token), jwtConfig.getSecretKey());
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private User getUser(){
        Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        System.out.println("userId = " + userId);
        return userRepository.findById(userId).orElseThrow();
    }

}
