package com.restlearningjourney.store.controllers;

import com.restlearningjourney.store.config.JwtConfig;
import com.restlearningjourney.store.dtos.JwtResponse;
import com.restlearningjourney.store.dtos.LoginRequest;
import com.restlearningjourney.store.dtos.UserDto;
import com.restlearningjourney.store.entities.User;
import com.restlearningjourney.store.mappers.UserMapper;
import com.restlearningjourney.store.repositories.UserRepository;
import com.restlearningjourney.store.services.JwtService;
import com.restlearningjourney.store.services.Jwt;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        Jwt accessToken = jwtService.generateAccessToken(user);
        Jwt refreshToken = jwtService.generateRefreshToken(user);

        Cookie cookie = new Cookie("refreshToken", refreshToken.toString());
        cookie.setHttpOnly(true);//not accessed by javascript
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());// 7d
        cookie.setSecure(true);//only accessible from https


        System.out.println(accessToken);
        System.out.println(refreshToken);
        System.out.println(cookie);

        response.addCookie(cookie);
        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(
            @CookieValue(value ="refreshToken") String refreshToken){

        Jwt jwt =  jwtService.parse(refreshToken);
        if(jwt.isExpired()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User user = userRepository.findById(jwt.getUserId()).orElseThrow();
        Jwt accessToken = jwtService.generateAccessToken(user);

        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }


    @GetMapping("/me")
    public ResponseEntity<UserDto> me(){
        Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();

        User user = userRepository.findById(userId).orElse(null);
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        UserDto userDto = userMapper.toDto(user);
        return ResponseEntity.ok(userDto);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException() {
        System.out.println("Bad credentialsException");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
