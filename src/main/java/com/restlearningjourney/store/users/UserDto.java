package com.restlearningjourney.store.users;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class UserDto {

    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
}
