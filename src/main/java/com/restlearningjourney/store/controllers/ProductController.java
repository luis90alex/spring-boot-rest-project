package com.restlearningjourney.store.controllers;

import com.restlearningjourney.store.dtos.ProductDto;
import com.restlearningjourney.store.entities.Product;
import com.restlearningjourney.store.mappers.ProductMapper;
import com.restlearningjourney.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @GetMapping()
    public List<ProductDto> getAllProducts
            (@RequestParam(required = false, name ="categoryId")
             Byte categoryId)
    {
        System.out.println("getAllProducts - categoryId = " + categoryId);
        List<Product> products;
        if (categoryId != null){
            products = productRepository.findProductsByCategoryId(categoryId);
        }else {
            products = productRepository.findAllIncludingCategories();
        }
        return products
                .stream()
                .map(product -> productMapper.toDto(product))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        var product = productRepository.findById(id).orElse(null);
        if (product == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productMapper.toDto(product));
    }
}
