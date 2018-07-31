package io.zeebe.spring.broker.config;

import io.zeebe.broker.Broker;
import io.zeebe.broker.system.configuration.BrokerCfg;
import java.nio.file.Files;
import java.util.Optional;
import java.util.function.Function;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/** Included by {@link io.zeebe.spring.broker.EnableZeebeBroker} annotation. */
@Slf4j
public class ZeebeBrokerConfiguration {

  /**
   * @param brokerFactory the factory that knows how to create a new broker instance.
   * @return a new lifecycle instance
   */
  @Bean
  public ZeebeBrokerLifecycle brokerLifecycle(final ZeebeBrokerFactory brokerFactory) {
    return new ZeebeBrokerLifecycle(brokerFactory);
  }

  @Bean
  @SneakyThrows
  public ZeebeBrokerFactory brokerFactory() {
    final String tempFolder =
        Files.createTempDirectory("zeebe").toAbsolutePath().normalize().toString();

    log.info("broker dir: {}", tempFolder);

    return () -> {
      final BrokerCfg cfg = new BrokerCfg();
      cfg.setBootstrap(1);

      return new Broker(cfg, tempFolder, null);
    };
  }

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
