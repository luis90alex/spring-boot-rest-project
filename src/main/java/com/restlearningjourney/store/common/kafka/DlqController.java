package com.restlearningjourney.store.common.kafka;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dlq")
public class DlqController {

    private final DlqReprocessService dlqReprocessService;

    public DlqController(DlqReprocessService dlqReprocessService) {
        this.dlqReprocessService = dlqReprocessService;
    }

    @PostMapping("/reprocess")
    public ResponseEntity<Void> reprocess(@RequestBody OrderCreatedEvent event) {
        dlqReprocessService.resendToMainTopic(event);
        return ResponseEntity.ok().build();
    }
}