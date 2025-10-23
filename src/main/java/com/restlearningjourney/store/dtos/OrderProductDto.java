package com.restlearningjourney.store.dtos;

import java.math.BigDecimal;


public class OrderProductDto {

    private Long id;
    private String name;
    private BigDecimal price;

    public OrderProductDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
