package com.restlearningjourney.store.orders;

import java.math.BigDecimal;

public class OrderItemDto {

    private ProductDto product;
    private Integer quantity;
    private BigDecimal totalPrice;

    public OrderItemDto() {
    }

    public ProductDto getProduct() {
        return product;
    }

    public void setProduct(ProductDto product) {
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
