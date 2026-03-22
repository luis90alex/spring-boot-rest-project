package com.restlearningjourney.store.common.kafka;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "processed_events")
public class ProcessedEvent {
    @Id
    @Column(name = "event_id")
    private String eventId;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    protected ProcessedEvent() {}

    public ProcessedEvent(String eventId, LocalDateTime processedAt) {
        this.eventId = eventId;
        this.processedAt = processedAt;
    }

    public String getEventId() {
        return eventId;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
}
