package io.camunda.zeebe.spring.client.configuration;

import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.exception.OperateException;
import io.camunda.zeebe.spring.client.properties.OperateClientConfigurationProperties;
import io.camunda.zeebe.spring.client.testsupport.SpringZeebeTestContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(prefix = "operate.client", name = "enabled", havingValue = "true",  matchIfMissing = false)
@ConditionalOnMissingBean(SpringZeebeTestContext.class)
@EnableConfigurationProperties(OperateClientConfigurationProperties.class)
public class OperateClientProdAutoConfiguration {

  @Bean
  public CamundaOperateClient camundaOperateClient(OperateClientConfigurationProperties props) throws OperateException {
    String operateUrl = props.getOperateUrl();
    return new CamundaOperateClient.Builder()
      .operateUrl(operateUrl)
      .authentication(props.getAuthentication(operateUrl))
      .build();
  }
}
