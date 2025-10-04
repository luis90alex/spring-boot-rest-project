package com.restlearningjourney.store.mappers;

import com.restlearningjourney.store.dtos.CartItemProductDto;
import com.restlearningjourney.store.dtos.ProductDto;
import com.restlearningjourney.store.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "category.id", target = "categoryId")
    ProductDto toDto(Product product);
    @Mapping(target = "category.id", ignore = true)
    Product toEntity(ProductDto productDto);
    @Mapping(target = "id", ignore = true)
    void updateProduct(ProductDto productDto, @MappingTarget Product product);
}
