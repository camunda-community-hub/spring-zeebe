package io.zeebe.spring.broker.config;

import io.zeebe.broker.Broker;
import io.zeebe.broker.system.configuration.BrokerCfg;
import io.zeebe.broker.system.configuration.TopicCfg;
import io.zeebe.spring.broker.ZeebeBrokerObjectFactory;
import io.zeebe.spring.broker.fn.ReadTomlFile;
import java.nio.file.Files;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

@Slf4j
@RequiredArgsConstructor
public class TempZeebeBrokerFactory implements ZeebeBrokerObjectFactory {

  private Broker broker;

  private final Environment environment;
  private final ReadTomlFile readTomlFile = new ReadTomlFile();

  @SneakyThrows
  @Override
  public Broker getObject() {
    if (broker != null) {
      // singleton instance
      return broker;
    }

    final String tempFolder =
      Files.createTempDirectory("zeebe-").toAbsolutePath().normalize().toString();

    log.info("broker dir: {}", tempFolder);

    final BrokerCfg cfg = readTomlFile.apply(environment);
    cfg.setBootstrap(1);

    // TODO make default topic configurable
    final TopicCfg topicCfg = new TopicCfg();
    topicCfg.setName("default-topic");
    topicCfg.setPartitions(1);
    topicCfg.setReplicationFactor(1);

    cfg.getTopics().add(topicCfg);

    return broker = new Broker(cfg, tempFolder, null);
  }
}
