package io.zeebe.spring.broker.config;

import io.zeebe.broker.system.SystemContext;
import io.zeebe.broker.system.configuration.BrokerCfg;
import io.zeebe.broker.system.configuration.TomlConfigurationReader;
import java.util.Optional;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/** Included by {@link io.zeebe.spring.broker.EnableZeebeBroker} annotation. */
@Slf4j
public class ZeebeBrokerConfiguration {

  static Function<Environment, Optional<String>> tomlFileFromEnv =
      environment -> {
        final String[] args =
            environment.getProperty("nonOptionArgs", String[].class, new String[0]);
        if (args == null || args.length == 0) {
          return Optional.empty();
        } else if (args.length > 1) {
          throw new IllegalArgumentException("requires exactly one cli argument, the tomlFile.");
        } else {
          return Optional.of(args[0]);
        }
      };

  @Bean
  public SystemContext systemContext(final Environment environment) {
    final Optional<String> tomlFile = tomlFileFromEnv.apply(environment);

    final BrokerCfg cfg =
        tomlFile.map(f -> new TomlConfigurationReader().read(f)).orElseGet(BrokerCfg::new);

    log.info("building broker from tomlFile={}", tomlFile);

    return new SystemContext(cfg, null, null);
  }

  @Bean
  public SpringZeebeBroker springBroker(final SystemContext systemContext) {
    return new SpringZeebeBroker(systemContext);
  }
}
