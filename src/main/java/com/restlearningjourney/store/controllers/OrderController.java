package com.restlearningjourney.store.controllers;

import com.restlearningjourney.store.dtos.OrderDto;
import com.restlearningjourney.store.dtos.RegisterOrderDto;
import com.restlearningjourney.store.dtos.RegisterOrderRequest;
import com.restlearningjourney.store.entities.User;
import com.restlearningjourney.store.exceptions.CartNotFoundException;
import com.restlearningjourney.store.exceptions.OrderNotFoundException;
import com.restlearningjourney.store.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<RegisterOrderDto> checkout(@RequestBody RegisterOrderRequest request) {
        System.out.println("OrderController:checkout");
        System.out.println("CartId = " + request.getCartId());
        RegisterOrderDto registerOrderDto = orderService.checkout(request.getCartId());

        return ResponseEntity.ok(registerOrderDto);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        System.out.println("OrderController:getAllOrders");
        List<OrderDto> orderDtos =  orderService.getAllOrders();
        return ResponseEntity.ok(orderDtos);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderDto> getOrder(
            @PathVariable("orderId") Long orderId) {
        System.out.println("OrderController:getOrder");
        OrderDto orderDto = orderService.getOrderById(orderId);
        return ResponseEntity.ok(orderDto);
    }

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCarNotFound(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("error", "Cart not found or empty")
        );
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleOrderNotFound(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("error", "order not found")
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
          Map.of("error", "Bad credentials")
        );
    }
}
