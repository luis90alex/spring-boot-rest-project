package com.restlearningjourney.store.controllers;

import com.restlearningjourney.store.dtos.JwtResponse;
import com.restlearningjourney.store.dtos.LoginRequest;
import com.restlearningjourney.store.repositories.UserRepository;
import com.restlearningjourney.store.services.JwtService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @RequestBody @Valid LoginRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var token = jwtService.generateJwtToken(request.getEmail());
        System.out.println(token);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/validate")
    public boolean validate(@RequestHeader("Authorization") String authHeader){
        var token = authHeader.replace("Bearer ", "");
        return jwtService.validateToken(token);
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
