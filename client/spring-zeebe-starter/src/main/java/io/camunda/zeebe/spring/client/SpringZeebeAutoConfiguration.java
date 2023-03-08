package io.camunda.zeebe.spring.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.client.impl.ZeebeObjectMapper;
import io.camunda.zeebe.spring.client.actuator.ZeebeActuatorConfiguration;
import io.camunda.zeebe.spring.client.annotation.customizer.ZeebeWorkerValueCustomizer;
import io.camunda.zeebe.spring.client.configuration.ZeebeClientConfiguration;
import io.camunda.zeebe.spring.client.configuration.ExecutorServiceConfiguration;
import io.camunda.zeebe.spring.client.event.ZeebeLifecycleEventProducer;
import io.camunda.zeebe.spring.client.properties.PropertyBasedZeebeWorkerValueCustomizer;
import io.camunda.zeebe.spring.client.properties.ZeebeClientConfigurationProperties;
import io.camunda.zeebe.spring.client.testsupport.SpringZeebeTestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.invoke.MethodHandles;

/**
 *
 * ZeebeClientBuilder is provided by ZeebeClientStarterAutoConfiguration (which is enabled by META-INF of Spring Boot Starter)
 *
 * ZeebeClientObjectFactory can create ZeebeClients, it does that by being used as ZeebeClientLifecycle in AbstractZeebeBaseClientSpringConfiguration
 */
@Configuration
@ConditionalOnProperty(prefix = "zeebe.client", name = "enabled", havingValue = "true",  matchIfMissing = true)
@Import({
  ZeebeClientConfiguration.class,
  ExecutorServiceConfiguration.class,
  ZeebeActuatorConfiguration.class})
@EnableConfigurationProperties(ZeebeClientConfigurationProperties.class)
public class SpringZeebeAutoConfiguration extends AbstractZeebeBaseClientSpringConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final ZeebeClientConfigurationProperties configurationProperties;
  public SpringZeebeAutoConfiguration(ZeebeClientConfigurationProperties configurationProperties) {
    this.configurationProperties = configurationProperties;
  }

  @Bean
  @ConditionalOnMissingBean(SpringZeebeTestContext.class) // only run if we are not running in a test case - as otherwise the the lifecycle is controlled by the test
  public ZeebeLifecycleEventProducer zeebeAnnotationLifecycle(final ZeebeClient client, final ApplicationEventPublisher publisher) {
    return new ZeebeLifecycleEventProducer(client, publisher);
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

}
