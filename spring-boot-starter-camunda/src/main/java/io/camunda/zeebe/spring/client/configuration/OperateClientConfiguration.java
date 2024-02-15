package io.camunda.zeebe.spring.client.configuration;

import io.camunda.common.auth.Authentication;
import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.CamundaOperateClientBuilder;
import io.camunda.zeebe.spring.client.configuration.condition.OperateClientCondition;
import io.camunda.zeebe.spring.client.properties.OperateClientConfigurationProperties;
import io.camunda.zeebe.spring.client.testsupport.SpringZeebeTestContext;
import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

@Conditional(OperateClientCondition.class)
@ConditionalOnProperty(
    prefix = "camunda.operate.client",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
@ConditionalOnMissingBean(SpringZeebeTestContext.class)
@EnableConfigurationProperties(OperateClientConfigurationProperties.class)
public class OperateClientConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Autowired Authentication authentication;

  @Bean
  @ConditionalOnMissingBean
  public CamundaOperateClient camundaOperateClient(OperateClientConfigurationProperties props) {
    CamundaOperateClient client;
    try {
      client =
          new CamundaOperateClientBuilder()
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
