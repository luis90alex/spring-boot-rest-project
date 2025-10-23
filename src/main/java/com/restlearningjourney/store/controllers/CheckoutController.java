package com.restlearningjourney.store.controllers;

import com.restlearningjourney.store.dtos.CheckoutRequest;
import com.restlearningjourney.store.dtos.CheckoutResponse;
import com.restlearningjourney.store.dtos.ErrorDto;
import com.restlearningjourney.store.exceptions.CartEmptyException;
import com.restlearningjourney.store.exceptions.CartNotFoundException;
import com.restlearningjourney.store.exceptions.OrderNotFoundException;
import com.restlearningjourney.store.services.CheckoutService;
import com.restlearningjourney.store.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/checkout")
public class CheckoutController {

    private final OrderService orderService;
    private final CheckoutService checkoutService;

    public CheckoutController(OrderService orderService, CheckoutService checkoutService) {
        this.orderService = orderService;
        this.checkoutService = checkoutService;
    }

    @PostMapping
    public CheckoutResponse checkout(
            @RequestBody @Valid CheckoutRequest request) {
        System.out.println("OrderController:checkout");
        System.out.println("CartId = " + request.getCartId());
        return checkoutService.checkout(request);
    }

    @ExceptionHandler({CartNotFoundException.class, CartEmptyException.class})
    public ResponseEntity<ErrorDto> handleCarNotFound(Exception ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorDto(ex.getMessage())
        );
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorDto> handleOrderNotFound(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorDto("Order not found")
        );
    }
}
