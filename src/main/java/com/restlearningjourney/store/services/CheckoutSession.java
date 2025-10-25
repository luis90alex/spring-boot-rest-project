package com.restlearningjourney.store.services;

public class CheckoutSession {

    private String checkoutUrl;

    public CheckoutSession(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }
    public String getCheckoutUrl() {
        return checkoutUrl;
    }
    public void setCheckoutUrl(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }
}
