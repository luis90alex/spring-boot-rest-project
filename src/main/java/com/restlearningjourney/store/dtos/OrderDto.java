package com.restlearningjourney.store.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderDto {
    @JsonProperty("orderId")
    private Long id;

    public OrderDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
