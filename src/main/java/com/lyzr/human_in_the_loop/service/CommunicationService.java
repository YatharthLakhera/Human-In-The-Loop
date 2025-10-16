package com.lyzr.human_in_the_loop.service;

import com.lyzr.human_in_the_loop.client.aws.sqs.models.CommunicationRequest;
import com.lyzr.human_in_the_loop.db.entity.TaskApprovalDetails;
import com.lyzr.human_in_the_loop.enums.ApprovalStatus;
import com.lyzr.human_in_the_loop.service.communication.MailingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunicationService {

    @Autowired
    private MailingService mailingService;

    @Autowired
    private WorkflowService workflowService;

    @Transactional(readOnly = true)
    public void sendCommunicationFor(CommunicationRequest communicationRequest) {
        TaskApprovalDetails approvalDetails = workflowService.getTaskApprovalDetails(communicationRequest.getTaskId());
        if (approvalDetails == null) {
            log.error("Task approval not found for task id: {}", communicationRequest.getTaskId());
            return;
        }
        String email = approvalDetails.getApproverId();
        if (StringUtils.isBlank(email)) {
            log.error("Approver email is blank for task id: {}", communicationRequest.getTaskId());
            workflowService.updateTaskApprovalDetails(approvalDetails, ApprovalStatus.FAILED);
            return;
        }
        log.info("Sending {} for task id: {}", communicationRequest.getType(), communicationRequest.getTaskId());
        switch (communicationRequest.getType()) {
            case APPROVAL:
                mailingService.sendCommunicationForApproval(
                        email,
                        email.split("@")[0],
                        approvalDetails
                );
                break;
            case EXPIRY:
                log.error("Cancelling expired task: {}", communicationRequest.getTaskId());
                workflowService.updateTaskApprovalDetails(approvalDetails, ApprovalStatus.CANCELLED);
                break;
            case ERROR:
                log.error("Error executing task: {}", communicationRequest.getTaskId());
                workflowService.updateTaskApprovalDetails(approvalDetails, ApprovalStatus.FAILED);
                break;
            default:
                log.error("Invalid communication type: {}", communicationRequest.getType());
                workflowService.updateTaskApprovalDetails(approvalDetails, ApprovalStatus.FAILED);
                break;
        }
    }
}
