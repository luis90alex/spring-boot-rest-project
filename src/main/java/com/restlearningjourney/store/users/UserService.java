package com.restlearningjourney.store.users;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {


    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public Iterable<UserDto> getAllUser(String sort) {
        if(!Set.of("name", "email").contains(sort)){
            sort ="name";
        }
        return userRepository.findAll(Sort.by(sort).ascending())
                .stream()
                .map( user -> userMapper.toDto(user))
                .toList();
    }

    public UserDto getUser(Long id){
        var user =  userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        return userMapper.toDto(user);
    }

    public UserDto registerUser(RegisterUserRequest request){
        if (userRepository.existsByEmail(request.getEmail())){
            throw new DuplicateUserException();
        }
        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);

        return userMapper.toDto(user);
    }

    public UserDto updateUser(Long id, UpdateUserRequest request) {

        var user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        userMapper.updateUser(request,user);
        System.out.println("After mapping" + user);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    public void deleteUser(Long id){
        var user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        userRepository.delete(user);
    }

    public void changePassword(Long id,ChangePasswordRequest request){
        var user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        if (!user.getPassword().equals(request.getOldPassword())){
            throw new AccessDeniedException("Wrong old password");
        }
        user.setPassword(request.getNewPassword());
        userRepository.save(user);
    }
}
