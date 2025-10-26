package com.restlearningjourney.store.carts;


import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "totalPrice" , expression = "java(cart.getTotalPrice())")
    CartDto toDto(Cart cart);

    @Mapping(target = "totalPrice", expression = "java(cartItem.getTotalPrice())")
    CartItemDto  toDto(CartItem cartItem);

}
