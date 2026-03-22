package com.restlearningjourney.store.common.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    KafkaTemplate<String,OrderCreatedEvent> kafkaTemplate;
    @Value("${app.kafka.topics.order}")
    private String topic;
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    public KafkaProducerService(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderEvent(OrderCreatedEvent event) {
        kafkaTemplate.send(topic, event.orderId(), event)
                .whenComplete((res, ex) -> {
                    if (ex != null) {
                        logger.error("Error sending Kafka event. eventId={}, error={}", event.eventId(), ex.getMessage());
                        return;
                    }
                    logger.info("Sent event eventId={}, orderId={} key={}, partition={}, offset={}",
                            event.eventId(), event.orderId(), event.orderId(),
                            res.getRecordMetadata().partition(),
                            res.getRecordMetadata().offset());
                });
    }
}
