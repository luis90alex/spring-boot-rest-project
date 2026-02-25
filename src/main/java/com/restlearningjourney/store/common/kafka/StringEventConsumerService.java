package com.restlearningjourney.store.common.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class StringEventConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(StringEventConsumerService.class);

    @KafkaListener( groupId = "group-0", topics = "${app.kafka.topics.user}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listener1 (OrderCreatedEvent event, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                           @Header(KafkaHeaders.OFFSET) long offset) {
        logger.info("Received 1 order.created: orderId={} amount={} partition={} offset={}",
                event.orderId(), event.amount(), partition, offset);
        logger.info("Listener 1. Receiving a  message: ");
    }

    @KafkaListener( groupId = "group-0", topics = "${app.kafka.topics.user}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listener2 (OrderCreatedEvent event, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                           @Header(KafkaHeaders.OFFSET) long offset) {
        logger.info("Received 2 order.created: orderId={} amount={} partition={} offset={}",
                event.orderId(), event.amount(), partition, offset);
        logger.info("Listener 2. Receiving a  message: ");
    }
}
