package io.camunda.zeebe.spring.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.lang.invoke.MethodHandles;

@Configuration
public class LegacyWarning {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public LegacyWarning() {
    LOG.warn("*******************************************************************\n\n" +
      "  You are using the deprecated 'spring-zeebe-starter' dependency. Please update your POM:\n" +
      "  io.camunda : spring-zeebe-starter --> io.camunda.spring : spring-boot-starter-camunda\n");
    LOG.warn("*******************************************************************");
  }

}
