package com.restlearningjourney.store.controllers;

import com.restlearningjourney.store.dtos.OrderDto;
import com.restlearningjourney.store.dtos.RegisterOrderDto;
import com.restlearningjourney.store.exceptions.CartNotFoundException;
import com.restlearningjourney.store.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderDto> checkout(@RequestBody RegisterOrderDto request) {
        System.out.println("OrderController:checkout");
        System.out.println("CartId = " + request.getCartId());
        OrderDto orderDto = orderService.checkout(request.getCartId());

        return ResponseEntity.ok(orderDto);
    }
    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCarNotFound(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("error", "Cart not found or empty")
        );
    }
}
