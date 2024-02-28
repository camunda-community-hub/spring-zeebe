package io.camunda.zeebe.spring.client.properties;

import static org.assertj.core.api.Assertions.*;

import io.camunda.zeebe.spring.client.properties.CamundaClientProperties.ClientMode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    classes = CamundaClientPropertiesTestConfig.class,
    properties = {"camunda.client.cluster-id=my-cluster-id", "camunda.client.region=bru-2"})
@ActiveProfiles("camunda-saas")
public class CamundaClientPropertiesSaasTest {
  @Autowired CamundaClientProperties properties;

  @Test
  void shouldPopulateBaseUrlsForSaas() {
    assertThat(properties.getZeebe().getBaseUrl().toString())
        .isEqualTo("https://my-cluster-id.bru-2.zeebe.camunda.io");
    assertThat(properties.getOperate().getBaseUrl().toString())
        .isEqualTo("https://bru-2.operate.camunda.io/my-cluster-id");
    assertThat(properties.getTasklist().getBaseUrl().toString())
        .isEqualTo("https://bru-2.tasklist.camunda.io/my-cluster-id");
    assertThat(properties.getOptimize().getBaseUrl().toString())
        .isEqualTo("https://bru-2.optimize.camunda.io/my-cluster-id");
  }

  @Test
  void shouldLoadDefaults_saas() {
    assertThat(properties.getMode()).isEqualTo(ClientMode.saas);
    assertThat(properties.getAuth().getIssuer())
        .isEqualTo("https://login.cloud.camunda.io/oauth/token");
    assertThat(properties.getZeebe().getEnabled()).isEqualTo(true);
    assertThat(properties.getZeebe().getAudience()).isEqualTo("zeebe.camunda.io");
    assertThat(properties.getOperate().getEnabled()).isEqualTo(true);
    assertThat(properties.getOperate().getAudience()).isEqualTo("operate.camunda.io");
    assertThat(properties.getTasklist().getEnabled()).isEqualTo(true);
    assertThat(properties.getTasklist().getAudience()).isEqualTo("tasklist.camunda.io");
    assertThat(properties.getOptimize().getEnabled()).isEqualTo(true);
    assertThat(properties.getIdentity().getEnabled()).isEqualTo(false);
  }
}
