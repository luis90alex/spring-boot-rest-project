package com.restlearningjourney.store.dtos;

import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AddItemRequest {

    @NotNull(message = "product id must not be null")
    //@Positive(message = "product id must be positive")
    private Long productId;

}
