package com.restlearningjourney.store.dtos;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CheckoutRequest {

    @NotNull(message = "CartId is required")
    private UUID cartId;

    public CheckoutRequest() {
    }

    public UUID getCartId() {
        return cartId;
    }

    public void setCartId(UUID cartId) {
        this.cartId = cartId;
    }
}
