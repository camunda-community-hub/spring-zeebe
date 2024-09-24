package io.camunda.common.auth;

import io.camunda.common.json.JsonMapper;
import java.time.LocalDateTime;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class SaaSAuthentication extends JwtAuthentication {

  private final JsonMapper jsonMapper;

  public SaaSAuthentication(JwtConfig jwtConfig, JsonMapper jsonMapper) {
    super(jwtConfig);
    this.jsonMapper = jsonMapper;
  }

  public static SaaSAuthenticationBuilder builder() {
    return new SaaSAuthenticationBuilder();
  }

  private TokenResponse retrieveToken(Product product, JwtCredential jwtCredential) {
    try (CloseableHttpClient client = HttpClients.createSystem()) {
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

  @Override
  protected JwtToken generateToken(Product product, JwtCredential credential) {
    TokenResponse tokenResponse = retrieveToken(product, credential);
    return new JwtToken(
        tokenResponse.getAccessToken(),
        LocalDateTime.now().plusSeconds(tokenResponse.getExpiresIn()));
  }
}
