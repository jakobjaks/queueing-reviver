package org.jroots.queueing.service;

import io.dropwizard.lifecycle.Managed;
import org.jroots.queueing.client.consumer.QueueConsumer;

public class ConsumerService implements Managed {

    private QueueConsumer consumer;

    public ConsumerService(QueueConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public void start() throws Exception {
        consumer.startConsuming();
    }

    @Override
    public void stop() throws Exception {
        consumer.stopConsuming();
    }
}
