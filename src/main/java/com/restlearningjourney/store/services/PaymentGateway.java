package com.restlearningjourney.store.services;

import com.restlearningjourney.store.entities.Order;

public interface PaymentGateway {
    CheckoutSession createCheckoutSession (Order order);
}
