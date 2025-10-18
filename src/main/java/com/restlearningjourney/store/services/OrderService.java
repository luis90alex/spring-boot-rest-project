package com.restlearningjourney.store.services;


import com.restlearningjourney.store.dtos.CartDto;
import com.restlearningjourney.store.dtos.OrderDto;
import com.restlearningjourney.store.entities.*;
import com.restlearningjourney.store.exceptions.CartNotFoundException;
import com.restlearningjourney.store.mappers.OrderMapper;
import com.restlearningjourney.store.repositories.CartRepository;
import com.restlearningjourney.store.repositories.OrderRepository;
import com.restlearningjourney.store.repositories.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderService(CartRepository cartRepository, UserRepository userRepository, OrderRepository orderRepository, OrderMapper orderMapper) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    public OrderDto createOrder(Cart cart) {
        User user = getCurrentUser();

        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setTotalPrice(cart.getTotalPrice());
        order.setCustomer(user);

        user.getOrders().add(order);

        cart.getItems().forEach(item -> {
           OrderItem orderItem = orderMapper.fromCartItemToOrderItem(item);
           orderItem.setOrder(order);
           System.out.println(orderItem);
           order.getOrderItems().add(orderItem);
        });
        orderRepository.save(order);

        System.out.println("Order created " + order);
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());

        return orderDto;
    }
    public OrderDto checkout(UUID cartID) {

        Cart cart = cartRepository.findById(cartID).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }
        if (cart.getItems().isEmpty()) {
            throw new CartNotFoundException();
        }
        OrderDto orderDto = createOrder(cart);

        System.out.println("OrderController:checkout clearing cart");
        System.out.println("Before clearing " +  cart);
        cart.clear();
        cartRepository.save(cart);
        System.out.println("After clearing " +  cart);
        return orderDto;
    }

    public User getCurrentUser() {
        Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new BadCredentialsException("Invalid username or password");
        }
        return user;
    }
}
