create database lyzr_test_db;
use lyzr_test_db;

CREATE TABLE events (
    id BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    tenant_id VARCHAR(255) NOT NULL,
    workflow_id VARCHAR(255) NOT NULL,
    session_id VARCHAR(255) NOT NULL,
    sequence_number BIGINT NOT NULL AUTO_INCREMENT,
    event_type VARCHAR(100) NOT NULL,
    payload JSON NOT NULL,
    metadata JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    correlation_id VARCHAR(255),
    UNIQUE(workflow_id, sequence_number),
    KEY `idx_events_sequence` (sequence_number),  -- Add this line
    KEY `idx_events_correlation` (correlation_id)
);

CREATE TABLE approval_requests (
    task_id BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    tenant_id VARCHAR(255) NOT NULL,
    workflow_id VARCHAR(255) NOT NULL,
    session_id VARCHAR(255) NOT NULL,
    correlation_id VARCHAR(255),
    requester_service VARCHAR(100), -- which agent/service requested the approval
    -- Approval metadata
    title TEXT NOT NULL,
    description TEXT,
    priority enum('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') DEFAULT 'medium',
    -- State management
    status enum('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED') DEFAULT 'PENDING',
    expires_at TIMESTAMP,
    -- Approver info
    approver_id VARCHAR(255), -- userId, email, role, team, etc
    approver_channel VARCHAR(50), -- slack, email, api
    -- Response data
    response_data JSON, -- any approval related information
    response_metadata JSON, -- api headers, slack meta, ip details, etc
    -- Rollback support
    checkpoint_data JSON, -- Snapshot for rollback
    compensation_action JSON, -- How to undo
    -- Audit
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    KEY `idx_approval_task_workflow` (task_id, workflow_id)
);

create table workflow_templates (
    workflow_id VARCHAR(255) PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    approval_channel VARCHAR(50) NOT NULL,
    template_data JSON,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);