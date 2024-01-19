package io.camunda.zeebe.spring.client.configuration;

import io.camunda.common.auth.Authentication;
import io.camunda.tasklist.CamundaTaskListClient;
import io.camunda.zeebe.spring.client.configuration.condition.TaskListClientCondition;
import io.camunda.zeebe.spring.client.properties.TaskListClientConfigurationProperties;
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

@Conditional(TaskListClientCondition.class)
@ConditionalOnProperty(prefix = "camunda.tasklist.client", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnMissingBean(SpringZeebeTestContext.class)
@EnableConfigurationProperties(TaskListClientConfigurationProperties.class)
public class TaskListClientConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Autowired
  Authentication authentication;

  @Bean
  @ConditionalOnMissingBean
  public CamundaTaskListClient camundaTaskListClient(TaskListClientConfigurationProperties props) {
    CamundaTaskListClient client;
    try {
      client = CamundaTaskListClient.builder()
        .authentication(authentication)
        .taskListUrl(props.getTaskListUrl())
        .setup()
        .build();
    } catch (Exception e) {
      LOG.warn("An attempt to connect to TaskList failed: " + e);
      throw new RuntimeException(e);
    }
    return client;
  }
}
