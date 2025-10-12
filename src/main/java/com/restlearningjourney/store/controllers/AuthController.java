package com.restlearningjourney.store.controllers;

import com.restlearningjourney.store.dtos.JwtResponse;
import com.restlearningjourney.store.dtos.LoginRequest;
import com.restlearningjourney.store.dtos.UserDto;
import com.restlearningjourney.store.entities.User;
import com.restlearningjourney.store.mappers.UserMapper;
import com.restlearningjourney.store.repositories.UserRepository;
import com.restlearningjourney.store.services.JwtService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @RequestBody @Valid LoginRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var token = jwtService.generateJwtToken(user);
        System.out.println(token);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/validate")
    public boolean validate(@RequestHeader("Authorization") String authHeader){
        System.out.println("Validate called");
        var token = authHeader.replace("Bearer ", "");
        return jwtService.validateToken(token);
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
