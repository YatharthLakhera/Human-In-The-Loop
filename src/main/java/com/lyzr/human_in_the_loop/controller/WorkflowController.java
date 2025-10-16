package com.lyzr.human_in_the_loop.controller;

import com.lyzr.human_in_the_loop.enums.ApprovalStatus;
import com.lyzr.human_in_the_loop.models.ApprovalRequest;
import com.lyzr.human_in_the_loop.models.WorkflowTemplateRequest;
import com.lyzr.human_in_the_loop.models.WorkflowTemplateResponse;
import com.lyzr.human_in_the_loop.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

@RestController
@RequestMapping("/v1/workflow")
public class WorkflowController {

    @Autowired
    private WorkflowService workflowService;

    @Operation(summary = "Create workflow template",
            description = "Create a new workflow template")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workflow template created successfully")
    })
    @PostMapping("/template")
    public void createWorkflow(@RequestHeader("tenant-id") String tenantId,
                               @RequestBody WorkflowTemplateRequest request) {
        workflowService.createWorkflow(tenantId, request);
    }

    @Operation(summary = "Get all workflow templates",
            description = "Get all workflow templates for a specific tenant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workflow templates fetched successfully")
    })
    @GetMapping("/templates")
    public List<WorkflowTemplateResponse> getAllWorkflowTemplates(@RequestHeader("tenant-id") String tenantId) {
        return workflowService.getAllWorkflowTemplates(tenantId);
    }

    @Operation(summary = "Workflow Paused as approval is required",
            description = "Pause a workflow as approval is required")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workflow checkpoint stored successfully")
    })
    @PostMapping("/{workflow_id}/pause")
    public void approvalRequiredFor(@RequestHeader("tenant-id") String tenantId,
                                    @RequestHeader("requester-service") String requesterService,
                                    @PathVariable("workflow_id") String workflowId,
                                    @RequestBody ApprovalRequest approvalRequest) {

        workflowService.approvalRequiredFor(tenantId, requesterService, workflowId, approvalRequest);
    }

    @Operation(summary = "Approve task",
            description = "Approve a specific task in a workflow")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task approved successfully")
    })
    @GetMapping("/{workflow_id}/approve/{task_id}")
    public void approveTaskFor(@PathVariable("workflow_id") String workflowId,
                               @PathVariable("task_id") String taskId,
                               @RequestParam("correlation_id") String correlationId) {

        workflowService.workflowApprovalUpdateFor(workflowId, taskId, correlationId, ApprovalStatus.APPROVED);
    }

    @Operation(summary = "Reject task",
            description = "Reject a specific task in a workflow")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task rejected successfully")
    })
    @GetMapping("/{workflow_id}/reject/{task_id}")
    public void rejectTaskFor(@PathVariable("workflow_id") String workflowId,
                              @PathVariable("task_id") String taskId,
                              @RequestParam("correlation_id") String correlationId) {

        workflowService.workflowApprovalUpdateFor(workflowId, taskId, correlationId, ApprovalStatus.REJECTED);
    }
}
