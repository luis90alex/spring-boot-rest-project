package com.restlearningjourney.store.auth;

import com.restlearningjourney.store.users.User;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtConfig jwtConfig;
    @InjectMocks
    private JwtService jwtService;

    @Test
    void givenUser_whenGenerateTokens_thenBothContainUserIdInClaims() {
        // given
        User user = new User();
        user.setId(1L);
        user.setEmail("a@b.c");
        user.setName("name");
        user.setPassword("password");

        //  real 32 bytes secretKey  HS256
        SecretKey secretKey = Keys.hmacShaKeyFor("01234567012345670123456701234567".getBytes(StandardCharsets.UTF_8));

        // Stubs
        when(jwtConfig.getAccessTokenExpiration()).thenReturn(3600); // 1 hora (si tu impl usa segundos)
        when(jwtConfig.getRefreshTokenExpiration()).thenReturn(60 * 60 * 24 * 7); // 7 días
        when(jwtConfig.getSecretKey()).thenReturn(secretKey);

        // when
        Jwt accessToken = jwtService.generateAccessToken(user);
        Jwt refreshToken = jwtService.generateRefreshToken(user);

        // then
        assertNotNull(accessToken);
        assertNotNull(refreshToken);

        assertEquals(1L, accessToken.getUserId());
        assertEquals(1L, refreshToken.getUserId());

        assertFalse(accessToken.isExpired(), "accessToken not expired");
        assertFalse(refreshToken.isExpired(), "refreshToken not expired");

        //verify mocks are consulted
        verify(jwtConfig).getAccessTokenExpiration();
        verify(jwtConfig).getRefreshTokenExpiration();
    }

    @Test
    void givenInvalidToken_whenParse_thenReturnNull() {

        //given
        SecretKey secretKey = Keys.hmacShaKeyFor("01234567012345670123456701234567".getBytes(StandardCharsets.UTF_8));
        when(jwtConfig.getSecretKey()).thenReturn(secretKey);
        //when
        Jwt parse = jwtService.parse("bad.token");
        //then
        assertNull(parse);
        verify(jwtConfig).getSecretKey();
    }
}