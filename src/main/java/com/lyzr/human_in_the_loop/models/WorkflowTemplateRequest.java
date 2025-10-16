package com.lyzr.human_in_the_loop.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lyzr.human_in_the_loop.enums.ApprovalChannel;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkflowTemplateRequest {

    private String workflowId;
    private String name;
    private String description;
    @JsonProperty("channelType")
    private ApprovalChannel channel;
    private Object slackData;
    private String htmlTemplate;
}
