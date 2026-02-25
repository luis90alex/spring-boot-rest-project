package com.restlearningjourney.store.common.kafka;

import java.math.BigDecimal;

public record OrderCreatedEvent(
   String orderId,
   BigDecimal amount
) {}
