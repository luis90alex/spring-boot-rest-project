package com.restlearningjourney.store.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;

public class RegisterOrderDto {
    @JsonProperty("orderId")
    private Long id;

    public RegisterOrderDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
