package com.restlearningjourney.store.controllers;

import com.restlearningjourney.store.dtos.UserDto;
import com.restlearningjourney.store.entities.User;
import com.restlearningjourney.store.mappers.UserMapper;
import com.restlearningjourney.store.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @GetMapping
    public List<UserDto> getAllUser(){
        return userRepository.findAll()
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
}
