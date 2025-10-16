package com.lyzr.human_in_the_loop.service;

import com.lyzr.human_in_the_loop.client.aws.sqs.AwsSqsClient;
import com.lyzr.human_in_the_loop.db.entity.TaskApprovalDetails;
import com.lyzr.human_in_the_loop.db.entity.WorkflowTemplateDetails;
import com.lyzr.human_in_the_loop.db.models.TemplateData;
import com.lyzr.human_in_the_loop.db.repository.TaskApprovalRepository;
import com.lyzr.human_in_the_loop.db.repository.WorkflowTemplateRepository;
import com.lyzr.human_in_the_loop.enums.ApprovalStatus;
import com.lyzr.human_in_the_loop.enums.CommunicationType;
import com.lyzr.human_in_the_loop.models.ApprovalRequest;
import com.lyzr.human_in_the_loop.client.aws.sqs.models.CommunicationRequest;
import com.lyzr.human_in_the_loop.models.WorkflowTemplateRequest;
import com.lyzr.human_in_the_loop.models.WorkflowTemplateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowService {

    @Value("${spring.cloud.aws.sqs.name.communication.trigger}")
    private String communicationQueueName;

    @Autowired
    private WorkflowTemplateRepository workflowTemplateRepository;
    @Autowired
    private TaskApprovalRepository taskApprovalRepository;
    @Autowired
    private AwsSqsClient awsSqsClient;

    public void approvalRequiredFor(String tenantId, String requesterService, String workflowId,
                                    ApprovalRequest request) {

        Date expiresAt = DateUtils.addHours(new Date(), 1);
        TaskApprovalDetails taskApprovalDetails = new TaskApprovalDetails(
                tenantId, requesterService, workflowId, expiresAt, request);
        taskApprovalDetails = taskApprovalRepository.save(taskApprovalDetails);
        awsSqsClient.pushEventToSqs(
                communicationQueueName,
                new CommunicationRequest(
                        taskApprovalDetails.getCorrelationId(),
                        taskApprovalDetails.getTaskId().toString(),
                        CommunicationType.APPROVAL
                )
        );
    }

    public TaskApprovalDetails getTaskApprovalDetails(String taskId) {
        return taskApprovalRepository.getReferenceById(UUID.fromString(taskId));
    }

    public void workflowApprovalUpdateFor(String workflowId, String taskId, String correlationId, ApprovalStatus status) {
        TaskApprovalDetails taskApprovalDetails = taskApprovalRepository.findByWorkflowIdAndTaskId(
                workflowId, UUID.fromString(taskId));
        if (taskApprovalDetails == null) {
            log.error("Task approval not found for workflow id: {} and task id: {}", workflowId, taskId);
            return;
        }
        if (taskApprovalDetails.getExpiresAt().before(new Date())) {
            // Send communication to inform user that the task approval has expired
            awsSqsClient.pushEventToSqs(
                    communicationQueueName,
                    new CommunicationRequest(
                            correlationId,
                            taskId,
                            CommunicationType.EXPIRY
                    )
            );
            return;
        }
        if (taskApprovalDetails.getStatus() != ApprovalStatus.PENDING) {
            awsSqsClient.pushEventToSqs(
                    communicationQueueName,
                    new CommunicationRequest(
                            correlationId,
                            taskId,
                            CommunicationType.ERROR
                    )
            );
            return;
        }
        updateTaskApprovalDetails(taskApprovalDetails, status);
    }

    protected void updateTaskApprovalDetails(TaskApprovalDetails taskApprovalDetails, ApprovalStatus status) {
        taskApprovalDetails.setStatus(status);
        taskApprovalDetails = taskApprovalRepository.save(taskApprovalDetails);
        log.info("Task approval updated for : {}", taskApprovalDetails);
    }

    public void createWorkflow(String tenantId, WorkflowTemplateRequest request) {
        log.info("Creating workflow template for tenantId : {} -> {}", tenantId, request);
        WorkflowTemplateDetails workflowTemplateDetails = new WorkflowTemplateDetails(
                tenantId, request
        );
        workflowTemplateDetails = workflowTemplateRepository.save(workflowTemplateDetails);
        log.info("Workflow template created for : {}", workflowTemplateDetails);
    }

    public List<WorkflowTemplateResponse> getAllWorkflowTemplates(String tenantId) {
        List<WorkflowTemplateDetails> workflowTemplateDetails = workflowTemplateRepository.findAllByTenantId(tenantId);
        log.info("Workflow templates found for tenantId : {}", tenantId);
        if (CollectionUtils.isEmpty(workflowTemplateDetails)) {
            return new ArrayList<>();
        }
        return workflowTemplateDetails.stream().map(WorkflowTemplateResponse::new).toList();
    }
}
