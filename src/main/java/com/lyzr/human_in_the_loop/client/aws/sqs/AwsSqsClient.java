package com.lyzr.human_in_the_loop.client.aws.sqs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsSqsClient {

    @Value("${aws.account.id}")
    private String awsAccountId;

    @Value("${aws.sqs.region}")
    private String awsRegion;

    private final SqsAsyncClient sqsAsyncClient;
    private final ObjectMapper objectMapper;

    public void pushEventToSqs(String queueName, Object payload) {
        try {
            String messageBody = objectMapper.writeValueAsString(payload);
            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                    .queueUrl(getQueueUrl(queueName))
                    .messageBody(messageBody)
                    .build();

            sqsAsyncClient.sendMessage(sendMessageRequest)
                    .whenComplete((result, error) -> {
                        if (error != null) {
                            log.error("Failed to send message to SQS queue: {}", queueName, error);
                        } else {
                            log.debug("Message sent to SQS queue: {} with messageId: {}",
                                    queueName, result.messageId());
                        }
                    });
        } catch (Exception e) {
            log.error("Error sending message to SQS queue: {}", queueName, e);
            throw new RuntimeException("Failed to send message to SQS", e);
        }
    }

    private String getQueueUrl(String queueName) {
        return String.format("https://sqs.%s.amazonaws.com/%s/%s", awsRegion, awsAccountId, queueName);
    }
}