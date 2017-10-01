package io.zeebe.spring.client.properties;

import io.zeebe.client.ClientProperties;
import io.zeebe.client.task.TaskHandler;
import io.zeebe.client.task.TaskSubscription;

import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static io.zeebe.client.ClientProperties.BROKER_CONTACTPOINT;
import static io.zeebe.client.ClientProperties.CLIENT_MAXREQUESTS;
import static io.zeebe.client.ClientProperties.CLIENT_SENDBUFFER_SIZE;
import static io.zeebe.client.ClientProperties.CLIENT_TASK_EXECUTION_THREADS;
import static io.zeebe.client.ClientProperties.CLIENT_TCP_CHANNEL_KEEP_ALIVE_PERIOD;
import static io.zeebe.client.ClientProperties.CLIENT_THREADINGMODE;
import static io.zeebe.client.ClientProperties.CLIENT_TOPIC_SUBSCRIPTION_PREFETCH_CAPACITY;


public interface ZeebeClientProperties extends Supplier<Properties> {

    ZeebeClientProperties DEFAULT = new ZeebeClientProperties() {

        private final Properties properties = new Properties();

        {
            ClientProperties.setDefaults(properties);
        }

        @Override
        public String getBrokerContactPoint() {
            return properties.getProperty(BROKER_CONTACTPOINT);
        }

        @Override
        public String getMaxRequests() {
            return properties.getProperty(CLIENT_MAXREQUESTS);
        }

        @Override
        public String getSendBufferSize() {
            return properties.getProperty(CLIENT_SENDBUFFER_SIZE);
        }

        @Override
        public String getThreadingMode() {
            return properties.getProperty(CLIENT_THREADINGMODE);
        }

        @Override
        public String getTaskExecutionThreads() {
            return properties.getProperty(CLIENT_TASK_EXECUTION_THREADS);
        }

        @Override
        public String getTopicSubscriptionPrefetchCapacity() {
            return properties.getProperty(CLIENT_TOPIC_SUBSCRIPTION_PREFETCH_CAPACITY);
        }

        @Override
        public String getTcpChannelKeepAlivePeriod() {
            return properties.getProperty(CLIENT_TCP_CHANNEL_KEEP_ALIVE_PERIOD);
        }
        
    };


    /**
     * Either a hostname if the broker is running on the default port or hostname:port
     */
    String getBrokerContactPoint();

    /**
     * The maximum count of concurrently in flight requests.
     */
    String getMaxRequests();


    /**
     * the size of the client's send buffer in MB
     */
    String getSendBufferSize();

    /**
     * Possible values:
     * SHARED: a single thread is used by the client for network communication
     * DEDICATED: a dedicated thread is used for running the sender, receive and conductor agent.
     */
    String getThreadingMode();


    /**
     * The number of threads for invocation of {@link TaskHandler}. Setting this value to 0 effectively disables
     * managed task execution via {@link TaskSubscription}s.
     */
    String getTaskExecutionThreads();


    /**
     * Determines the maximum amount of topic events are prefetched and buffered at a time
     * before they are handled to the event handler. Default value is 32.
     */
    String getTopicSubscriptionPrefetchCapacity();


    /**
     * The period of time in milliseconds for sending keep alive messages on tcp channels. Setting this appropriately
     * can avoid overhead by reopening channels after idle time.
     */
    /*
     * Optional property; Default is defined by transport
     */
    String getTcpChannelKeepAlivePeriod();

    default boolean isAutoStartup() {
        return true;
    }

    @Override
    default Properties get() {
        final Properties properties = new Properties();
        ClientProperties.setDefaults(properties);

        // only set property if configured value is not null
        final BiConsumer<String, Supplier<String>> set = (key, supplier) -> {
            if (supplier.get() != null) {
                properties.setProperty(key, supplier.get());
            }
        };

        set.accept(BROKER_CONTACTPOINT, this::getBrokerContactPoint);
        set.accept(CLIENT_MAXREQUESTS, this::getMaxRequests);
        set.accept(CLIENT_SENDBUFFER_SIZE, this::getSendBufferSize);
        set.accept(CLIENT_THREADINGMODE, this::getThreadingMode);
        set.accept(CLIENT_TASK_EXECUTION_THREADS, this::getTaskExecutionThreads);
        set.accept(CLIENT_TOPIC_SUBSCRIPTION_PREFETCH_CAPACITY, this::getTopicSubscriptionPrefetchCapacity);
        set.accept(CLIENT_TCP_CHANNEL_KEEP_ALIVE_PERIOD, this::getTcpChannelKeepAlivePeriod);

        return properties;
    }


}
