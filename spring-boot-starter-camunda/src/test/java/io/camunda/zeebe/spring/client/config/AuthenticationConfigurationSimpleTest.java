package io.camunda.zeebe.spring.client.config;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.camunda.common.auth.Authentication;
import io.camunda.common.auth.Product;
import io.camunda.common.auth.SimpleAuthentication;
import io.camunda.zeebe.spring.client.configuration.AuthenticationConfiguration;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = {AuthenticationConfiguration.class},
    properties = {
      "camunda.client.mode=simple",
      "camunda.client.operate.base-url=http://localhost:15864",
      "camunda.client.tasklist.base-url=http://localhost:15864"
    })
@WireMockTest(httpPort = 15864)
public class AuthenticationConfigurationSimpleTest {
  @Autowired Authentication authentication;

  @Test
  void shouldBeSimple() {
    assertThat(authentication).isExactlyInstanceOf(SimpleAuthentication.class);
  }

  @BeforeEach
  void setUp() {
    authentication.resetToken(Product.OPERATE);
  }

  @Test
  void shouldHaveOperateAuth() {
    stubFor(
        post("/api/login")
            .willReturn(
                ok().withHeader("Set-Cookie", "OPERATE-SESSION=3205A03818447100591792E774DB8AF6")
                    .withHeader(
                        "Set-Cookie",
                        "OPERATE-X-CSRF-TOKEN=139196d4-7768-451c-aa66-078e1ed74785")));
    assertThat(authentication.getTokenHeader(Product.OPERATE))
        .isNotNull()
        .isEqualTo(
            Map.of(
                "Cookie",
                "OPERATE-SESSION=3205A03818447100591792E774DB8AF6; OPERATE-X-CSRF-TOKEN=139196d4-7768-451c-aa66-078e1ed74785"));
    verify(
        postRequestedFor(urlEqualTo("/api/login"))
            .withHeader(
                "Content-Type", equalTo("application/x-www-form-urlencoded; charset=ISO-8859-1")));
  }

  @Test
  void shouldHaveOperateAuthNew() {
    stubFor(
        post("/api/login")
            .willReturn(
                ok().withHeader("Set-Cookie", "OPERATE-SESSION=3205A03818447100591792E774DB8AF6")
                    .withHeader(
                        "Set-Cookie", "X-CSRF-TOKEN=139196d4-7768-451c-aa66-078e1ed74785")));
    assertThat(authentication.getTokenHeader(Product.OPERATE))
        .isNotNull()
        .isEqualTo(
            Map.of(
                "Cookie",
                "OPERATE-SESSION=3205A03818447100591792E774DB8AF6; X-CSRF-TOKEN=139196d4-7768-451c-aa66-078e1ed74785"));
    verify(
        postRequestedFor(urlEqualTo("/api/login"))
            .withHeader(
                "Content-Type", equalTo("application/x-www-form-urlencoded; charset=ISO-8859-1")));
  }

  @Test
  void shouldHaveTasklistAuth() {
    stubFor(
        post("/api/login")
            .willReturn(
                ok().withHeader("Set-Cookie", "TASKLIST-SESSION=3205A03818447100591792E774DB8AF6")
                    .withHeader(
                        "Set-Cookie",
                        "TASKLIST-X-CSRF-TOKEN=139196d4-7768-451c-aa66-078e1ed74785")));
    assertThat(authentication.getTokenHeader(Product.TASKLIST))
        .isNotNull()
        .isEqualTo(
            Map.of(
                "Cookie",
                "TASKLIST-SESSION=3205A03818447100591792E774DB8AF6; TASKLIST-X-CSRF-TOKEN=139196d4-7768-451c-aa66-078e1ed74785"));
    verify(
        postRequestedFor(urlEqualTo("/api/login"))
            .withHeader(
                "Content-Type", equalTo("application/x-www-form-urlencoded; charset=ISO-8859-1")));
  }

  @Test
  void shouldHaveCSRFToken() {
    stubFor(
        post("/api/login")
            .willReturn(
                ok().withHeader(
                        "Set-Cookie", "OPERATE-X-CSRF-TOKEN=139196d4-7768-451c-aa66-078e1ed74785")
                    .withHeader(
                        "OPERATE-X-CSRF-TOKEN",
                        "WwbfQ33kHNEHu99ioC39yCMuVE2JjQnK_vYEpGPQGxBv1Nfn10pSDzapEpzfcJJvbiE2kG6jDMbFVVBSMy9K_K5dlV1")));

    assertThat(authentication.getTokenHeader(Product.OPERATE))
        .isNotNull()
        .isEqualTo(
            Map.of(
                "Cookie",
                "OPERATE-X-CSRF-TOKEN=139196d4-7768-451c-aa66-078e1ed74785",
                "OPERATE-X-CSRF-TOKEN",
                "WwbfQ33kHNEHu99ioC39yCMuVE2JjQnK_vYEpGPQGxBv1Nfn10pSDzapEpzfcJJvbiE2kG6jDMbFVVBSMy9K_K5dlV1"));
    verify(
        postRequestedFor(urlEqualTo("/api/login"))
            .withHeader(
                "Content-Type", equalTo("application/x-www-form-urlencoded; charset=ISO-8859-1")));
  }

  @Test
  void shouldHaveCSRFTokenNew() {
    stubFor(
        post("/api/login")
            .willReturn(
                ok().withHeader("Set-Cookie", "X-CSRF-TOKEN=139196d4-7768-451c-aa66-078e1ed74785")
                    .withHeader(
                        "X-CSRF-TOKEN",
                        "WwbfQ33kHNEHu99ioC39yCMuVE2JjQnK_vYEpGPQGxBv1Nfn10pSDzapEpzfcJJvbiE2kG6jDMbFVVBSMy9K_K5dlV1")));

    assertThat(authentication.getTokenHeader(Product.OPERATE))
        .isNotNull()
        .isEqualTo(
            Map.of(
                "Cookie",
                "X-CSRF-TOKEN=139196d4-7768-451c-aa66-078e1ed74785",
                "X-CSRF-TOKEN",
                "WwbfQ33kHNEHu99ioC39yCMuVE2JjQnK_vYEpGPQGxBv1Nfn10pSDzapEpzfcJJvbiE2kG6jDMbFVVBSMy9K_K5dlV1"));
    verify(
        postRequestedFor(urlEqualTo("/api/login"))
            .withHeader(
                "Content-Type", equalTo("application/x-www-form-urlencoded; charset=ISO-8859-1")));
  }
}
