package com.lyzr.human_in_the_loop.db.entity;

import com.lyzr.human_in_the_loop.db.converter.ObjectConverter;
import com.lyzr.human_in_the_loop.enums.ApprovalChannel;
import com.lyzr.human_in_the_loop.enums.Priority;
import com.lyzr.human_in_the_loop.enums.ApprovalStatus;
import com.lyzr.human_in_the_loop.models.ApprovalRequest;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Table(name = "approval_requests")
public class TaskApprovalDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "task_id", columnDefinition = "BINARY(16)")
    private UUID taskId;
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;
    @Column(name = "workflow_id", nullable = false)
    private String workflowId;
    @Column(name = "session_id", nullable = false)
    private String sessionId;
    @Column(name = "requester_service")
    private String requesterService;
    @Column(nullable = false)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') DEFAULT 'MEDIUM'")
    private Priority priority;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED') DEFAULT 'PENDING'")
    private ApprovalStatus status = ApprovalStatus.PENDING;
    @Column(name = "expires_at")
    private Date expiresAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "approver_channel")
    private ApprovalChannel approverChannel;
    @Column(name = "correlation_id")
    private String correlationId;
    // Details for workflow approval
    @Column(name = "approver_id")
    private String approverId;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response_data", columnDefinition = "JSON")
    @Convert(converter = ObjectConverter.class)
    private Object responseData;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response_metadata", columnDefinition = "JSON")
    @Convert(converter = ObjectConverter.class)
    private Object responseMetadata;
    // Details for checkpoint snapshot for restarting the workflow
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "checkpoint_data", columnDefinition = "JSON")
    @Convert(converter = ObjectConverter.class)
    private Object checkpointData;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "compensation_action", columnDefinition = "JSON")
    @Convert(converter = ObjectConverter.class)
    private Object compensationAction;
    @Column(name = "created_at", insertable = false, nullable = false)
    private Date createdAt;
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @PrePersist
    private void prePersist() {
        createdAt = new Date();
        updatedAt = new Date();
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = new Date();
    }

    public TaskApprovalDetails(String tenantId, String requesterService, String workflowId,
                               Date expiresAt, ApprovalRequest request) {
        this.tenantId = tenantId;
        this.requesterService = requesterService;
        this.workflowId = workflowId;
        this.sessionId = request.getSessionId();
        this.correlationId = request.getCorrelationId();
        this.title = request.getTitle();
        this.description = request.getDescription();
        this.priority = request.getPriority();
        this.expiresAt = expiresAt;
        this.approverChannel = request.getApproverChannel();
        this.checkpointData = request.getCheckpointData();
        this.compensationAction = request.getCompensationAction();
        this.approverId = request.getApproverId();
    }
}