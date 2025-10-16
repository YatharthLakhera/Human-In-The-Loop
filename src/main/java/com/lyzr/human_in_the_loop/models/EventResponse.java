package com.lyzr.human_in_the_loop.models;

import com.lyzr.human_in_the_loop.db.entity.EventDetails;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class EventResponse {

    private String tenantId;
    private String workflowId;
    private String sessionId;
    private long totalEvents;
    private List<Event> events;

    public EventResponse(String tenantId, String workflowId, String sessionId) {
        this.tenantId = tenantId;
        this.workflowId = workflowId;
        this.sessionId = sessionId;
        this.events = new ArrayList<>();
    }

    public void addEvent(EventDetails eventDetails) {
        this.events.add(new Event(eventDetails));
    }

    @Data
    public static class Event {
        private String eventId;
        private String eventType;
        private Object eventPayload;
        private Object eventMetadata;
        private Long sequenceNumber;
        private String correlationId;

        public Event(EventDetails eventDetails) {
            this.eventId = eventDetails.getId().toString();
            this.eventType = eventDetails.getEventType();
            this.eventPayload = eventDetails.getPayload();
            this.eventMetadata = eventDetails.getMetadata();
            this.sequenceNumber = eventDetails.getSequenceNumber();
            this.correlationId = eventDetails.getCorrelationId();
        }
    }
}
