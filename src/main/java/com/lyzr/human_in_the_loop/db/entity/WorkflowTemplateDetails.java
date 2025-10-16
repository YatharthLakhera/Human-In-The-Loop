package com.lyzr.human_in_the_loop.db.entity;

import com.lyzr.human_in_the_loop.db.converter.TemplateDataConverter;
import com.lyzr.human_in_the_loop.db.models.TemplateData;
import com.lyzr.human_in_the_loop.enums.ApprovalChannel;
import com.lyzr.human_in_the_loop.models.WorkflowTemplateRequest;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Entity
@NoArgsConstructor
@Table(name = "workflow_templates")
public class WorkflowTemplateDetails {

    @Id
    @Column(name = "workflow_id")
    private String workflowId;
    @Column(name = "tenant_id")
    private String tenantId;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(name = "approval_channel")
    private ApprovalChannel approvalChannel;
    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = TemplateDataConverter.class)
    @Column(name = "template_data")
    private TemplateData templateData;

    public WorkflowTemplateDetails(String tenantId, WorkflowTemplateRequest request) {
        this.tenantId = tenantId;
        this.workflowId = request.getWorkflowId();
        this.name = request.getName();
        this.description = request.getDescription();
        this.approvalChannel = request.getChannel();
        this.templateData = new TemplateData(request);
    }
}
