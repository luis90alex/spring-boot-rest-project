package com.restlearningjourney.store.dtos;


import com.restlearningjourney.store.entities.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class CartDto {

    private String id; //UUID as text

    @Builder.Default
    private List<CartItemDto> items = new ArrayList<>();

    private BigDecimal totalPrice;
}
