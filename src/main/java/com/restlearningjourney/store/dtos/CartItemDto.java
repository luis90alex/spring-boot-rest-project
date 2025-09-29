package com.restlearningjourney.store.dtos;

import com.restlearningjourney.store.entities.Product;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemDto {

    private CartItemProductDto product;
    private Integer quantity;
    private BigDecimal totalPrice;
}
