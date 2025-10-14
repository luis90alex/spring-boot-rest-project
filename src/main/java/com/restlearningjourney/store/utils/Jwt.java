package com.restlearningjourney.store.utils;

import com.restlearningjourney.store.entities.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Component;

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

    public boolean isValid() {
        try {
            return claims.getExpiration().after(new Date());
        }catch (JwtException e){
            System.out.println("Invalid token");
            return false;
        }
    }

    public Long getUserId() {
        return Long.valueOf(claims.getSubject());
    }

    public Role getRole() {
        return Role.valueOf(claims.get("role", String.class));
    }

    @Override
    public String toString() {
        return "Jwt{" +
                "claims=" + claims +
                ", secretKey=" + secretKey +
                '}';
    }
}
