package io.camunda.zeebe.spring.client.config;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.camunda.common.auth.Authentication;
import io.camunda.common.auth.Product;
import io.camunda.common.auth.SimpleAuthentication;
import io.camunda.zeebe.spring.client.configuration.AuthenticationConfiguration;
import io.camunda.zeebe.spring.client.configuration.JsonMapperConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    classes = {AuthenticationConfiguration.class, JsonMapperConfiguration.class},
    properties = {
      "camunda.client.operate.base-url=http://localhost:15864",
      "camunda.client.tasklist.base-url=http://localhost:15864"
    })
@ActiveProfiles("camunda-simple")
@WireMockTest(httpPort = 15864)
public class AuthenticationConfigurationSimpleTest {
  @Autowired Authentication authentication;

  @Test
  void shouldBeSimple() {
    assertThat(authentication).isExactlyInstanceOf(SimpleAuthentication.class);
  }

  @Test
  void shouldHaveOperateAuth() {
    stubFor(
        post("/api/login")
            .willReturn(
                ok().withHeader("Set-Cookie", "OPERATE-SESSION=3205A03818447100591792E774DB8AF6")));
    assertThat(authentication.getTokenHeader(Product.OPERATE))
        .isNotNull()
        .isEqualTo(entry("Cookie", "OPERATE-SESSION=3205A03818447100591792E774DB8AF6"));
  }

  @Test
  void shouldHaveTasklistAuth() {
    stubFor(
        post("/api/login")
            .willReturn(
                ok().withHeader(
                        "Set-Cookie", "TASKLIST-SESSION=3205A03818447100591792E774DB8AF6")));
    assertThat(authentication.getTokenHeader(Product.TASKLIST))
        .isNotNull()
        .isEqualTo(entry("Cookie", "TASKLIST-SESSION=3205A03818447100591792E774DB8AF6"));
  }
}
