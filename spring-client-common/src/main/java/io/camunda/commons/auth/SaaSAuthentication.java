package io.camunda.commons.auth;

import io.camunda.commons.exception.SdkException;
import io.camunda.commons.json.JsonMapper;
import io.camunda.commons.json.SdkObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class SaaSAuthentication extends JwtAuthentication {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private String authUrl;
  private JwtConfig jwtConfig;
  private Map<Product, String> tokens;
  private Map<Product, LocalDateTime> expirations;

  // TODO: have a single object mapper to be used all throughout the SDK, i.e.bean injection
  private JsonMapper jsonMapper = new SdkObjectMapper();

  public SaaSAuthentication() {
    tokens = new HashMap<>();
    expirations = new HashMap<>();
  }

  public static SaaSAuthenticationBuilder builder() {
    return new SaaSAuthenticationBuilder();
  }

  public void jwtConfig(JwtConfig jwtConfig) {
    this.jwtConfig = jwtConfig;
  }

  @Override
  public Authentication build() {
    authUrl = "https://login.cloud.camunda.io/oauth/token";
    jwtConfig.getMap().forEach(this::retrieveToken);
    return this;
  }

  private void retrieveToken(Product product, Credential credential) {
    try {
      HttpPost httpPost = new HttpPost(authUrl);
      httpPost.addHeader("Content-Type", "application/json");
      TokenRequest tokenRequest = new TokenRequest(getAudience(product), credential.clientId, credential.clientSecret);

      httpPost.setEntity(new StringEntity(jsonMapper.toJson(tokenRequest)));
      CloseableHttpClient client = HttpClients.createDefault();
      CloseableHttpResponse response = client.execute(httpPost);
      TokenResponse tokenResponse = jsonMapper.fromJson(EntityUtils.toString(response.getEntity()), TokenResponse.class);
      tokens.put(product, tokenResponse.getAccessToken());
      expirations.put(product, LocalDateTime.now().plusSeconds(tokenResponse.getExpiresIn()));
    } catch (Exception e) {
      LOG.warn("Authenticating for " + product + " failed due to " + e);
      throw new RuntimeException("Unable to authenticate");
    }
  }

  private void retrieveToken(Product product) {
    Credential credential = jwtConfig.getMap().get(product);
    retrieveToken(product, credential);
  }

  private String getAudience(Product product) {
    switch (product) {
      case OPERATE:
        return "operate.camunda.io";
      case TASKLIST:
        return "tasklist.camunda.io";
      case OPTIMIZE:
        return "optimize.camunda.io";
      case CONSOLE:
        return "console.camunda.io";
      case ZEEBE:
        return "zeebe.camunda.io";
      default:
        throw new SdkException("Unable to get audience because product is invalid");
    }
  }

  @Override
  public Map.Entry<String, String> getTokenHeader(Product product) {
    refreshToken();
    return new AbstractMap.SimpleEntry<>("Authorization", "Bearer " + tokens.get(product));
  }

  private void refreshToken() {
    expirations.forEach((product, expiration) -> {
      if (expiration.isAfter(LocalDateTime.now())) {
        retrieveToken(product);
      }
    });
  }
}
