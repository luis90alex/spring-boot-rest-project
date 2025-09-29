package com.restlearningjourney.store.mappers;

import com.restlearningjourney.store.dtos.CartItemDto;
import com.restlearningjourney.store.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface CartItemMapper {

    @Mapping(target = "product", source = "product") // use ProductMapper
    @Mapping(
            target = "totalPrice",
            expression = "java((cartItem.getProduct() != null && cartItem.getProduct().getPrice() != null) ? " +
                    "cartItem.getProduct().getPrice().multiply(java.math.BigDecimal.valueOf(cartItem.getQuantity())) : " +
                    "java.math.BigDecimal.ZERO)"
    )
    CartItemDto toDto(CartItem cartItem);
}