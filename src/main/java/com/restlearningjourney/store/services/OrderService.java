package com.restlearningjourney.store.services;


import com.restlearningjourney.store.dtos.OrderDto;
import com.restlearningjourney.store.dtos.RegisterOrderDto;
import com.restlearningjourney.store.entities.*;
import com.restlearningjourney.store.exceptions.CartNotFoundException;
import com.restlearningjourney.store.exceptions.OrderNotFoundException;
import com.restlearningjourney.store.mappers.OrderMapper;
import com.restlearningjourney.store.repositories.CartRepository;
import com.restlearningjourney.store.repositories.OrderRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserService userService;

    public OrderService(CartRepository cartRepository, OrderRepository orderRepository, OrderMapper orderMapper, UserService userService) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.userService = userService;
    }

    public RegisterOrderDto createOrder(Cart cart) {
        User user = userService.getCurrentUser();

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
           order.getItems().add(orderItem);
        });
        orderRepository.save(order);

        System.out.println("Order created " + order);
        RegisterOrderDto registerOrderDto = new RegisterOrderDto();
        registerOrderDto.setId(order.getId());

        return registerOrderDto;
    }
    public RegisterOrderDto checkout(UUID cartID) {

        Cart cart = cartRepository.findById(cartID).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }
        if (cart.getItems().isEmpty()) {
            throw new CartNotFoundException();
        }
        RegisterOrderDto registerOrderDto = createOrder(cart);

        System.out.println("OrderController:checkout clearing cart");
        System.out.println("Before clearing " +  cart);
        cart.clear();
        cartRepository.save(cart);
        System.out.println("After clearing " +  cart);
        return registerOrderDto;
    }


    public List<OrderDto> getAllOrders() {
        System.out.println("OrderController:getAllOrders");
        User user = userService.getCurrentUser();
        List<Order> orders = orderRepository.findOrdersByCustomerId(user.getId());
        System.out.println(orders.getFirst());
        List<OrderDto> orderDtos = new ArrayList<>();
        orders.forEach(order ->
        {
            OrderDto orderDto = orderMapper.fromOrderToDto(order);
            System.out.println(orderDto);
            orderDtos.add(orderDto);
        });
        return orderDtos;
    }

    public OrderDto getOrderById(Long id) {
        Order  order = orderRepository.findById(id).orElse(null);
        if(order == null) {
            throw new OrderNotFoundException();
        }
        User user = userService.getCurrentUser();
        if (!order.getCustomer().getId().equals(user.getId())) {
            throw new BadCredentialsException("Id does not belong to the current user");
        }
        return orderMapper.fromOrderToDto(order);
    }
}
