package com.restlearningjourney.store.services;

import com.restlearningjourney.store.entities.PaymentStatus;

public class PaymentResult {

    private Long orderId;
    private PaymentStatus paymentStatus;

    public PaymentResult() {
    }

    public PaymentResult(Long orderId, PaymentStatus paymentStatus) {
        this.orderId = orderId;
        this.paymentStatus = paymentStatus;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
