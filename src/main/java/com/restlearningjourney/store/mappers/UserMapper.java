package com.restlearningjourney.store.mappers;

import com.restlearningjourney.store.dtos.UserDto;
import com.restlearningjourney.store.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserMapper {

    //@Mapping(target ="createdAt", expression = "java(java.time.LocalDateTime.now())")
    UserDto toDto(User user);
}
