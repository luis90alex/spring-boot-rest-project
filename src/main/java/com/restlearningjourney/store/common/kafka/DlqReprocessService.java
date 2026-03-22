package com.restlearningjourney.store.common.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class DlqReprocessService {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(DlqReprocessService.class);

    @Value("${app.kafka.topics.order}")
    private String mainTopic;

    public DlqReprocessService(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void resendToMainTopic(OrderCreatedEvent event) {
        logger.info("resendToMainTopic: eventId={} orderId={}", event.eventId(), event.orderId());
        kafkaTemplate.send(mainTopic, event.orderId(), event);
    }
}