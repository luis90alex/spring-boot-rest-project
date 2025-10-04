package com.restlearningjourney.store.mappers;


import com.restlearningjourney.store.dtos.CartDto;
import com.restlearningjourney.store.dtos.CartItemDto;
import com.restlearningjourney.store.entities.Cart;
import com.restlearningjourney.store.entities.CartItem;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "totalPrice" , expression = "java(cart.getTotalPrice())")
    CartDto toDto(Cart cart);

    @Mapping(target = "totalPrice", expression = "java(cartItem.getTotalPrice())")
    CartItemDto  toDto(CartItem cartItem);

}
