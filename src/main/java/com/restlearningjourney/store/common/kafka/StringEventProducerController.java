package com.restlearningjourney.store.common.kafka;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/producer")
public class StringEventProducerController {

    private final KakfaProducerService kakfaProducerService;

    public StringEventProducerController(KakfaProducerService kakfaProducerService) {
        this.kakfaProducerService = kakfaProducerService;
    }

    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody OrderCreatedEvent event) {
        kakfaProducerService.sendOrderEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
