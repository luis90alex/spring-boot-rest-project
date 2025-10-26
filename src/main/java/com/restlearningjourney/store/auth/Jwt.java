package com.restlearningjourney.store.auth;

import com.restlearningjourney.store.users.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.util.Date;

public class Jwt {

    private final Claims claims;
    private final SecretKey secretKey;

    public Jwt(Claims claims, SecretKey secretKey) {
        this.claims = claims;
        this.secretKey = secretKey;
    }

    public Claims getClaims() {
        return claims;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public boolean isExpired() {
        return claims.getExpiration().before(new Date());
    }

    public Long getUserId() {
        return Long.valueOf(claims.getSubject());
    }

    public Role getRole() {
        return Role.valueOf(claims.get("role", String.class));
    }

    //Jwt with claims and signed
    @Override
    public String toString() {
        return Jwts.builder().claims(claims)
                .signWith(secretKey)
                .compact();
    }
}
