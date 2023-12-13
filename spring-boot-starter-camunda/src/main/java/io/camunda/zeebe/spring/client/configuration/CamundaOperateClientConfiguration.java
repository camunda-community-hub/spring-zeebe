package io.camunda.zeebe.spring.client.configuration;

import io.camunda.common.auth.Authentication;
import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.CamundaOperateClientBuilder;
import io.camunda.zeebe.spring.client.configuration.condition.CamundaOperateClientCondition;
import io.camunda.zeebe.spring.client.properties.CamundaOperateClientConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

import java.lang.invoke.MethodHandles;

/**
 * This will be deprecated once we move to the new schema (i.e. not prefixing with camunda.*)
 */
@Deprecated
@Conditional(CamundaOperateClientCondition.class)
@EnableConfigurationProperties(CamundaOperateClientConfigurationProperties.class)
public class CamundaOperateClientConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Autowired
  Authentication authentication;

  @Bean
  public CamundaOperateClient camundaOperateClient(CamundaOperateClientConfigurationProperties props) {
    LOG.warn("Using a deprecated operate properties");
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


