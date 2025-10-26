package com.restlearningjourney.store.payments;

import java.util.Map;

public class WebhookRequest {

    private Map<String, String> headers;
    private String payload;

    public WebhookRequest() {
    }

    public WebhookRequest(Map<String, String> headers, String payload) {
        this.headers = headers;
        this.payload = payload;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
    public String getPayload() {
        return payload;
    }
    public void setPayload(String payload) {
        this.payload = payload;
    }
}
