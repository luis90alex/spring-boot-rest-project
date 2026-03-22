package com.restlearningjourney.store.common.kafka;

import java.math.BigDecimal;

public record OrderCreatedEvent(
        String eventId,
        String orderId,
        BigDecimal amount
) {}