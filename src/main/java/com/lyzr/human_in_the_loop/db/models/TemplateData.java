package com.lyzr.human_in_the_loop.db.models;

import com.lyzr.human_in_the_loop.models.WorkflowTemplateRequest;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TemplateData {
    private String htmlTemplate;
    private Object slackData;

    public TemplateData(WorkflowTemplateRequest request) {
        this.htmlTemplate = request.getHtmlTemplate();
        this.slackData = request.getSlackData();
    }
}
