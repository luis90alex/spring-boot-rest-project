package com.restlearningjourney.store.users;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String email;
    private String name;
}
