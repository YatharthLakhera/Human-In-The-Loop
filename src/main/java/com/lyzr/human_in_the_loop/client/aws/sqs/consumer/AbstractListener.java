package com.lyzr.human_in_the_loop.client.aws.sqs.consumer;

import com.lyzr.human_in_the_loop.client.aws.sqs.models.QueueRequest;
import com.lyzr.human_in_the_loop.constants.ThreadConstants;
import com.lyzr.human_in_the_loop.utils.ApplicationUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.UUID;

@Slf4j
public abstract class AbstractListener<T extends QueueRequest> {

    protected void processMessage(String message, Class<T> requestClass) throws Exception {
        log.info("Received message: {}", message);
        T queueRequest = ApplicationUtils.GSON.fromJson(message, requestClass);

        // Setup MDC context
        MDC.put(ThreadConstants.LOG_ID, UUID.randomUUID().toString());
        log.info("Processing message for correlationId: {}", queueRequest.getCorrelationId());

        try {
            // Delegate to concrete implementation
            handleMessage(queueRequest);
            log.info("Successfully processed message for correlationId: {}", queueRequest.getCorrelationId());
        } catch (Exception e) {
            log.error("Failed to process message for correlationId: {}", queueRequest.getCorrelationId(), e);
            throw e;
        } finally {
            // Clean up MDC context
            MDC.remove(ThreadConstants.LOG_ID);
        }
    }

    protected abstract void handleMessage(T queueRequest) throws Exception;
}

