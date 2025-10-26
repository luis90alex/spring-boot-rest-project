package com.restlearningjourney.store.carts;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AddItemToCartRequest {

    @NotNull(message = "product id must not be null")
    //@Positive(message = "product id must be positive")
    private Long productId;

}
