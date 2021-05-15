package org.jroots.queueing.client.producer;

import org.jroots.queueing.QueueReviverConfiguration;
import org.jroots.queueing.api.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.concurrent.CompletableFuture;

public class QueueProducerImpl implements QueueProducer {

    private final String sqsUrl;
    private final SqsAsyncClient amazonSQSClient;

    private final Logger logger = LoggerFactory.getLogger(QueueProducerImpl.class);

    public QueueProducerImpl(QueueReviverConfiguration configuration) {
        this.sqsUrl = configuration.getIncomingQueueUrl();
        amazonSQSClient = SqsAsyncClient.builder().region(Region.US_EAST_1).build();
    }

    @Override
    public CompletableFuture<Void> sendMessageToQueue(Message message, int delay) {
        logger.info("Sending message to SQS with id={} and identifier={}", message.getUUID(), message.getIdentifier());
        SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(sqsUrl)
                .messageBody(message.serializeToJson())
                .delaySeconds(delay)
                .build();
        return amazonSQSClient.sendMessage(request).thenAccept(x -> CompletableFuture.completedFuture(null));
    }
}
