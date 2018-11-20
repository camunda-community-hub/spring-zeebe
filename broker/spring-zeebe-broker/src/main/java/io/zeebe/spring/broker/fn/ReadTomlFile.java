package io.zeebe.spring.broker.fn;

import io.zeebe.broker.system.configuration.BrokerCfg;
import io.zeebe.util.TomlConfigurationReader;
import java.io.File;
import java.util.Optional;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

/**
 * Reads a toml file from args. If not found a default {@link BrokerCfg} is created.
 */
@Slf4j
public class ReadTomlFile implements Function<Environment, BrokerCfg> {

  static Function<Environment, Optional<File>> tomlFileFromEnv =
    environment -> {
      final String[] args =
        environment.getProperty("nonOptionArgs", String[].class, new String[0]);
      if (args == null || args.length == 0) {
        return Optional.empty();
      } else if (args.length > 1) {
        throw new IllegalArgumentException(
          "requires exactly one cli argument, the tomlFile-path.");
      } else {
        File tomlFile = new File(args[0]);
        if (!tomlFile.exists() || !tomlFile.canRead()) {
          throw ZeebeBrokerExceptions.tomlFileNotReadable(tomlFile);
        }
        return Optional.of(tomlFile);
      }
    };

  private final TomlConfigurationReader reader = new TomlConfigurationReader();

  @Override
  public BrokerCfg apply(final Environment environment) {
    final Optional<File> tomlFile = tomlFileFromEnv.apply(environment);

    if (tomlFile.isPresent()) {
      log.info("read tomlFile config: {}", tomlFile.get());
      return reader.read(tomlFile.get().getAbsolutePath(), BrokerCfg.class);
    } else {
      log.info("no toml file found, using default");
      return new BrokerCfg();
    }
  }
}
