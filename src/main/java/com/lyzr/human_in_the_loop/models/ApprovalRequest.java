package com.lyzr.human_in_the_loop.models;

import com.lyzr.human_in_the_loop.enums.ApprovalChannel;
import com.lyzr.human_in_the_loop.enums.Priority;
import com.lyzr.human_in_the_loop.enums.ApprovalStatus;
import lombok.Data;

@Data
public class ApprovalRequest {

    private String sessionId;
    private String correlationId;
    private String title;
    private String description;
    private Priority priority = Priority.MEDIUM;
    private ApprovalChannel approverChannel;
    private Object checkpointData;
    private Object compensationAction;
    private String approverId;
}
