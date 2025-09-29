package com.restlearningjourney.store.dtos;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemProductDto {
    private  Integer id;
    private String name;
    private BigDecimal price;
}
