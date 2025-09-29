package com.restlearningjourney.store.dtos;

import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterCartRequest {
    @PastOrPresent(message = "dateCreated can not be a future date")
    private LocalDate dateCreated;
}
