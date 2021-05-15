package org.jroots.queueing.client.producer;

import org.jroots.queueing.api.Message;

import java.util.concurrent.CompletableFuture;

public interface QueueProducer {

    CompletableFuture<Void> sendMessageToQueue(Message message, int delay);
}
