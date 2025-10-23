package com.restlearningjourney.store.mappers;


import com.restlearningjourney.store.dtos.CheckoutResponse;
import com.restlearningjourney.store.dtos.OrderDto;
import com.restlearningjourney.store.entities.CartItem;
import com.restlearningjourney.store.entities.Order;
import com.restlearningjourney.store.entities.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    CheckoutResponse toDto(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "product", target = "product")
    @Mapping(target = "unitPrice" , expression = "java(cartItem.getProduct().getPrice())")
    @Mapping(target = "totalPrice", expression = "java(cartItem.getTotalPrice())")
    OrderItem fromCartItemToOrderItem(CartItem cartItem);

    OrderDto fromOrderToDto(Order order);
}
