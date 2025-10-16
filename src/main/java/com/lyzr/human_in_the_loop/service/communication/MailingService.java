package com.lyzr.human_in_the_loop.service.communication;

import com.lyzr.human_in_the_loop.client.communication.MailGunClient;
import com.lyzr.human_in_the_loop.db.entity.TaskApprovalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MailingService {

    @Value("${backend.base.url}")
    private String backendBaseUrl;

    @Autowired
    private MailGunClient mailGunClient;

    public void sendCommunicationForApproval(String userName, String to,
                                             TaskApprovalDetails taskApprovalDetails) {
        try {
            String subject = "Approval Required By " + taskApprovalDetails.getRequesterService();
            mailGunClient.sendEmail(userName, to, subject, getBody(taskApprovalDetails));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getBody(TaskApprovalDetails taskApprovalDetails) {
        String approveUrl = String.format(
                "%s/v1/workflow/%s/approve/%s?correlation_id=%s",
                backendBaseUrl,
                taskApprovalDetails.getWorkflowId(),
                taskApprovalDetails.getTaskId(),
                taskApprovalDetails.getCorrelationId()
        );
        String rejectUrl = String.format(
                "%s/v1/workflow/%s/reject/%s?correlation_id=%s",
                backendBaseUrl,
                taskApprovalDetails.getWorkflowId(),
                taskApprovalDetails.getTaskId(),
                taskApprovalDetails.getCorrelationId()
        );

        return String.format("""
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                .button {
                    display: inline-block;
                    padding: 10px 20px;
                    margin: 10px;
                    color: white;
                    text-decoration: none;
                    border-radius: 4px;
                    font-family: Arial, sans-serif;
                    font-weight: bold;
                    cursor: pointer;
                    border: none;
                }
                .approve {
                    background-color: #4CAF50;
                }
                .reject {
                    background-color: #f44336;
                }
                .container {
                    max-width: 600px;
                    margin: 0 auto;
                    padding: 20px;
                    font-family: Arial, sans-serif;
                }
                .button-container {
                    margin: 20px 0;
                    text-align: center;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <h2>Action Required: Approval Request</h2>
                <p>You have a pending approval request with the following details:</p>
                <p><strong>Title:</strong> %s</p>
                <p><strong>Description:</strong> %s</p>
                <p><strong>Requested by:</strong> %s</p>
                
                <div class="button-container">
                    <button class="button approve" onclick="handleAction('%s')">Approve</button>
                    <button class="button reject" onclick="handleAction('%s')">Reject</button>
                </div>
                
                <p>This is an automated message, please do not reply to this email.</p>
            </div>
            
            <script>
                function handleAction(url) {
                    fetch(url, {
                        method: 'GET',
                        headers: {
                            'Accept': 'application/json',
                            'Content-Type': 'application/json'
                        }
                    })
                    .then(response => {
                        if (response.ok) {
                            alert('Action completed successfully!');
                        } else {
                            alert('Failed to complete the action. Please try again.');
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        alert('An error occurred. Please try again later.');
                    });
                }
            </script>
        </body>
        </html>
        """,
                taskApprovalDetails.getTitle(),
                taskApprovalDetails.getDescription(),
                taskApprovalDetails.getRequesterService(),
                approveUrl,
                rejectUrl
        );
    }
}
