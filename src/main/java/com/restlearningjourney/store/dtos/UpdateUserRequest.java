package com.restlearningjourney.store.dtos;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String email;
    private String name;
}
