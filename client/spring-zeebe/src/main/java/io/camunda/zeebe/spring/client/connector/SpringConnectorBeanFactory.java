package io.camunda.zeebe.spring.client.connector;

import io.camunda.connector.impl.config.ConnectorBeanFactory;
import org.springframework.context.ApplicationContext;

public class SpringConnectorBeanFactory implements ConnectorBeanFactory {

  private final ApplicationContext ctx;

  public SpringConnectorBeanFactory(ApplicationContext ctx) {
    this.ctx = ctx;
  }

  @Override
  public boolean containsBeans(Class<?> type) {
    return !ctx.getBeansOfType(type).isEmpty();
  }

  @Override
  public <T> T getBean(Class<T> type) {
    return ctx.getBean(type);
  }
}
