package com.lyzr.human_in_the_loop.client.aws.sqs.models;

import lombok.Data;

@Data
public class QueueRequest {

    private String correlationId;

    public QueueRequest(String correlationId) {
        this.correlationId = correlationId;
    }
}
