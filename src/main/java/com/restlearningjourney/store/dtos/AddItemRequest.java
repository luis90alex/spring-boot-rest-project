package com.restlearningjourney.store.dtos;

import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AddItemRequest {

    //@NotNull(message = "product id must not be null")
    //@Positive(message = "product id must be positive")
    private Long productId;

    @NotNull(message = "Quantity must not be null")
    @Min(value = 1, message = "Quantity must be between 1 and 100")
    @Max(value = 100, message = "Quantity must be between 1 and 100")
    private Integer quantity;
}
