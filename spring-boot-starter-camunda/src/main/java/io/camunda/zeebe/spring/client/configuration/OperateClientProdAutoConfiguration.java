package io.camunda.zeebe.spring.client.configuration;

import io.camunda.common.auth.Authentication;
import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.CamundaOperateClientBuilder;
import io.camunda.zeebe.spring.client.configuration.condition.OperateClientCondition;
import io.camunda.zeebe.spring.client.properties.OperateClientConfigurationProperties;
import io.camunda.zeebe.spring.client.testsupport.SpringZeebeTestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

import java.lang.invoke.MethodHandles;

@Conditional(OperateClientCondition.class)
@ConditionalOnProperty(prefix = "operate.client", name = "enabled", havingValue = "true",  matchIfMissing = true)
@ConditionalOnMissingBean(SpringZeebeTestContext.class)
@EnableConfigurationProperties(OperateClientConfigurationProperties.class)
public class OperateClientProdAutoConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  // TODO: Once resiliency is implemented use this
  /**
  @Value("${camunda.operate.client.startup.retry.maxAttempts:24}")
  private int startupRetryMaxAttempts;

  @Value("${camunda.operate.client.startup.retry.awaitDurationInSeconds:5}")
  private int startupRetryAwaitDurationInSeconds;
  **/

  @Autowired
  Authentication authentication;

  // TODO: Handle resiliency when connecting to Operate
  @Bean
  @ConditionalOnMissingBean
  public CamundaOperateClient camundaOperateClient(OperateClientConfigurationProperties props) {
    LOG.debug("Using a newer operate properties");
    CamundaOperateClient client;
    try {
         client = new CamundaOperateClientBuilder()
         .authentication(authentication)
         .operateUrl(props.getOperateUrl())
         .setup()
         .build();
    } catch (Exception e) {
      LOG.warn("An attempt to connect to Operate failed: " + e);
      throw new RuntimeException(e);
    }
    return client;
  }
}
