package com.restlearningjourney.store.common.kafka;

import com.restlearningjourney.store.orders.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KakfaProducerService {

    KafkaTemplate<String,OrderCreatedEvent> kafkaTemplate;
    @Value("${app.kafka.topics.user}")
    private String topic;
    private static final Logger logger = LoggerFactory.getLogger(KakfaProducerService.class);

    public KakfaProducerService(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderEvent(OrderCreatedEvent event) {
            kafkaTemplate.send(topic, event.orderId(), event).whenComplete((res, ex) -> {
            if (ex != null) {
                logger.error("Error while sending kafka data to topic. Error = {}", ex.getMessage());
            }
            logger.info("Sent event key = {}, partition = {} offset = {} ", event.orderId(),
                    res.getRecordMetadata().partition(), res.getRecordMetadata().offset());
        });
    }
}
