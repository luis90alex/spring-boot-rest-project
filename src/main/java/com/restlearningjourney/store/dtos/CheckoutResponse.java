package com.restlearningjourney.store.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;

public class CheckoutResponse {
    @JsonProperty("orderId")
    private Long id;

    public CheckoutResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
