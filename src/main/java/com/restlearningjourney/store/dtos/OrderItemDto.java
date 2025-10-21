package com.restlearningjourney.store.dtos;

import com.restlearningjourney.store.entities.Product;

import java.math.BigDecimal;

public class OrderItemDto {

    private OrderProductDto product;
    private Integer quantity;
    private BigDecimal totalPrice;

    public OrderItemDto() {
    }

    public OrderProductDto getProduct() {
        return product;
    }

    public void setProduct(OrderProductDto product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
