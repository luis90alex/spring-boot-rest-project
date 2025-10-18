package com.restlearningjourney.store.dtos;

import java.util.UUID;

public class RegisterOrderDto {

    private UUID cartId;

    public RegisterOrderDto() {
    }

    public UUID getCartId() {
        return cartId;
    }

    public void setCartId(UUID cartId) {
        this.cartId = cartId;
    }
}
