package com.restlearningjourney.store.services;

import com.restlearningjourney.store.dtos.CheckoutRequest;
import com.restlearningjourney.store.dtos.CheckoutResponse;
import com.restlearningjourney.store.entities.Cart;
import com.restlearningjourney.store.entities.Order;
import com.restlearningjourney.store.exceptions.CartEmptyException;
import com.restlearningjourney.store.exceptions.CartNotFoundException;
import com.restlearningjourney.store.exceptions.PaymentException;
import com.restlearningjourney.store.repositories.CartRepository;
import com.restlearningjourney.store.repositories.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CheckoutService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final AuthService authService;
    private final PaymentGateway paymentGateway;

    public CheckoutService(CartRepository cartRepository, CartService cartService, AuthService authService, OrderRepository orderRepository, PaymentGateway paymentGateway) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.authService = authService;
        this.paymentGateway = paymentGateway;
    }

    @Transactional
    public CheckoutResponse checkout(CheckoutRequest checkoutRequest)  {

        Cart cart = cartRepository.getCartWithItems(checkoutRequest.getCartId()).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }
        if (cart.isEmpty()) {
            throw new CartEmptyException();
        }
        Order order = Order.fromCart(cart, authService.getCurrentUser());
        orderRepository.save(order);
        try {
            CheckoutSession session = paymentGateway.createCheckoutSession(order);
            System.out.println("Order created " + order);
            CheckoutResponse checkoutResponse = new CheckoutResponse();
            checkoutResponse.setId(order.getId());
            checkoutResponse.setCheckoutUrl(session.getCheckoutUrl());

            cartService.clearCart(cart.getId());

            return checkoutResponse;
        }catch (PaymentException ex){
            System.out.println(ex.getMessage());
            orderRepository.delete(order);
            throw ex;
        }
    }


    public void handleWebhookEvent(WebhookRequest request) {
        paymentGateway
                .parseWebhookRequest(request)
                .ifPresent(paymentResult -> {
                    Order order = orderRepository.findById(paymentResult.getOrderId()).orElseThrow();
                    order.setStatus(paymentResult.getPaymentStatus());
                    orderRepository.save(order);
                });
    }
}
