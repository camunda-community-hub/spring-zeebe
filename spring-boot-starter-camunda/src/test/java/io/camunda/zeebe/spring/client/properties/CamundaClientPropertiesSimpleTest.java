package io.camunda.zeebe.spring.client.properties;

import static io.camunda.zeebe.spring.client.properties.CamundaClientProperties.ClientMode.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = CamundaClientPropertiesTestConfig.class)
@ActiveProfiles("camunda-simple")
public class CamundaClientPropertiesSimpleTest {
  @Autowired CamundaClientProperties properties;

  @Test
  void shouldLoadDefaults_simple() {
    assertThat(properties.getMode()).isEqualTo(simple);
    assertThat(properties.getAuth().getUsername()).isEqualTo("demo");
    assertThat(properties.getAuth().getPassword()).isEqualTo("demo");
    assertThat(properties.getZeebe().getBaseUrl().toString()).isEqualTo("http://localhost:26500");
    assertThat(properties.getZeebe().getEnabled()).isEqualTo(true);
    assertThat(properties.getOperate().getBaseUrl().toString()).isEqualTo("http://localhost:8081");
    assertThat(properties.getOperate().getEnabled()).isEqualTo(true);
    assertThat(properties.getTasklist().getBaseUrl().toString()).isEqualTo("http://localhost:8082");
    assertThat(properties.getTasklist().getEnabled()).isEqualTo(true);
    assertThat(properties.getOptimize().getEnabled()).isEqualTo(false);
    assertThat(properties.getIdentity().getEnabled()).isEqualTo(false);
  }
}
