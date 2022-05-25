package io.camunda.zeebe.spring.test;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.AbstractZeebeBaseClientSpringConfiguration;
import io.camunda.zeebe.spring.client.lifecycle.ZeebeClientObjectFactory;
import io.camunda.zeebe.spring.test.lifecycle.NoopZeebeClientLifecycleConfiguration;
import io.camunda.zeebe.spring.test.proxy.TestProxyConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import java.lang.invoke.MethodHandles;

@TestConfiguration
@Import({TestProxyConfiguration.class, NoopZeebeClientLifecycleConfiguration.class})
public class ZeebeTestClientSpringConfiguration extends AbstractZeebeBaseClientSpringConfiguration {

}
