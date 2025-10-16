package com.lyzr.human_in_the_loop.client.aws.sqs.models;

import com.lyzr.human_in_the_loop.enums.CommunicationType;

public class CommunicationRequest extends QueueRequest {

    private String taskId;
    private CommunicationType type;

    public String getTaskId() {
        return taskId;
    }

    public CommunicationType getType() {
        return type;
    }

    public CommunicationRequest(String correlationId, String taskId, CommunicationType type) {
        super(correlationId);
        this.taskId = taskId;
        this.type = type;
    }
}
