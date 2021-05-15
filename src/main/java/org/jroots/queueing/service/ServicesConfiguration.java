package org.jroots.queueing.service;

import io.dropwizard.setup.Environment;
import org.jroots.queueing.QueueReviverConfiguration;
import org.jroots.queueing.client.consumer.QueueConsumerImpl;
import org.jroots.queueing.client.producer.QueueProducer;
import org.jroots.queueing.client.producer.QueueProducerImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Nonnull;
import java.util.concurrent.Executor;

@Configuration
public class ServicesConfiguration {

    private final QueueReviverConfiguration configuration;
    private final ConsumerService consumerService;
    private final ApplicationContext applicationContext;
    private final Environment environment;

    public ServicesConfiguration(@Nonnull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.configuration = (QueueReviverConfiguration) applicationContext.getBean("appConf");
        this.environment = (Environment) applicationContext.getBean("appEnv");
        this.consumerService = consumerService();
        environment.lifecycle().manage(consumerService);
    }

    @Bean
    ConsumerService consumerService() {
        return new ConsumerService(new QueueConsumerImpl(configuration, threadPoolTaskExecutor(), handlerService()));
    }

    @Bean
    HandlerService handlerService() {
        return new HandlerService(queueConsumer());
    }

    @Bean
    QueueProducer queueConsumer() {
        return new QueueProducerImpl(configuration);
    }

    @Bean
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(6);
        executor.setThreadNamePrefix("sqsExecutor");
        executor.initialize();
        return executor;
    }
}
