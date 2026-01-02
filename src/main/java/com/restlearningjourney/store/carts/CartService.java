package com.restlearningjourney.store.carts;


import com.restlearningjourney.store.products.ProductNotFoundException;
import com.restlearningjourney.store.products.ProductRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class CartService {

    private final ProductRepository productRepository;
    private CartMapper cartMapper;
    private CartRepository cartRepository;
    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

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

        logger.info("Before saving CartItem Object");
        logger.info("cartItem = {}", cartItem);

        cartRepository.save(cart);
        logger.info("After saving Cart Object");
        logger.info("cartItem = {}", cartItem);
        var cartItemDto = cartMapper.toDto(cartItem);
        logger.info("cartItemDto = {}",cartItemDto);
        return cartItemDto;
    }

    public CartDto getCart(UUID cartId) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }
        var cartDto = cartMapper.toDto(cart);
        logger.info( "cartDto = {}", cartDto);
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
        logger.info("After deleting CartItem Object");
        logger.info( "cart = {}",cart);
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
