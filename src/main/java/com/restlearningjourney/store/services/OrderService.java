package com.restlearningjourney.store.services;


import com.restlearningjourney.store.dtos.OrderDto;
import com.restlearningjourney.store.entities.*;
import com.restlearningjourney.store.exceptions.OrderNotFoundException;
import com.restlearningjourney.store.mappers.OrderMapper;
import com.restlearningjourney.store.repositories.OrderRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final AuthService authService;

    public OrderService( OrderRepository orderRepository, OrderMapper orderMapper, AuthService authService) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.authService = authService;
    }

    public List<OrderDto> getAllOrders() {
        System.out.println("OrderController:getAllOrders");
        User user = authService.getCurrentUser();
        List<Order> orders = orderRepository.getOrdersByCustomer(user.getId());
        return orders.stream().map(order -> orderMapper.fromOrderToDto(order)).toList();
    }

    public OrderDto getOrder(Long id) {
        Order  order = orderRepository
                .getOrderWithItems(id)
                .orElseThrow(OrderNotFoundException::new);

        User user = authService.getCurrentUser();
        if (!order.isPlacedBy(user)) {
            throw new AccessDeniedException("Id does not belong to the current user");
        }
        return orderMapper.fromOrderToDto(order);
    }
}
