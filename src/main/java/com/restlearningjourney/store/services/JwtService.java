package com.restlearningjourney.store.services;

import com.restlearningjourney.store.entities.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Value("${spring.jwt.secret}")
    private String secret;
    public String generateJwtToken(User user) {

        final long tokenExpiration = 86400; // 1 day
        return Jwts.builder()
                .subject(user.getId().toString())  //sub property of jwt
                .issuedAt(new Date())
                .claim("name", user.getName())
                .claim("email", user.getEmail())
                .expiration(new Date(System.currentTimeMillis() + tokenExpiration*1000))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
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
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserIdFromToken(String token) {
        return Long.valueOf(getClaims(token).getSubject());
    }

}
