package com.restlearningjourney.store.services;

import com.restlearningjourney.store.dtos.CheckoutRequest;
import com.restlearningjourney.store.dtos.CheckoutResponse;
import com.restlearningjourney.store.entities.Cart;
import com.restlearningjourney.store.entities.Order;
import com.restlearningjourney.store.entities.User;
import com.restlearningjourney.store.exceptions.CartEmptyException;
import com.restlearningjourney.store.exceptions.CartNotFoundException;
import com.restlearningjourney.store.repositories.CartRepository;
import com.restlearningjourney.store.repositories.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class CheckoutService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final AuthService authService;

    public CheckoutService(CartRepository cartRepository, CartService cartService, AuthService authService, OrderRepository orderRepository) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.authService = authService;
    }

    public CheckoutResponse checkout(CheckoutRequest checkoutRequest) {

        Cart cart = cartRepository.getCartWithItems(checkoutRequest.getCartId()).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }
        if (cart.isEmpty()) {
            throw new CartEmptyException();
        }
        Order order = Order.fromCart(cart, authService.getCurrentUser());
        orderRepository.save(order);

        System.out.println("Order created " + order);
        CheckoutResponse checkoutResponse = new CheckoutResponse();
        checkoutResponse.setId(order.getId());

        System.out.println("Before clearing " +  cart);
        cartService.clearCart(cart.getId());
        System.out.println("After clearing " +  cart);
        return checkoutResponse;
    }

}
