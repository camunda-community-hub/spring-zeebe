package io.camunda.zeebe.spring.client.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.ZeebeClientBuilder;
import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.client.impl.ZeebeObjectMapper;
import io.camunda.zeebe.spring.client.actuator.ZeebeActuatorConfiguration;
import io.camunda.zeebe.spring.client.annotation.customizer.ZeebeWorkerValueCustomizer;
import io.camunda.zeebe.spring.client.lifecycle.ZeebeClientObjectFactory;
import io.camunda.zeebe.spring.client.lifecycle.ZeebeClientObjectFactoryImpl;
import io.camunda.zeebe.spring.client.properties.PropertyBasedZeebeWorkerValueCustomizer;
import io.camunda.zeebe.spring.client.properties.ZeebeClientConfigurationProperties;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.Nullable;

@Import(ZeebeActuatorConfiguration.class)
@EnableConfigurationProperties(ZeebeClientConfigurationProperties.class)
@Configuration
public class ZeebeClientStarterAutoConfiguration {

  private final ZeebeClientConfigurationProperties configurationProperties;

  public ZeebeClientStarterAutoConfiguration(ZeebeClientConfigurationProperties configurationProperties) {
    this.configurationProperties = configurationProperties;
  }

  @ConditionalOnBean(ZeebeClientBuilder.class)
  @Bean
  public ZeebeClientObjectFactory customBuilderZeebeClientObjectFactory(ZeebeClientBuilder builder) {
    return new ZeebeClientObjectFactoryImpl.FromBuilder(builder);
  }

  /**
   * Defines a {@link ZeebeClientObjectFactory} to create ZeebeClient
   * instances based on {@link ZeebeClientConfigurationProperties}
   */
  @ConditionalOnMissingBean(ZeebeClientObjectFactory.class)
  @Bean
  public ZeebeClientObjectFactory configPropertiesZeebeClientObjectFactory(
    final @Autowired(required = false) @Lazy MeterRegistry meterRegistry) {

    return new ZeebeClientObjectFactoryImpl.FromConfiguration(
      configurationProperties,
      () -> initZeebeClientThreadPool(meterRegistry)
    );
  }

  @Bean("propertyBasedZeebeWorkerValueCustomizer")
  @ConditionalOnMissingBean(name = "propertyBasedZeebeWorkerValueCustomizer")
  public ZeebeWorkerValueCustomizer propertyBasedZeebeWorkerValueCustomizer() {
    return new PropertyBasedZeebeWorkerValueCustomizer(this.configurationProperties);
  }

  /**
   * Registering a JsonMapper bean when there is none already exists in {@link org.springframework.beans.factory.BeanFactory}.
   *
   * NOTE: This method SHOULD NOT be explicitly called as it might lead to unexpected behaviour due to the
   * {@link ConditionalOnMissingBean} annotation. i.e. Calling this method when another JsonMapper bean is defined in the context
   * might throw {@link org.springframework.beans.factory.NoSuchBeanDefinitionException}
   *
   * @return a new JsonMapper bean if none already exists in {@link org.springframework.beans.factory.BeanFactory}
   */
  @Bean(name = "zeebeJsonMapper")
  @ConditionalOnMissingBean
  public JsonMapper jsonMapper(ObjectMapper objectMapper) {
    return new ZeebeObjectMapper(objectMapper);
  }

  private ScheduledExecutorService initZeebeClientThreadPool(@Nullable MeterRegistry meterRegistry) {
    ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(configurationProperties.getNumJobWorkerExecutionThreads());

    if (meterRegistry != null) {
      MeterBinder threadPoolMetrics = new ExecutorServiceMetrics(
        threadPool, "zeebe_client_thread_pool", Collections.emptyList());
      threadPoolMetrics.bindTo(meterRegistry);
    }

    return threadPool;
  }
}
