package com.lyzr.human_in_the_loop.controller;

import com.lyzr.human_in_the_loop.models.EventResponse;
import com.lyzr.human_in_the_loop.models.EventTrackingRequest;
import com.lyzr.human_in_the_loop.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Operation(summary = "Store event",
            description = "Store an event in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event stored successfully")
    })
    @PostMapping("/{workflow_id}/tracking/{session_id}")
    public void storeEvent(@RequestHeader("tenant-id") String tenantId,
                           @PathVariable("workflow_id") String workflowId,
                           @PathVariable("session_id") String sessionId,
                           @RequestBody EventTrackingRequest trackingRequest) {

        eventService.storeEvent(tenantId, workflowId, sessionId, trackingRequest);
    }

    @Operation(summary = "Get events",
            description = "Get events for a specific workflow and session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Events fetched successfully")
    })
    @GetMapping("/{workflow_id}/sessions/{session_id}")
    public EventResponse getEvents(
            @RequestHeader("tenant-id") String tenantId,
            @PathVariable("workflow_id") String workflowId,
            @PathVariable("session_id") String sessionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("Fetching events for tenantId: {}, workflowId: {}, sessionId: {}, page: {}, size: {}",
                tenantId, workflowId, sessionId, page, size);

        return eventService.getEventsByPagination(tenantId, workflowId, sessionId, page, size);
    }
}
