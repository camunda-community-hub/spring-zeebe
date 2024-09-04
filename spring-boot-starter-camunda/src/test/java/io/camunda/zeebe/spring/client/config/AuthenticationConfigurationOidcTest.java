package io.camunda.zeebe.spring.client.config;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.camunda.common.auth.Authentication;
import io.camunda.common.auth.Product;
import io.camunda.common.auth.SelfManagedAuthentication;
import io.camunda.zeebe.spring.client.configuration.AuthenticationConfiguration;
import java.time.Instant;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wiremock.com.fasterxml.jackson.databind.node.JsonNodeFactory;

@SpringBootTest(
    classes = {AuthenticationConfiguration.class},
    properties = {
      "camunda.client.mode=oidc",
      "camunda.client.auth.client-id=my-client-id",
      "camunda.client.auth.client-secret=my-client-secret",
      "camunda.client.auth.issuer=http://localhost:14682/auth-server"
    })
@WireMockTest(httpPort = 14682)
public class AuthenticationConfigurationOidcTest {
  private static final String ACCESS_TOKEN =
      JWT.create().withExpiresAt(Instant.now().plusSeconds(300)).sign(Algorithm.none());
  @Autowired Authentication authentication;

  @Test
  void shouldBeSelfManaged() {
    assertThat(authentication).isExactlyInstanceOf(SelfManagedAuthentication.class);
  }

  @Test
  void shouldHaveOperateAuth() {
    String accessToken = ACCESS_TOKEN;
    stubFor(
        post("/auth-server/protocol/openid-connect/token")
            .willReturn(
                ok().withJsonBody(
                        JsonNodeFactory.instance
                            .objectNode()
                            .put("access_token", accessToken)
                            .put("expires_in", 300))));
    assertThat(authentication.getTokenHeader(Product.OPERATE))
        .isNotNull()
        .isEqualTo(Collections.singletonMap("Authorization", "Bearer " + accessToken));
    verify(
        postRequestedFor(urlEqualTo("/auth-server/protocol/openid-connect/token"))
            .withHeader("Content-Type", equalTo("application/x-www-form-urlencoded")));
  }

  @Test
  void shouldHaveTasklistAuth() {
    String accessToken = ACCESS_TOKEN;
    stubFor(
        post("/auth-server/protocol/openid-connect/token")
            .willReturn(
                ok().withJsonBody(
                        JsonNodeFactory.instance
                            .objectNode()
                            .put("access_token", accessToken)
                            .put("expires_in", 300))));
    assertThat(authentication.getTokenHeader(Product.TASKLIST))
        .isNotNull()
        .isEqualTo(Collections.singletonMap("Authorization", "Bearer " + accessToken));
    verify(
        postRequestedFor(urlEqualTo("/auth-server/protocol/openid-connect/token"))
            .withHeader("Content-Type", equalTo("application/x-www-form-urlencoded")));
  }

  @Test
  void shouldHaveOptimizeAuth() {
    String accessToken = ACCESS_TOKEN;
    stubFor(
        post("/auth-server/protocol/openid-connect/token")
            .willReturn(
                ok().withJsonBody(
                        JsonNodeFactory.instance
                            .objectNode()
                            .put("access_token", accessToken)
                            .put("expires_in", 300))));
    assertThat(authentication.getTokenHeader(Product.OPTIMIZE))
        .isNotNull()
        .isEqualTo(Collections.singletonMap("Authorization", "Bearer " + accessToken));
    verify(
        postRequestedFor(urlEqualTo("/auth-server/protocol/openid-connect/token"))
            .withHeader("Content-Type", equalTo("application/x-www-form-urlencoded")));
  }

  @Test
  void shouldHaveZeebeAuth() {
    String accessToken = ACCESS_TOKEN;
    stubFor(
        post("/auth-server/protocol/openid-connect/token")
            .willReturn(
                ok().withJsonBody(
                        JsonNodeFactory.instance
                            .objectNode()
                            .put("access_token", accessToken)
                            .put("expires_in", 300))));
    assertThat(authentication.getTokenHeader(Product.ZEEBE))
        .isNotNull()
        .isEqualTo(Collections.singletonMap("Authorization", "Bearer " + accessToken));
    verify(
        postRequestedFor(urlEqualTo("/auth-server/protocol/openid-connect/token"))
            .withHeader("Content-Type", equalTo("application/x-www-form-urlencoded")));
  }
}
