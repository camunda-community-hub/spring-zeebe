package io.zeebe.spring.client;

import io.zeebe.client.impl.ZeebeClientImpl;
import io.zeebe.client.task.TaskHandler;
import io.zeebe.client.task.TaskSubscription;
import io.zeebe.client.task.TaskSubscriptionBuilder;
import io.zeebe.spring.ZeebeTaskSubscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

public class ZeebeClientLifecycle extends ZeebeClientImpl implements SmartLifecycle {

    private final Logger logger = LoggerFactory.getLogger(ZeebeClientLifecycle.class);

    @Autowired(required = false)
    private List<TaskHandler> taskHandlers;

    private List<TaskSubscriptionBuilder> taskSubscriptionBuilders;

    private List<TaskSubscription> taskSubscriptions;


    public ZeebeClientLifecycle() {
        super(new Properties());
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void start() {
        logger.info("start client-template");
        connect();


        taskSubscriptionBuilders = Optional.ofNullable(taskHandlers).orElseGet(ArrayList::new).stream().map(taskHandler -> {
            ZeebeTaskSubscription annotation = Optional.ofNullable(taskHandler.getClass().getAnnotation(ZeebeTaskSubscription.class)).orElseThrow(IllegalStateException::new);

            TaskSubscriptionBuilder builder = tasks().newTaskSubscription(annotation.topicName());

            builder
                    .lockOwner(annotation.lockOwner())
                    .handler(taskHandler)
                    .lockTime(annotation.lockTime())
                    .taskFetchSize(annotation.taskFetchSize())
                    .taskType(annotation.taskType());

            return builder;
        }).collect(Collectors.toList());

       taskSubscriptions =  taskSubscriptionBuilders.stream().peek(b -> logger.info("register: {}", b)).map(TaskSubscriptionBuilder::open).collect(Collectors.toList());
    }

    @Override
    public void stop() {
        stop(() -> {
        });
    }

    @Override
    public void stop(Runnable runnable) {
        taskSubscriptions.forEach(TaskSubscription::close);

        close();
        runnable.run();

        logger.info("stopped client-template");
    }

    @Override
    public boolean isRunning() {
        return connected;
    }

    @Override
    public int getPhase() {
        return 3000;
    }
}
