package io.zeebe.spring.broker.config;

import io.zeebe.broker.system.ConfigurationManager;
import io.zeebe.broker.system.ConfigurationManagerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.Optional;
import java.util.function.Function;

/**
 * Included by {@link io.zeebe.spring.broker.EnableZeebeBroker} annotation.
 */
@Slf4j
public class ZeebeBrokerConfiguration {

    static Function<Environment, Optional<String>> tomlFileFromEnv = environment -> {
        String[] args = environment.getProperty("nonOptionArgs", String[].class, new String[0]);
        if (args == null || args.length == 0) {
            return Optional.empty();
        } else if (args.length > 1) {
            throw new IllegalArgumentException("requires exactly one cli argument, the tomlFile.");
        } else {
            return Optional.of(args[0]);
        }

    };

    @Bean
    public ConfigurationManager configurationManager(final Environment environment) {
        Optional<String> tomlFile = tomlFileFromEnv.apply(environment);

        log.info("building broker from tomlFile={}", tomlFile);

        return new ConfigurationManagerImpl(tomlFile.orElse(null));
    }

    @Bean
    public SpringZeebeBroker springBroker(final ConfigurationManager configurationManager) {
        return new SpringZeebeBroker(configurationManager);
    }

}
