package com.restlearningjourney.store.services;

import com.restlearningjourney.store.entities.Order;
import com.restlearningjourney.store.entities.OrderItem;
import com.restlearningjourney.store.entities.PaymentStatus;
import com.restlearningjourney.store.exceptions.PaymentException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class StripePaymentGateway implements PaymentGateway {

    @Value("${websiteUrl}")
    private String websiteUrl;

    @Value("${stripe.webhookSecretKey}")
    private String webhookSecretKey;

    public StripePaymentGateway() {
    }

    @Override
    public CheckoutSession createCheckoutSession(Order order) throws PaymentException {
        try{
            //create a checkout session
            SessionCreateParams.Builder builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(websiteUrl + "/checkout-success?orderId=" + order.getId())
                    .setCancelUrl(websiteUrl + "/checkout-cancel")
                    .putMetadata("order_id",order.getId().toString());

            order.getItems().forEach(item -> {
                SessionCreateParams.LineItem lineItem = createLineItem(item);
                builder.addLineItem(lineItem);
            });

            SessionCreateParams sessionCreateParams = builder.build();
            Session session = Session.create(sessionCreateParams);
            return new CheckoutSession(session.getUrl());
        } catch (StripeException ex) {
            System.out.println(ex.getMessage());
            throw new PaymentException() ;
        }
    }

    @Override
    public Optional<PaymentResult> parseWebhookRequest(WebhookRequest request) {
        try {
            System.out.println("StripePaymentGateway:parseWebhookRequest");
            String payload = request.getPayload();
            String signature = request.getHeaders().get("stripe-signature");

            Event event = Webhook.constructEvent(payload, signature,webhookSecretKey);
            System.out.println("handleWebHook eventType : " + event.getType());

            switch (event.getType()) {
                case "payment_intent.succeeded" ->{
                    System.out.println("Accessing Case payment_intent.succeeded");
                    return Optional.of(new PaymentResult(extractOrderId(event), PaymentStatus.PAID));
                }
                case "payment_intent.payment_failed" ->{
                    System.out.println("Accessing Case payment_intent.payment_failed");
                    return Optional.of(new PaymentResult(extractOrderId(event), PaymentStatus.FAILED));
                }
                default -> {
                    System.out.println("Default case");
                    return Optional.empty();
                }
            }

        } catch (SignatureVerificationException e) {
            throw new PaymentException("Invalid signature");
        }
    }

    private Long extractOrderId(Event event) {

        StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElseThrow(
                () -> new PaymentException("Could not deserialize Stripe event. Check the SDK and API version")
        );
        //Depending on the status and event it should be caste to a different object
        // for example if the event = charge --> it should be cast to (Charge)
        // if event= payment_intent.succeeded --> cast to (PaymentObject)
        PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
        String orderId = paymentIntent.getMetadata().get("order_id");
        System.out.println("ExtractOrderId " + orderId);
        return Long.valueOf(orderId);
    }

    private SessionCreateParams.LineItem createLineItem(OrderItem item) {
        return  SessionCreateParams.LineItem.builder()
                .setQuantity(Long.valueOf(item.getQuantity()))
                .setPriceData(createPriceData(item))
                .build();
    }

    private SessionCreateParams.LineItem.PriceData createPriceData(OrderItem item) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("usd")
                .setUnitAmountDecimal(
                        item.getUnitPrice().multiply(BigDecimal.valueOf(100)))
                .setProductData(createProductData(item))
                .build();
    }

    private SessionCreateParams.LineItem.PriceData.ProductData createProductData(OrderItem item) {
        return SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(item.getProduct().getName())
                .build();
    }
}
