package io.zeebe.spring.client.config;

import io.zeebe.client.ZeebeClientBuilder;
import io.zeebe.spring.client.bean.value.factory.ReadAnnotationValueConfiguration;
import io.zeebe.spring.client.config.processor.PostProcessorConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import({ PostProcessorConfiguration.class, ReadAnnotationValueConfiguration.class, })
public class ZeebeClientConfiguration
{

    @Bean
    public ZeebeClientBuilder builder()
    {
        throw new UnsupportedOperationException("implement");
        //return  ZeebeClientBuilderImpl.fromProperties(new Properties());
    }

    @Bean
    public SpringZeebeClient springZeebeClient(final ZeebeClientBuilder builder, final ApplicationEventPublisher publisher,
            final CreateDefaultTopic createDefaultTopic)
    {
        return new SpringZeebeClient(builder, publisher, createDefaultTopic);
    }

    @Bean
    public CreateDefaultTopic defaultTopic()
    {
        return new CreateDefaultTopic();
    }
}
