package com.lyzr.human_in_the_loop.db.entity;

import com.lyzr.human_in_the_loop.db.converter.ObjectConverter;
import com.lyzr.human_in_the_loop.models.EventTrackingRequest;
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
@Table(name = "events")
public class EventDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;
    @Column(name = "workflow_id", nullable = false)
    private String workflowId;
    @Column(name = "session_id", nullable = false)
    private String sessionId;
    @Column(name = "sequence_number", columnDefinition = "BIGINT AUTO_INCREMENT")
    private Long sequenceNumber;
    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;
    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = ObjectConverter.class)
    @Column(nullable = false, columnDefinition = "JSON")
    private Object payload;
    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = ObjectConverter.class)
    @Column(columnDefinition = "JSON")
    private Object metadata;
    @Column(name = "correlation_id")
    private String correlationId;
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Date createdAt;

    public EventDetails(String tenantId, String workflowId, String sessionId, EventTrackingRequest request) {
        this.tenantId = tenantId;
        this.workflowId = workflowId;
        this.sessionId = sessionId;
        this.eventType = request.getEventType();
        this.payload = request.getPayload();
        this.metadata = request.getMetaData();
        this.correlationId = request.getCorrelationId();
    }
}