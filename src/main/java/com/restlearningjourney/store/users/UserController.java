package com.restlearningjourney.store.users;

import com.restlearningjourney.store.common.ErrorDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
    private final UserService userService;

    @GetMapping
    public Iterable<UserDto> getAllUser(
            @RequestHeader(required = false, name ="X-Auth-Token") String authToken,
            @RequestParam(required = false, defaultValue = "", name ="sort")
            String sort) {
        System.out.println("UserController - getAllUser - sort: " + sort);
        System.out.println("UserController - getAllUser - authToken: " + authToken);
        return userService.getAllUser(sort);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id){
        UserDto userDto =  userService.getUser(id);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping
    public ResponseEntity<UserDto> registerUser(
            @Valid @RequestBody RegisterUserRequest request,
            UriComponentsBuilder uriBuilder){
        UserDto userDto = userService.registerUser(request);
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();
        return ResponseEntity.created(uri).body(userDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable(name ="id") Long id,
            @RequestBody UpdateUserRequest request)
    {
        System.out.println("UserController - updateUser - id: " + id);
        System.out.println("UserController - updateUser - request: " + request);

        UserDto userDto = userService.updateUser(id, request);
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping ("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
    }

    @PostMapping("/{id}/change-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequest request){
        userService.changePassword(id, request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Void> handleUserNotFound(){
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ErrorDto> handleDuplicateUser(){
        return ResponseEntity.badRequest().body(new ErrorDto("User already exists"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Void> handleAccessDenied(){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


}
