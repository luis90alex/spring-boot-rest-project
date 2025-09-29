package com.restlearningjourney.store.mappers;


import com.restlearningjourney.store.dtos.CartDto;
import com.restlearningjourney.store.dtos.RegisterCartRequest;
import com.restlearningjourney.store.entities.Cart;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.util.ArrayList;

@Mapper(componentModel = "spring", uses = CartItemMapper.class)
public interface CartMapper {
    Cart toEntity(RegisterCartRequest registerCartRequest);

    @Mapping(target = "id", expression = "java(cart.getId() != null ? cart.getId().toString() : null)")
    @Mapping(target = "items", source = "cartItems")
    @Mapping(target = "totalPrice", ignore = true) // afterMapping
    CartDto toDto(Cart cart);

    @AfterMapping
    default void calculateTotalPrice(Cart cart, @MappingTarget CartDto dto) {
        if (dto.getItems() == null) {
            dto.setItems(new ArrayList<>()); //items = []
        }

        var totalPrice = dto.getItems().stream()
                .map(item -> item.getTotalPrice() != null ? item.getTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
        dto.setTotalPrice(totalPrice);
    }
}
