package com.restlearningjourney.store.mappers;

import com.restlearningjourney.store.dtos.RegisterUserRequest;
import com.restlearningjourney.store.dtos.UpdateUserRequest;
import com.restlearningjourney.store.dtos.UserDto;
import com.restlearningjourney.store.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    //@Mapping(target ="createdAt", expression = "java(java.time.LocalDateTime.now())")
    UserDto toDto(User user);
    User toEntity(RegisterUserRequest registerUserRequest);
    void updateUser(UpdateUserRequest request, @MappingTarget User user);
}
