package com.lyzr.human_in_the_loop.service;

import com.lyzr.human_in_the_loop.db.entity.EventDetails;
import com.lyzr.human_in_the_loop.db.repository.EventRepository;
import com.lyzr.human_in_the_loop.models.EventResponse;
import com.lyzr.human_in_the_loop.models.EventTrackingRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public void storeEvent(String tenantId, String workflowId, String sessionId,
                           EventTrackingRequest trackingRequest) {
        EventDetails eventDetails = new EventDetails(tenantId, workflowId, sessionId, trackingRequest);
        eventRepository.save(eventDetails);
        log.info("Event stored successfully for tenantId : {}, workflowId : {}", tenantId, workflowId);
    }

    public EventResponse getEventsByPagination(String tenantId, String workflowId,
                                               String sessionId, int page, int size) {

        // Validate page and size
        page = Math.max(0, page);
        // Limiting page size to 1000
        size = size <= 0 ? 100 : Math.min(size, 1000);
        Pageable pageable = PageRequest.of(page, size, Sort.by("sequenceNumber").ascending());
        // Fetch paginated events
        Page<EventDetails> eventsPage = eventRepository.findByTenantIdAndWorkflowIdAndSessionId(
                tenantId,
                workflowId,
                sessionId,
                pageable
        );
        // Build response
        EventResponse response = new EventResponse(tenantId, workflowId, sessionId);
        response.setTotalEvents(eventsPage.getTotalElements());
        // Add events to response
        eventsPage.getContent().forEach(response::addEvent);
        log.info("Retrieved {} events for tenantId: {}, workflowId: {}, sessionId: {}",
                eventsPage.getNumberOfElements(), tenantId, workflowId, sessionId);
        return response;
    }

}
