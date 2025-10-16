package com.lyzr.human_in_the_loop.db.repository;

import com.lyzr.human_in_the_loop.db.entity.TaskApprovalDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaskApprovalRepository extends JpaRepository<TaskApprovalDetails, UUID> {
    
    /**
     * Internal method that performs the actual query with UUID
     */
    TaskApprovalDetails findByWorkflowIdAndTaskId(String workflowId, UUID id);
}
