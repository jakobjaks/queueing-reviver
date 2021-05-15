package org.jroots.queueing.service;

import org.jroots.queueing.api.Message;
import org.jroots.queueing.client.producer.QueueProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class HandlerService {

    private final QueueProducer queueProducer;
    private final int backoffDelay = 5;

    private final Logger logger = LoggerFactory.getLogger(HandlerService.class);

    public HandlerService(QueueProducer queueProducer) {
        this.queueProducer = queueProducer;
    }

    public CompletableFuture<Void> handlePayload(Message message) {
        var counter = message.getRequeueCounter();
        var delay = Math.pow(backoffDelay, counter);
        if (delay > 900) {
            logger.error("Message with id={} and identificator={} has been retired too many times, aborting message",
                    message.getUUID(), message.getIdentifier());
            return CompletableFuture.failedFuture(new RuntimeException("Message retried too many times"));
        }

        return queueProducer.sendMessageToQueue(message, (int) delay);
    }

}
