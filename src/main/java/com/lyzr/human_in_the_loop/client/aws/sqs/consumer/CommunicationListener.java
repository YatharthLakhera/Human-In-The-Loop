package com.lyzr.human_in_the_loop.client.aws.sqs.consumer;

import com.lyzr.human_in_the_loop.client.aws.sqs.models.CommunicationRequest;
import com.lyzr.human_in_the_loop.service.CommunicationService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommunicationListener extends AbstractListener<CommunicationRequest> {

    @Autowired
    private CommunicationService communicationService;

    @SqsListener(value = "${spring.cloud.aws.sqs.name.communication.trigger}", factory = "defaultSqsListenerContainerFactory")
    public void onMessage(String message) throws Exception {
        processMessage(message, CommunicationRequest.class);
    }

    @Override
    protected void handleMessage(CommunicationRequest communicationRequest) throws Exception {
        communicationService.sendCommunicationFor(communicationRequest);
    }
}
