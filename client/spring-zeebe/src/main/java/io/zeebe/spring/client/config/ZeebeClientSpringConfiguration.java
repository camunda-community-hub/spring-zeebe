package io.zeebe.spring.client.config;

import io.zeebe.client.ZeebeClientConfiguration;
import io.zeebe.client.impl.ZeebeClientBuilderImpl;
import io.zeebe.spring.client.bean.value.factory.ReadAnnotationValueConfiguration;
import io.zeebe.spring.client.config.processor.PostProcessorConfiguration;
import java.util.Properties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import({
  PostProcessorConfiguration.class,
  ReadAnnotationValueConfiguration.class,
})
public class ZeebeClientSpringConfiguration {

  public static final ZeebeClientBuilderImpl DEFAULT =
      (ZeebeClientBuilderImpl) new ZeebeClientBuilderImpl().withProperties(new Properties());

  @Bean
  public SpringZeebeClient springZeebeClient(
      final ZeebeClientConfiguration properties,
      final ApplicationEventPublisher publisher,
      final CreateDefaultTopic createDefaultTopic) {
    return new SpringZeebeClient(properties, publisher, createDefaultTopic);
  }

  @Bean
  public CreateDefaultTopic defaultTopic() {
    return new CreateDefaultTopic();
  }
}
