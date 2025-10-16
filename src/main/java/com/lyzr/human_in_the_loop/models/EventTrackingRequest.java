package com.lyzr.human_in_the_loop.models;

import lombok.Data;

@Data
public class EventTrackingRequest {

    private String correlationId;
    private String eventType;
    private Object payload;
    private Object metaData;
}
