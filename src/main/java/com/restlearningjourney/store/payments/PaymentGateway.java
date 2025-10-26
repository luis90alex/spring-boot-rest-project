package com.restlearningjourney.store.payments;

import com.restlearningjourney.store.entities.Order;

import java.util.Optional;

public interface PaymentGateway {
    CheckoutSession createCheckoutSession (Order order);
    Optional<PaymentResult> parseWebhookRequest(WebhookRequest request);
}
