package io.zeebe.spring.client.config;

import io.zeebe.client.ZeebeClientBuilder;
import io.zeebe.client.impl.ZeebeClientBuilderImpl;
import io.zeebe.client.impl.ZeebeClientImpl;
import io.zeebe.spring.client.ZeebeClientLifecycle;
import io.zeebe.spring.client.ZeebeClientObjectFactory;
import io.zeebe.spring.client.bean.value.factory.ReadAnnotationValueConfiguration;
import io.zeebe.spring.client.config.processor.PostProcessorConfiguration;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import({
  PostProcessorConfiguration.class,
  ReadAnnotationValueConfiguration.class,

})
public class ZeebeClientSpringConfiguration {

  @Autowired
  ZeebeClientBuilder zeebeClientBuilder;

  public static final ZeebeClientBuilderImpl DEFAULT =
    (ZeebeClientBuilderImpl) new ZeebeClientBuilderImpl().withProperties(new Properties());

  @Bean
  public ZeebeClientLifecycle zeebeClientLifecycle(
    final ZeebeClientObjectFactory factory,
    final ApplicationEventPublisher publisher) {
    return new ZeebeClientLifecycle(factory, publisher);
  }

  @Bean
  public ZeebeClientObjectFactory zeebeClientObjectFactory() {
    return () -> zeebeClientBuilder.build();
  }
}
