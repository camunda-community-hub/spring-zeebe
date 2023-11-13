package io.camunda.commons.auth;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

import static io.camunda.commons.http.JsonUtils.toJson;
import static io.camunda.commons.http.JsonUtils.toResult;

public class SaaSAuthentication extends JwtAuthentication {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private String authUrl;
  private JwtConfig jwtConfig;
  private Map<Product, String> tokens;
  private Map<Product, Integer> expirations;

  public SaaSAuthentication() {
    tokens = new HashMap<>();
    expirations = new HashMap<>();
  }

  public SaaSAuthentication jwtConfig(JwtConfig jwtConfig) {
    this.jwtConfig = jwtConfig;
    return this;
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

      httpPost.setEntity(new StringEntity(toJson(tokenRequest)));
      CloseableHttpClient client = HttpClients.createDefault();
      CloseableHttpResponse response = client.execute(httpPost);
      TokenResponse tokenResponse = toResult(EntityUtils.toString(response.getEntity()), TokenResponse.class);
      tokens.put(product, tokenResponse.getAccessToken());
      expirations.put(product, (int) (System.currentTimeMillis()/1000 + tokenResponse.getExpiresIn()));
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
    String audience = null;
    switch (product) {
      case OPERATE:
        audience = "operate.camunda.io";
        break;
      case TASKLIST:
        audience = "tasklist.camunda.io";
        break;
      case OPTIMIZE:
        audience = "optimize.camunda.io";
        break;
      case CONSOLE:
        audience = "console.camunda.io";
        break;
      case ZEEBE:
        audience = "zeebe.camunda.io";
        break;
    }
    return audience;
  }

  @Override
  public Map.Entry<String, String> getTokenHeader(Product product) {
    refreshToken();
    return new AbstractMap.SimpleEntry<>("Authorization", "Bearer " + tokens.get(product));
  }

  private void refreshToken() {
    expirations.forEach((product, expiration) -> {
      if (expiration < System.currentTimeMillis()/1000) {
        retrieveToken(product);
      }
    });
  }
}