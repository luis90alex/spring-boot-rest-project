package com.restlearningjourney.store.controllers;

import com.restlearningjourney.store.dtos.ChangePasswordRequest;
import com.restlearningjourney.store.dtos.RegisterUserRequest;
import com.restlearningjourney.store.dtos.UpdateUserRequest;
import com.restlearningjourney.store.dtos.UserDto;
import com.restlearningjourney.store.entities.Role;
import com.restlearningjourney.store.mappers.UserMapper;
import com.restlearningjourney.store.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public Iterable<UserDto> getAllUser(
            @RequestHeader(required = false, name ="X-Auth-Token") String authToken,
            @RequestParam(required = false, defaultValue = "", name ="sort")
            String sort) {

        System.out.println("getAllUser - sort: " + sort);
        System.out.println("getAllUser - authToken: " + authToken);

        if(!Set.of("name", "email").contains(sort)){
            sort ="name";
        }

        return userRepository.findAll(Sort.by(sort).ascending())
                .stream()
                .map( user -> userMapper.toDto(user))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id){
        var user =  userRepository.findById(id).orElse(null);
        if (user == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PostMapping
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegisterUserRequest request,
            UriComponentsBuilder uriBuilder){
        if (userRepository.existsByEmail(request.getEmail())){
            return ResponseEntity.badRequest().body(
                    Map.of("email", "Email is already registered")
            );
        }
        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);

        var userDto = userMapper.toDto(user);
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();
        return ResponseEntity.created(uri).body(userDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable(name ="id") Long id,
            @RequestBody UpdateUserRequest request)
    {
        System.out.println("updateUser - id: " + id);
        System.out.println("updateUser - request: " + request);

        var user = userRepository.findById(id).orElse(null);
        System.out.println(user);
        if (user == null){
            return ResponseEntity.notFound().build();
        }
        userMapper.updateUser(request,user);
        System.out.println("After mapping" + user);
        userRepository.save(user);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable Long id){
        var user = userRepository.findById(id).orElse(null);
        if (user == null){
            return ResponseEntity.notFound().build();
        }
        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequest request){
        var user = userRepository.findById(id).orElse(null);
        if (user == null){
            return ResponseEntity.notFound().build();
        }
        if (!user.getPassword().equals(request.getOldPassword())){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        user.setPassword(request.getNewPassword());
        userRepository.save(user);
        return  ResponseEntity.noContent().build();
    }
}
