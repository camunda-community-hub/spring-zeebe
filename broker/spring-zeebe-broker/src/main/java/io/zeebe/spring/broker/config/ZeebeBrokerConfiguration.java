package io.zeebe.spring.broker.config;

import io.zeebe.spring.broker.ZeebeBrokerLifecycle;
import io.zeebe.spring.broker.ZeebeBrokerObjectFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * Included by {@link io.zeebe.spring.broker.EnableZeebeBroker} annotation.
 */
@Slf4j
public class ZeebeBrokerConfiguration {

  /**
   * @param brokerFactory the factory that knows how to create a new broker instance.
   * @return a new lifecycle instance
   */
  @Bean
  public ZeebeBrokerLifecycle brokerLifecycle(final ZeebeBrokerObjectFactory brokerFactory) {
    return new ZeebeBrokerLifecycle(brokerFactory);
  }

  @Bean
  public ZeebeBrokerObjectFactory brokerFactory(final Environment environment) {
    return new TempZeebeBrokerFactory(environment);
  }

  //  @Bean
  //  public SystemContext systemContext(final Environment environment) {
  //    final Optional<String> tomlFile = tomlFileFromEnv.apply(environment);
  //
  //    final BrokerCfg cfg =
  //        tomlFile.map(f -> new TomlConfigurationReader().read(f)).orElseGet(BrokerCfg::new);
  //
  //    log.info("building broker from tomlFile={}", tomlFile);
  //
  //    return new SystemContext(cfg, null, null);
  //  }

  //  @Bean
  //  public SpringZeebeBroker springBroker(final SystemContext systemContext) {
  //    return new SpringZeebeBroker(systemContext);
  //  }
}
