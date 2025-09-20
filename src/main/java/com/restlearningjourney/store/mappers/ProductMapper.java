package com.restlearningjourney.store.mappers;

import com.restlearningjourney.store.dtos.ProductDto;
import com.restlearningjourney.store.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "category.id", target = "categoryId")
    ProductDto toDto(Product product);
}
