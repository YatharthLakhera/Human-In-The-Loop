package com.lyzr.human_in_the_loop.models;

import com.lyzr.human_in_the_loop.db.entity.WorkflowTemplateDetails;
import com.lyzr.human_in_the_loop.enums.ApprovalChannel;
import lombok.Data;

@Data
public class WorkflowTemplateResponse {
    private String workflowId;
    private String name;
    private String description;
    private ApprovalChannel channel;
    private Object slackData;
    private String htmlTemplate;

    public WorkflowTemplateResponse(WorkflowTemplateDetails templateDetails) {
        this.workflowId = templateDetails.getWorkflowId();
        this.name = templateDetails.getName();
        this.description = templateDetails.getDescription();
        this.channel = templateDetails.getApprovalChannel();
        this.slackData = templateDetails.getTemplateData().getSlackData();
        this.htmlTemplate = templateDetails.getTemplateData().getHtmlTemplate();
    }
}
