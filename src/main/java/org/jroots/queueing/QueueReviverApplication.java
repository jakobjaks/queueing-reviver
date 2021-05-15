package org.jroots.queueing;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.jroots.queueing.health.TemplateHealthCheck;
import org.jroots.queueing.service.ServicesConfiguration;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class QueueReviverApplication extends Application<QueueReviverConfiguration> {

    public static void main(final String[] args) throws Exception {
        new QueueReviverApplication().run(args);
    }

    @Override
    public String getName() {
        return "QueueReviver";
    }

    @Override
    public void initialize(final Bootstrap<QueueReviverConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final QueueReviverConfiguration configuration,
                    final Environment environment) {
        final TemplateHealthCheck healthCheck =
                new TemplateHealthCheck("health");
        environment.healthChecks().register("template", healthCheck);

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        registerEnvironment(environment, ctx);
        registerConfiguration(configuration, ctx);

        ctx.refresh();

        var servicesConfiguration = new ServicesConfiguration(ctx);
        ctx.registerBean(ServicesConfiguration.class, servicesConfiguration);
    }

    private ConfigurableApplicationContext applicationContext() {
        var context = new AnnotationConfigApplicationContext();
        context.scan("org.jroots.queueing");
        return context;
    }

    private void registerEnvironment(Environment environment, ConfigurableApplicationContext context) {
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        beanFactory.registerSingleton("appEnv", environment);
    }

    private void registerConfiguration(QueueReviverConfiguration configuration, ConfigurableApplicationContext context) {
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        beanFactory.registerSingleton("appConf", configuration);
    }

}
