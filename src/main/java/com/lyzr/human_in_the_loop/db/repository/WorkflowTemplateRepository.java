package com.lyzr.human_in_the_loop.db.repository;

import com.lyzr.human_in_the_loop.db.entity.WorkflowTemplateDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowTemplateRepository extends JpaRepository<WorkflowTemplateDetails, String> {
    List<WorkflowTemplateDetails> findAllByTenantId(String tenantId);
}
