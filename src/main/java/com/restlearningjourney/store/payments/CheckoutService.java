package com.restlearningjourney.store.payments;

import com.restlearningjourney.store.carts.Cart;
import com.restlearningjourney.store.common.kafka.KafkaProducerService;
import com.restlearningjourney.store.common.kafka.OrderCreatedEvent;
import com.restlearningjourney.store.orders.Order;
import com.restlearningjourney.store.carts.CartEmptyException;
import com.restlearningjourney.store.carts.CartNotFoundException;
import com.restlearningjourney.store.carts.CartRepository;
import com.restlearningjourney.store.orders.OrderRepository;
import com.restlearningjourney.store.auth.AuthService;
import com.restlearningjourney.store.carts.CartService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CheckoutService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final AuthService authService;
    private final PaymentGateway paymentGateway;
    private final KafkaProducerService kafkaProducerService;
    // Timer to create our own metrics using Micrometer
    private final Timer checkoutProcessingTimer;

    private static final Logger logger = LoggerFactory.getLogger(CheckoutService.class);


    public CheckoutService(CartRepository cartRepository, CartService cartService, AuthService authService, OrderRepository orderRepository, PaymentGateway paymentGateway, KafkaProducerService kafkaProducerService, MeterRegistry meterRegistry) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.authService = authService;
        this.paymentGateway = paymentGateway;
        this.kafkaProducerService = kafkaProducerService;
        // metric checkout.processing.time is defined with percentiles and histogram
        this.checkoutProcessingTimer = Timer.builder("checkout.processing.time")
                .description("Time spent processing checkout requests")
                .publishPercentiles(0.5,0.95)
                .publishPercentileHistogram()
                .register(meterRegistry);
    }

    @Transactional
    public CheckoutResponse checkout(CheckoutRequest checkoutRequest)  {

        //  Timer used to evaluate checkout process
        return checkoutProcessingTimer.record(() -> {
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
                logger.info("Order created order = {}" , order);
                CheckoutResponse checkoutResponse = new CheckoutResponse();
                checkoutResponse.setId(order.getId());
                checkoutResponse.setCheckoutUrl(session.getCheckoutUrl());

                cartService.clearCart(cart.getId());

                // Event is sent after order is created. Possible DB inconsistency
                // if the event is sent but the db does a rollback. Possible refactor
                OrderCreatedEvent event = new OrderCreatedEvent(
                        UUID.randomUUID().toString(),
                        order.getId().toString(),
                        order.getTotalPrice()
                );
                kafkaProducerService.sendOrderEvent(event);

                return checkoutResponse;
            }catch (PaymentException ex){
                logger.info(ex.getMessage());
                orderRepository.delete(order);
                throw ex;
            }
        });
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
