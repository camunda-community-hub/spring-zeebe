package io.camunda.common.auth;

import io.camunda.common.exception.SdkException;
import io.camunda.common.json.JsonMapper;
import io.camunda.common.json.SdkObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
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
  private JwtConfig jwtConfig;
  private Map<Product, String> tokens;

  // TODO: have a single object mapper to be used all throughout the SDK, i.e.bean injection
  private JsonMapper jsonMapper = new SdkObjectMapper();

  public SaaSAuthentication() {
    tokens = new HashMap<>();
  }

  public static SaaSAuthenticationBuilder builder() {
    return new SaaSAuthenticationBuilder();
  }

  public JwtConfig getJwtConfig() {
    return jwtConfig;
  }

  public void setJwtConfig(JwtConfig jwtConfig) {
    this.jwtConfig = jwtConfig;
  }

  @Override
  public Authentication build() {
    return this;
  }

  @Override
  public void resetToken(Product product) {
    JwtCredential jwtCredential = jwtConfig.getProduct(product);
    retrieveToken(product, jwtCredential);
  }

  private String retrieveToken(Product product, JwtCredential jwtCredential) {
    try {
      HttpPost httpPost = new HttpPost(jwtCredential.getAuthUrl());
      httpPost.addHeader("Content-Type", "application/json");
      TokenRequest tokenRequest = new TokenRequest(jwtCredential.getAudience(), jwtCredential.getClientId(), jwtCredential.getClientSecret());

      httpPost.setEntity(new StringEntity(jsonMapper.toJson(tokenRequest)));
      CloseableHttpClient client = HttpClient.getInstance();
      CloseableHttpResponse response = client.execute(httpPost);
      TokenResponse tokenResponse = jsonMapper.fromJson(EntityUtils.toString(response.getEntity()), TokenResponse.class);
      tokens.put(product, tokenResponse.getAccessToken());
    } catch (Exception e) {
      LOG.error("Authenticating for " + product + " failed due to " + e);
      throw new RuntimeException("Unable to authenticate", e);
    }
    return tokens.get(product);
  }

  @Override
  public Map.Entry<String, String> getTokenHeader(Product product) {
    String token;
    if (tokens.containsKey(product)) {
      token = tokens.get(product);
    } else {
      JwtCredential jwtCredential = jwtConfig.getProduct(product);
      token = retrieveToken(product, jwtCredential);
    }
    return new AbstractMap.SimpleEntry<>("Authorization", "Bearer " + token);
  }
}
