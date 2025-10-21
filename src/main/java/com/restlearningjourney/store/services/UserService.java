package com.restlearningjourney.store.services;

import com.restlearningjourney.store.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       var user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
       return new User(
               user.getEmail(),
               user.getPassword(),
               Collections.emptyList()
       );
    }

    public com.restlearningjourney.store.entities.User getCurrentUser() {
        Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();

        com.restlearningjourney.store.entities.User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new BadCredentialsException("Invalid username or password");
        }
        return user;
    }
}
