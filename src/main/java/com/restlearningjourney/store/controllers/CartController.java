package com.restlearningjourney.store.controllers;


import com.restlearningjourney.store.dtos.*;
import com.restlearningjourney.store.entities.CartItem;
import com.restlearningjourney.store.mappers.CartItemMapper;
import com.restlearningjourney.store.mappers.CartMapper;
import com.restlearningjourney.store.repositories.CartItemsRepository;
import com.restlearningjourney.store.repositories.CartRepository;
import com.restlearningjourney.store.repositories.ProductRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/carts")
public class CartController {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemsRepository cartItemsRepository;
    private final CartItemMapper cartItemMapper;
    private CartMapper cartMapper;

    @PostMapping
    public ResponseEntity<CartDto> createCart(
            @RequestBody RegisterCartRequest registerCartRequest,
            UriComponentsBuilder uriComponentsBuilder) {
        System.out.println("createCart" + registerCartRequest);
        var cart = cartMapper.toEntity(registerCartRequest);
        if (cart.getDateCreated() == null) {
            cart.setDateCreated(LocalDate.now());
        }
        System.out.println("createCart before saving Cart Object");
        System.out.println(cart);
        cartRepository.save(cart);
        System.out.println("After saving Cart Object");
        System.out.println(cart);
        var cartDto = cartMapper.toDto(cart);
        System.out.println(cartDto);
        var uri =  uriComponentsBuilder.path("/carts/{id}").buildAndExpand(cartDto.getId()).toUri();
        return ResponseEntity.created(uri).body(cartDto);
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<?> addProductToCart(
            @PathVariable(name = "cartId") UUID cartId,
            @RequestBody @Valid AddItemRequest request,
            UriComponentsBuilder uriComponentsBuilder) {
        System.out.println("addProductToCart " + request);
        System.out.println("addProductToCart " + cartId);

        var cart = cartRepository.findById(cartId).orElse(null);
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }
        var product = productRepository.findById(request.getProductId()).orElse(null);
        if (product == null) {
            return ResponseEntity.badRequest().build();
        }
        var cartItem = cartItemsRepository.findByCartIdAndProductId(cartId, product.getId()).orElse(null);
        System.out.println("Before saving CartItem Object");
        System.out.println(cartItem);
        int qtyToAdd = Optional.ofNullable(request.getQuantity()).orElse(1);
        boolean created = false;
        if (cartItem == null) {
            cartItem = CartItem.builder()
                    .product(product)
                    .cart(cart)
                    .quantity(qtyToAdd)
                    .build();
            cart.addCartItem(cartItem);
            created = true;
        } else {
            cartItem.setQuantity(cartItem.getQuantity() +1);
        }
        cartItemsRepository.save(cartItem);
        System.out.println("After saving CartItem Object");
        System.out.println(cartItem);
        var cartItemDto = cartItemMapper.toDto(cartItem);
        System.out.println(cartItemDto);
        if (created) {
            var uri =  uriComponentsBuilder.path("/carts/{cartId}/items/{productId}").buildAndExpand(cartId, request.getProductId()).toUri();
            return ResponseEntity.created(uri).body(cartItemDto);
        }
        return ResponseEntity.ok(cartItemDto);
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<CartDto> getCart(@PathVariable(name = "cartId") UUID cartId){
        var cart = cartRepository.findById(cartId).orElse(null);
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }
        var cartDto = cartMapper.toDto(cart);
        System.out.println(cartDto);
        return ResponseEntity.ok(cartDto);
    }

    @PutMapping("/{cartId}/items/{productId}")
    public ResponseEntity<CartItemDto> updateProduct(
            @PathVariable(name = "cartId") UUID cartId,
            @PathVariable(name = "productId") Long productId,
            @RequestBody @Valid AddItemRequest request){

        System.out.println("updateProduct " + request);
        System.out.println("updateProduct " + cartId);
        System.out.println("updateProduct " + productId);
        var cart = cartRepository.findById(cartId).orElse(null);
        if(cart == null){
            return ResponseEntity.notFound().build();
        }
        var cartItem = cartItemsRepository.findByCartIdAndProductId(cartId, productId).orElse(null);
        if(cartItem == null){
            return ResponseEntity.notFound().build();
        }
        cartItem.setQuantity(request.getQuantity());
        cartItemsRepository.save(cartItem);
        System.out.println("After saving CartItem Object");
        System.out.println(cartItem);
        return ResponseEntity.ok(cartItemMapper.toDto(cartItem));
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<CartItemDto> deleteCartItem(
            @PathVariable(name = "cartId") UUID cartId,
            @PathVariable(name = "productId") Long productId){

        var cart = cartRepository.findById(cartId).orElse(null);
        if(cart == null){
            return ResponseEntity.notFound().build();
        }
        var cartItem = cartItemsRepository.findByCartIdAndProductId(cartId, productId).orElse(null);
        if(cartItem == null){
            return ResponseEntity.notFound().build();
        }

        cart.removeCartItem(cartItem);
        cartItemsRepository.delete(cartItem);
        System.out.println("After deleting CartItem Object");
        System.out.println(cartItem);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cartId}/items")
    public ResponseEntity<CartDto> deleteCart(
            @PathVariable(name = "cartId")  UUID cartId){
        var cart = cartRepository.findById(cartId).orElse(null);
        if(cart == null){
            return ResponseEntity.notFound().build();
        }
        cartRepository.delete(cart);
        return ResponseEntity.noContent().build();
    }

}
