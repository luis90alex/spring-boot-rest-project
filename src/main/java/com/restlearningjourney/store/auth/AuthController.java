package com.restlearningjourney.store.auth;

import com.restlearningjourney.store.users.UserDto;
import com.restlearningjourney.store.users.User;
import com.restlearningjourney.store.users.UserMapper;
import com.restlearningjourney.store.users.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
    private final AuthService authService;

    @PostMapping("/login")
    public JwtResponse login(
            @RequestBody @Valid LoginRequest request,
            HttpServletResponse response) {

        LoginResponse loginResponse = authService.login(request);
        String refreshToken = loginResponse.getRefreshToken().toString();

        Cookie cookie = new Cookie("refreshToken",refreshToken);
        cookie.setHttpOnly(true);//not accessed by javascript
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());// 7d
        cookie.setSecure(true);//only accessible from https
        response.addCookie(cookie);

        System.out.println(refreshToken);
        System.out.println(cookie);

        return new JwtResponse(loginResponse.getAccessToken().toString());
    }

    @PostMapping("/refresh")
    public JwtResponse refresh(
            @CookieValue(value ="refreshToken") String refreshToken){
        Jwt accessToken = authService.refreshAccessToken(refreshToken);
        return new JwtResponse(accessToken.toString());
    }


    @GetMapping("/me")
    public ResponseEntity<UserDto> me(){

        User user = authService.getCurrentUser();
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
