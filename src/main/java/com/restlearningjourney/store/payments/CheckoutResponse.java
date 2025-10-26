package com.restlearningjourney.store.payments;


import com.fasterxml.jackson.annotation.JsonProperty;

public class CheckoutResponse {
    @JsonProperty("orderId")
    private Long id;
    private String checkoutUrl;


    public String getCheckoutUrl() {
        return checkoutUrl;
    }

    public void setCheckoutUrl(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }

    public CheckoutResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
