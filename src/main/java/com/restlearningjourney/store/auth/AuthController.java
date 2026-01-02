package com.restlearningjourney.store.auth;

import com.restlearningjourney.store.users.UserDto;
import com.restlearningjourney.store.users.User;
import com.restlearningjourney.store.users.UserMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {


    private final JwtConfig jwtConfig;
    private final UserMapper userMapper;
    private final AuthService authService;

    public AuthController(JwtConfig jwtConfig, UserMapper userMapper, AuthService authService) {
        this.jwtConfig = jwtConfig;
        this.userMapper = userMapper;
        this.authService = authService;
    }

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
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
