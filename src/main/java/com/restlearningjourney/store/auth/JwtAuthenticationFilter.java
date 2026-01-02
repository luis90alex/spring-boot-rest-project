package com.restlearningjourney.store.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("doFilter internal - Init JwtAuthenticationFilter");
        String authHeader = request.getHeader("Authorization");
        if (authHeader ==  null || !authHeader.startsWith("Bearer ")) {
            System.out.println("doFilterInternal - Null or invalid Authorization header");
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.replace("Bearer ", "");

        Jwt jwt = jwtService.parse(token);

        if(jwt == null || jwt.isExpired()){
            System.out.println("doFilterInternal - Invalid token ");
            filterChain.doFilter(request, response);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = new  UsernamePasswordAuthenticationToken(
                jwt.getUserId(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + jwt.getRole())));

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("doFilterInternal - Authenticated");
        filterChain.doFilter(request, response);
    }
}
