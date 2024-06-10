package io.camunda.zeebe.spring.client.configuration;

import io.camunda.common.auth.Authentication;
import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.CamundaOperateClientBuilder;
import io.camunda.zeebe.spring.client.configuration.condition.OperateClientCondition;
import io.camunda.zeebe.spring.client.properties.CamundaClientProperties;
import io.camunda.zeebe.spring.client.properties.OperateClientConfigurationProperties;
import io.camunda.zeebe.spring.client.testsupport.SpringZeebeTestContext;
import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

@Conditional(OperateClientCondition.class)
@ConditionalOnMissingBean(SpringZeebeTestContext.class)
@EnableConfigurationProperties({
  OperateClientConfigurationProperties.class,
  CamundaClientProperties.class
})
public class OperateClientConfiguration {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final OperateClientConfigurationProperties legacyProperties;
  private final CamundaClientProperties properties;
  private final Authentication authentication;
  @Autowired Authentication legacyAuthentication;

  @Autowired
  public OperateClientConfiguration(
      OperateClientConfigurationProperties legacyProperties,
      CamundaClientProperties properties,
      Authentication authentication) {
    this.legacyProperties = legacyProperties;
    this.properties = properties;
    this.authentication = authentication;
  }

  @Deprecated
  public OperateClientConfiguration() {
    this.legacyProperties = null;
    this.properties = null;
    this.authentication = null;
  }

  @Deprecated
  public CamundaOperateClient camundaOperateClient(
      OperateClientConfigurationProperties properties) {
    CamundaOperateClient client;
    try {
      client =
          new CamundaOperateClientBuilder()
              .authentication(legacyAuthentication)
              .operateUrl(properties.getOperateUrl())
              .setup()
              .build();
    } catch (Exception e) {
      LOG.warn("An attempt to connect to Operate failed: " + e);
      throw new RuntimeException(e);
    }
    return client;
  }

  @Bean
  @ConditionalOnMissingBean
  public CamundaOperateClient camundaOperateClient() {
    CamundaOperateClient client;
    try {
      client =
          new CamundaOperateClientBuilder()
              .authentication(authentication)
              .operateUrl(operateUrl())
              .setup()
              .build();
    } catch (Exception e) {
      LOG.warn("An attempt to connect to Operate failed: " + e);
      throw new RuntimeException(e);
    }
    return client;
  }

  private String operateUrl() {
    return PropertyUtil.getOrLegacyOrDefault(
        "OperateUrl",
        () -> properties.getOperate().getBaseUrl().toString(),
        legacyProperties::getOperateUrl,
        null,
        null);
  }
}
