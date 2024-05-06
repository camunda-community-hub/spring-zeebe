package io.camunda.common.auth;

import io.camunda.common.json.JsonMapper;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class SaaSAuthentication implements Authentication {
  private final JsonMapper jsonMapper;
  private final JwtConfig jwtConfig;
  private final Map<Product, JwtToken> tokens = new HashMap<>();

  public SaaSAuthentication(JwtConfig jwtConfig, JsonMapper jsonMapper) {
    this.jsonMapper = jsonMapper;
    this.jwtConfig = jwtConfig;
  }

  public JwtConfig getJwtConfig() {
    return jwtConfig;
  }

  public static SaaSAuthenticationBuilder builder() {
    return new SaaSAuthenticationBuilder();
  }

  private TokenResponse retrieveToken(Product product, JwtCredential jwtCredential) {
    try (CloseableHttpClient client = HttpClients.createDefault()) {
      HttpPost request = buildRequest(jwtCredential);
      return client.execute(
          request,
          response -> {
            try {
              return jsonMapper.fromJson(
                  EntityUtils.toString(response.getEntity()), TokenResponse.class);
            } catch (Exception e) {
              var errorMessage =
                  String.format(
                      """
              Token retrieval failed from: %s
              Response code: %s
              Audience: %s
              """,
                      jwtCredential.getAuthUrl(), response.getCode(), jwtCredential.getAudience());
              throw new RuntimeException(errorMessage, e);
            }
          });
    } catch (Exception e) {
      throw new RuntimeException(
          "Authenticating for " + product + " failed due to " + e.getMessage(), e);
    }
  }

  private HttpPost buildRequest(JwtCredential jwtCredential) {
    HttpPost httpPost = new HttpPost(jwtCredential.getAuthUrl());
    httpPost.addHeader("Content-Type", "application/json");
    TokenRequest tokenRequest =
        new TokenRequest(
            jwtCredential.getAudience(),
            jwtCredential.getClientId(),
            jwtCredential.getClientSecret());
    httpPost.setEntity(new StringEntity(jsonMapper.toJson(tokenRequest)));
    return httpPost;
  }

  private JwtToken generateToken(Product product, JwtCredential credential) {
    TokenResponse tokenResponse = retrieveToken(product, credential);
    return new JwtToken(
        tokenResponse.getAccessToken(),
        LocalDateTime.now().plusSeconds(tokenResponse.getExpiresIn()));
  }

  @Override
  public Entry<String, String> getTokenHeader(Product product) {
    if (!tokens.containsKey(product) || !isValid(tokens.get(product))) {
      JwtToken newToken = generateToken(product, jwtConfig.getProduct(product));
      tokens.put(product, newToken);
    }
    return authHeader(tokens.get(product).token());
  }

  private Entry<String, String> authHeader(String token) {
    return new AbstractMap.SimpleEntry<>("Authorization", "Bearer " + token);
  }

  private boolean isValid(JwtToken jwtToken) {
    // a token is only counted valid if the expiry is later than in 30 seconds
    return jwtToken.expiry().isAfter(LocalDateTime.now().plusSeconds(30));
  }

  @Override
  public void resetToken(Product product) {
    tokens.remove(product);
  }

  record JwtToken(String token, LocalDateTime expiry) {}
}
