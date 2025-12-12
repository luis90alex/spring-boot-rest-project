package com.restlearningjourney.store.auth;


import com.restlearningjourney.store.users.User;
import com.restlearningjourney.store.users.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;



//Activate mockito extension
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @InjectMocks//Injects the mocks in this class
    private AuthService authService;

    @AfterEach
    void tearDown() {
        //Used to avoid authentications of previous test interact with the current test
        SecurityContextHolder.clearContext();
    }

    @Test
    //given_when_then
    void givenValidUser_whenLogin_thenReturnLoginResponseWithTokens() {
        // given
        LoginRequest req = new LoginRequest();
        req.setEmail("a@b.com");
        req.setPassword("secret");

        User u = new User();
        u.setId(42L);
        u.setEmail("a@b.com");

        Jwt access = mock(Jwt.class);
        Jwt refresh = mock(Jwt.class);

        //Stubs
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(u));
        when(jwtService.generateAccessToken(u)).thenReturn(access);
        when(jwtService.generateRefreshToken(u)).thenReturn(refresh);

        // when
        LoginResponse res = authService.login(req);

        // then
        assertNotNull(res);
        assertSame(access, res.getAccessToken());
        assertSame(refresh, res.getRefreshToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail("a@b.com");
        verify(jwtService).generateAccessToken(u);
        verify(jwtService).generateRefreshToken(u);
    }

    @Test
    void givenValidPrincipal_whenGetCurrentUser_thenReturnUser() {
        // given
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(99L);
        SecurityContextHolder.getContext().setAuthentication(auth);

        User u = new User();
        u.setId(99L);
        when(userRepository.findById(99L)).thenReturn(Optional.of(u));

        // when
        User res = authService.getCurrentUser();

        // then
        assertNotNull(res);
        assertEquals(99L, res.getId());
        verify(userRepository).findById(99L);
    }

    @Test
    void givenInvalidToken_whenRefreshAccessToken_thenThrowBadCredentialsException() {
        // given
        when(jwtService.parse("bad.token")).thenReturn(null);

        // when + then
        assertThrows(BadCredentialsException.class, () -> authService.refreshAccessToken("bad.token"));
        verify(jwtService).parse("bad.token");
    }

    @Test
    void givenValidToken_whenRefreshAccessToken_thenReturnNewAccessToken() {
        // given
        Jwt parsed = mock(Jwt.class);
        when(parsed.isExpired()).thenReturn(false);
        when(parsed.getUserId()).thenReturn(7L);

        User u = new User();
        u.setId(7L);

        Jwt newAccess = mock(Jwt.class);

        when(jwtService.parse("valid.refresh")).thenReturn(parsed);
        when(userRepository.findById(7L)).thenReturn(Optional.of(u));
        when(jwtService.generateAccessToken(u)).thenReturn(newAccess);

        // when
        Jwt res = authService.refreshAccessToken("valid.refresh");

        // then
        assertNotNull(res);
        assertSame(newAccess, res);
        verify(jwtService).parse("valid.refresh");
        verify(userRepository).findById(7L);
        verify(jwtService).generateAccessToken(u);
    }
}
