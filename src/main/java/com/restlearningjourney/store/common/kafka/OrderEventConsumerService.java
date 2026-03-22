package com.restlearningjourney.store.common.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class OrderEventConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventConsumerService.class);
    private final ProcessedEventRepository processedEventRepository;

    public OrderEventConsumerService(ProcessedEventRepository processedEventRepository) {
        this.processedEventRepository = processedEventRepository;
    }

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 2000, multiplier = 1.5, maxDelay = 10000),
            dltTopicSuffix = ".DLQ"
    )
    @KafkaListener(
            topics = "${app.kafka.topics.order}",
            groupId = "group-0",
            containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void listener1(OrderCreatedEvent event,
                          @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                          @Header(KafkaHeaders.OFFSET) long offset) {

        if (processedEventRepository.existsById(event.eventId())) {
            logger.info("Event 1 already processed: {}", event.eventId());
            return;
        }

        logger.info("Listener 1 received order.created: orderId={} amount={} partition={} offset={}",
                event.orderId(), event.amount(), partition, offset);

        processedEventRepository.save(new ProcessedEvent(event.eventId(), LocalDateTime.now()));
    }

    @DltHandler
    public void handleDlt(OrderCreatedEvent event) {
        logger.error("DLQ received message: eventId={} orderId={}", event.eventId(), event.orderId());
    }
}