package com.lyzr.human_in_the_loop.db.repository;

import com.lyzr.human_in_the_loop.db.entity.EventDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<EventDetails, UUID> {
    Page<EventDetails> findByTenantIdAndWorkflowIdAndSessionId(
            String tenantId,
            String workflowId,
            String sessionId,
            Pageable pageable
    );

    long countByTenantIdAndWorkflowIdAndSessionId(
            String tenantId,
            String workflowId,
            String sessionId
    );
}
