package io.zeebe.spring.client.config;

import io.zeebe.spring.client.bean.value.factory.ReadAnnotationValueConfiguration;
import io.zeebe.spring.client.config.processor.PostProcessorConfiguration;
import io.zeebe.spring.client.properties.ZeebeClientProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import({
        PostProcessorConfiguration.class,
        ReadAnnotationValueConfiguration.class,
})
public class ZeebeClientConfiguration
{

    @Bean
    public ZeebeClientProperties properties()
    {
        return ZeebeClientProperties.DEFAULT;
    }

    @Bean
    public SpringZeebeClient springZeebeClient(final ZeebeClientProperties properties, final ApplicationEventPublisher publisher)
    {
        return new SpringZeebeClient(properties, publisher);
    }
}
