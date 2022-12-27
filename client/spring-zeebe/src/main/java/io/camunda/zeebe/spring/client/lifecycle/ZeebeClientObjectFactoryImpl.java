package io.camunda.zeebe.spring.client.lifecycle;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.ZeebeClientBuilder;
import io.camunda.zeebe.client.ZeebeClientConfiguration;
import io.camunda.zeebe.client.impl.ZeebeClientImpl;
import io.camunda.zeebe.gateway.protocol.GatewayGrpc.GatewayStub;
import io.grpc.ManagedChannel;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.lang.NonNull;

/**
 * Default {@link ZeebeClientObjectFactory} implementations
 */
public abstract class ZeebeClientObjectFactoryImpl implements ZeebeClientObjectFactory {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * Creates {@link ZeebeClient} instances based on {@link ZeebeClientBuilder},
   * delegating object creation to its {@link ZeebeClientBuilder#build()} method.
   */
  public static class FromBuilder extends ZeebeClientObjectFactoryImpl {
    private final ZeebeClientBuilder builder;

    public FromBuilder(ZeebeClientBuilder builder) {
      this.builder = builder;
    }

    @Override
    @NonNull
    public ZeebeClient getObject() throws BeansException {
      LOG.info("Creating ZeebeClient using ZeebeClientBuilder [" + builder + "]");
      return builder.build();
    }
  }

  /**
   * Creates {@link ZeebeClient} instances based on {@link ZeebeClientConfiguration}.
   * <p>
   * This implementation provides more flexibility compared to {@link FromConfiguration} implementation,
   * because it also allows to use a custom thread pool.
   */
  public static class FromConfiguration extends ZeebeClientObjectFactoryImpl {
    private final ZeebeClientConfiguration configuration;
    private final Supplier<ScheduledExecutorService> threadPoolSupplier;

    /**
     * @param configuration Zeebe client configuration
     * @param threadPoolSupplier Function to create a {@link ScheduledExecutorService} that will be used by Zeebe client.
     *                           ExecutorService lifecycle is managed by the underlying {@link ZeebeClientImpl}.
     */
    public FromConfiguration(ZeebeClientConfiguration configuration, Supplier<ScheduledExecutorService> threadPoolSupplier) {
      this.configuration = configuration;
      this.threadPoolSupplier = threadPoolSupplier;
    }

    @Override
    @NonNull
    public ZeebeClient getObject() throws BeansException {
      LOG.info("Creating ZeebeClient using ZeebeClientConfiguration [" + configuration + "]");

      ManagedChannel managedChannel = ZeebeClientImpl.buildChannel(configuration);
      GatewayStub gatewayStub = ZeebeClientImpl.buildGatewayStub(managedChannel, configuration);
      return new ZeebeClientImpl(configuration, managedChannel, gatewayStub, threadPoolSupplier.get());
    }
  }
}
