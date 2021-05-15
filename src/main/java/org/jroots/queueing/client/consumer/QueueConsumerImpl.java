package org.jroots.queueing.client.consumer;

import org.jroots.queueing.QueueReviverConfiguration;
import org.jroots.queueing.api.Message;
import org.jroots.queueing.client.producer.QueueProducerImpl;
import org.jroots.queueing.service.HandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.concurrent.*;

public class QueueConsumerImpl implements QueueConsumer {

    private final String incomingSqsUrl;
    private final String outgoingSqsUrl;
    private final HandlerService handlerService;

    private final SqsAsyncClient amazonSQSClient;
    private final Executor executor;

    private final ScheduledExecutorService executorService;

    private final Logger logger = LoggerFactory.getLogger(QueueProducerImpl.class);

    public QueueConsumerImpl(QueueReviverConfiguration configuration, Executor executor, HandlerService handlerService) {
        this.handlerService = handlerService;
        this.incomingSqsUrl = configuration.getIncomingDlQueueUrl();
        this.outgoingSqsUrl = configuration.getExitDlQueueUrl();
        amazonSQSClient = SqsAsyncClient.builder().region(Region.US_EAST_1).build();
        this.executor = executor;
        this.executorService = Executors.newScheduledThreadPool(2);
    }

    @Override
    public void startConsuming() {
        startConsumingFromQueue(incomingSqsUrl);
        startConsumingFromQueue(outgoingSqsUrl);
    }

    @Override
    public void stopConsuming() {
        executorService.shutdown();
    }

    private void startConsumingFromQueue(String queueName) {
        executorService.scheduleWithFixedDelay(() -> {
            try {
                var request = ReceiveMessageRequest.builder()
                        .waitTimeSeconds(5)
                        .queueUrl(queueName)
                        .visibilityTimeout(30)
                        .build();

                amazonSQSClient.receiveMessage(request)
                        .thenAccept(receiveMessageResponse -> {
                            var messages = receiveMessageResponse.messages();
                            for (var message : messages) {
                                executor.execute(() -> {
                                    logger.info("Started executor");
                                    var internalMessage = convertToInternalMessage(message);
                                    handlerService.handlePayload(internalMessage)
                                            .thenAccept(resp -> {
                                                logger.info("Deleting message with id {}", internalMessage.getUUID());
                                                deleteMessage(queueName, message.receiptHandle());
                                            });
                                });

                            }
                        });
            } catch (Exception e) {
                logger.warn("Exception while polling SQS", e);
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    public CompletableFuture<DeleteMessageResponse> deleteMessage(String sqsUrl, String receiptHandle) {
        return amazonSQSClient.deleteMessage(
                DeleteMessageRequest.builder().queueUrl(sqsUrl).receiptHandle(receiptHandle).build());
    }

    private Message convertToInternalMessage(software.amazon.awssdk.services.sqs.model.Message message) {
        var internalMessage = new Message().deserializeFromJson(message.body());
        internalMessage.setReceiptHandle(message.receiptHandle());
        return internalMessage;
    }

}
