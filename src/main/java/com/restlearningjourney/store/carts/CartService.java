package com.restlearningjourney.store.carts;


import com.restlearningjourney.store.products.ProductNotFoundException;
import com.restlearningjourney.store.products.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class CartService {

    private final ProductRepository productRepository;
    private CartMapper cartMapper;
    private CartRepository cartRepository;

    public CartDto createCart() {
        var cart = new Cart();
        cartRepository.save(cart);
        return  cartMapper.toDto(cart);
    }

    public CartItemDto addToCart(UUID cartId, Long productId) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }
        var product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            throw new ProductNotFoundException();
        }
        var cartItem = cart.addItem(product);

        System.out.println("Before saving CartItem Object");
        System.out.println(cartItem);

        cartRepository.save(cart);
        System.out.println("After saving Cart Object");
        System.out.println(cartItem);
        var cartItemDto = cartMapper.toDto(cartItem);
        System.out.println(cartItemDto);
        return cartItemDto;
    }

    public CartDto getCart(UUID cartId) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }
        var cartDto = cartMapper.toDto(cart);
        System.out.println(cartDto);
        return cartDto;
    }

    public CartItemDto updateCart(UUID cartId, Long productId, Integer quantity) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if(cart == null){
            throw new CartNotFoundException();
        }
        var cartItem = cart.getItem(productId);
        if(cartItem == null){
            throw new ProductNotFoundException();
        }
        cartItem.setQuantity(quantity);
        cartRepository.save(cart);
        return cartMapper.toDto(cartItem);
    }

    public void deleteCartItem(UUID cartId, Long productId) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if(cart == null){
            throw new CartNotFoundException();
        }
        cart.removeItem(productId);
        cartRepository.save(cart);
        System.out.println("After deleting CartItem Object");
        System.out.println(cart);
    }

    public void clearCart(UUID cartId) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if(cart == null){
            throw new CartNotFoundException();
        }
        cart.clear();
        cartRepository.save(cart);
    }
}
