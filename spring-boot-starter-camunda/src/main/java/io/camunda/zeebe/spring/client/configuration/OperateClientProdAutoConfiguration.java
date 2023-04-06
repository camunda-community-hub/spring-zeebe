package io.camunda.zeebe.spring.client.configuration;

import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.exception.OperateException;
import io.camunda.zeebe.spring.client.properties.OperateClientConfigurationProperties;
import io.camunda.zeebe.spring.client.testsupport.SpringZeebeTestContext;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;

@ConditionalOnProperty(prefix = "operate.client", name = "enabled", havingValue = "true",  matchIfMissing = false)
@ConditionalOnMissingBean(SpringZeebeTestContext.class)
@EnableConfigurationProperties(OperateClientConfigurationProperties.class)
public class OperateClientProdAutoConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Value("${camunda.operate.client.startup.retry.maxAttempts:24}")
  private int startupRetryMaxAttempts;

  @Value("${camunda.operate.client.startup.retry.awaitDurationInSeconds:5}")
  private int startupRetryAwaitDurationInSeconds;

  @Bean
  public CamundaOperateClient camundaOperateClient(OperateClientConfigurationProperties props) {
    String operateUrl = props.getOperateUrl();

    // Giving 2 minutes for Operate to start-up
    RetryConfig config = RetryConfig.custom()
      .maxAttempts(startupRetryMaxAttempts)
      .waitDuration(Duration.of(startupRetryAwaitDurationInSeconds, ChronoUnit.SECONDS))
      .build();
    Retry retry = Retry.of("camundaOperateClient", config);

    return retry.executeSupplier(() -> {
      try {
        return new CamundaOperateClient.Builder()
          .operateUrl(operateUrl)
          .authentication(props.getAuthentication(operateUrl))
          .build();
      } catch (OperateException e) {
        LOG.warn("An attempt to connect to Operate failed: " + e);
        throw new RuntimeException(e);
      }
    });
  }
}
