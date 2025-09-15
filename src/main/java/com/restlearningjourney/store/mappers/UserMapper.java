package com.restlearningjourney.store.mappers;

import com.restlearningjourney.store.dtos.UserDto;
import com.restlearningjourney.store.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
}
