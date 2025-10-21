package com.restlearningjourney.store.dtos;

import java.util.UUID;

public class RegisterOrderRequest {

    private UUID cartId;

    public RegisterOrderRequest() {
    }

    public UUID getCartId() {
        return cartId;
    }

    public void setCartId(UUID cartId) {
        this.cartId = cartId;
    }
}
