package io.zeebe.spring.broker.fn;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import io.zeebe.broker.system.configuration.BrokerCfg;
import org.junit.Test;
import org.springframework.core.env.Environment;

public class ReadTomlFileTest {

  private final ReadTomlFile readTomlFile = new ReadTomlFile();

  @Test
  public void use_default_brokerCfg_when_toml_file_is_not_given() {
    final BrokerCfg brokerCfg = readTomlFile.apply(mock(Environment.class));

    assertThat(brokerCfg).isNotNull();
    assertThat(brokerCfg.getNetwork().getCommandApi().getPort())
      .isEqualTo(26501);
  }
}
