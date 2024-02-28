package io.camunda.zeebe.spring.client.config;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.camunda.common.auth.Authentication;
import io.camunda.common.auth.Product;
import io.camunda.common.auth.SelfManagedAuthentication;
import io.camunda.zeebe.spring.client.configuration.AuthenticationConfiguration;
import io.camunda.zeebe.spring.client.configuration.JsonMapperConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import wiremock.com.fasterxml.jackson.databind.node.JsonNodeFactory;

@SpringBootTest(
    classes = {AuthenticationConfiguration.class, JsonMapperConfiguration.class},
    properties = {
      "camunda.client.auth.client-id=my-client-id",
      "camunda.client.auth.client-secret=my-client-secret",
      "camunda.client.auth.issuer=http://localhost:14682/auth-server"
    })
@ActiveProfiles("camunda-oidc")
@WireMockTest(httpPort = 14682)
public class AuthenticationConfigurationOidcTest {
  private static final String ACCESS_TOKEN =
      "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IlFVVXdPVFpDUTBVM01qZEVRME0wTkRFelJrUkJORFk0T0RZeE1FRTBSa1pFUlVWRVF6bERNZyJ9.eyJodHRwczovL2FwaS5jbG91ZC5jYW11bmRhLmlvL2NsaWVudC9vcmdJZCI6IjM2YmQ4MDAxLTUyZGItNGIzZS05ZWZiLWViMzBhZTQyMDM3YiIsImh0dHBzOi8vYXBpLmNsb3VkLmNhbXVuZGEuaW8vY2xpZW50L3V1aWQiOiJjNDJlMjY3Yy03YWE2LTQ5ZmItYTgyMi0zYmQ0NjgxOGUzNzUiLCJpc3MiOiJodHRwczovL3dlYmxvZ2luLmNsb3VkLmNhbXVuZGEuaW8vIiwic3ViIjoiNGdGMmdvc2xWOEN6TWUyZ0cwVFB1bTNZeDF5TWRld3FAY2xpZW50cyIsImF1ZCI6ImFwaS5jbG91ZC5jYW11bmRhLmlvIiwiaWF0IjoxNzA5MTIxNTIzLCJleHAiOjE3MDkyMDc5MjMsImF6cCI6IjRnRjJnb3NsVjhDek1lMmdHMFRQdW0zWXgxeU1kZXdxIiwic2NvcGUiOiJHZXRXaGl0ZWxpc3RzIFVwZGF0ZVdoaXRlbGlzdHMiLCJndHkiOiJjbGllbnQtY3JlZGVudGlhbHMifQ.VlPxXPFFK3Oc8qfYKnPNmTKB29AdLnFRIDz0lmRJ2rDKdd-91D90-qM4Br11HW9tka2BYyJZG3BGkLugV_W0RgycrjGG4kWMAlI22axtU5h8BGiVHFYmudb8eZSYxDk7SN-872sJ1CSSeYkADuGZ_FoTJFEudunta8dVivtaoNXOvIaY8qS8w0Rmyd5plA6oia96pynJYWnwYELim8AAPNX24zWSZW6N16tPRxXT8YPBhg4a-XHu9gM_Q_8modroCFc8q5xw_aApWpbNxFaffMr2lSB9mK7AqXKBbrDzpL4bhZ7lSZVrF3lWVI0lGoROyQXUBYNvy24i-NHVDU0olA";
  @Autowired Authentication authentication;

  @Test
  void shouldBeSelfManaged() {
    assertThat(authentication).isExactlyInstanceOf(SelfManagedAuthentication.class);
  }

  @Test
  void shouldHaveOperateAuth() {
    stubFor(
        post("/auth-server/protocol/openid-connect/token")
            .willReturn(
                ok().withJsonBody(
                        JsonNodeFactory.instance
                            .objectNode()
                            .put("access_token", ACCESS_TOKEN)
                            .put("expires_in", 300))));
    assertThat(authentication.getTokenHeader(Product.OPERATE))
        .isNotNull()
        .isEqualTo(entry("Authorization", "Bearer " + ACCESS_TOKEN));
  }

  @Test
  void shouldHaveTasklistAuth() {
    stubFor(
        post("/auth-server/protocol/openid-connect/token")
            .willReturn(
                ok().withJsonBody(
                        JsonNodeFactory.instance
                            .objectNode()
                            .put("access_token", ACCESS_TOKEN)
                            .put("expires_in", 300))));
    assertThat(authentication.getTokenHeader(Product.TASKLIST))
        .isNotNull()
        .isEqualTo(entry("Authorization", "Bearer " + ACCESS_TOKEN));
  }

  @Test
  void shouldHaveOptimizeAuth() {
    stubFor(
        post("/auth-server/protocol/openid-connect/token")
            .willReturn(
                ok().withJsonBody(
                        JsonNodeFactory.instance
                            .objectNode()
                            .put("access_token", ACCESS_TOKEN)
                            .put("expires_in", 300))));
    assertThat(authentication.getTokenHeader(Product.OPTIMIZE))
        .isNotNull()
        .isEqualTo(entry("Authorization", "Bearer " + ACCESS_TOKEN));
  }

  @Test
  void shouldHaveZeebeAuth() {
    stubFor(
        post("/auth-server/protocol/openid-connect/token")
            .willReturn(
                ok().withJsonBody(
                        JsonNodeFactory.instance
                            .objectNode()
                            .put("access_token", ACCESS_TOKEN)
                            .put("expires_in", 300))));
    assertThat(authentication.getTokenHeader(Product.OPTIMIZE))
        .isNotNull()
        .isEqualTo(entry("Authorization", "Bearer " + ACCESS_TOKEN));
  }
}
