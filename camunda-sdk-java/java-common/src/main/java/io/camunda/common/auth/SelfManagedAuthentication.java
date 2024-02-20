package io.camunda.common.auth;

import io.camunda.common.exception.SdkException;
import io.camunda.common.json.JsonMapper;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelfManagedAuthentication extends JwtAuthentication {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final String authUrl;
  private final JsonMapper jsonMapper;

  public SelfManagedAuthentication(JwtConfig jwtConfig, String authUrl, JsonMapper jsonMapper) {
    super(jwtConfig);
    this.authUrl = authUrl;
    this.jsonMapper = jsonMapper;
  }

  public static SelfManagedAuthenticationBuilder builder() {
    return new SelfManagedAuthenticationBuilder();
  }

  private TokenResponse retrieveToken(Product product, JwtCredential jwtCredential) {
    try (CloseableHttpClient client = HttpClients.createDefault()) {
      HttpPost request = buildRequest(jwtCredential);
      return client.execute(
          request,
          response -> {
            if (response.getCode() == HttpStatus.SC_OK) {
              return jsonMapper.fromJson(
                  EntityUtils.toString(response.getEntity()), TokenResponse.class);
            } else {
              throw new SdkException(
                  "Error "
                      + response.getCode()
                      + " obtaining access token: "
                      + EntityUtils.toString(response.getEntity()));
            }
          });
    } catch (Exception e) {
      LOG.error("Authenticating for " + product + " failed due to " + e);
      throw new SdkException("Unable to authenticate", e);
    }
  }

  private HttpPost buildRequest(JwtCredential jwtCredential) {
    HttpPost httpPost = new HttpPost(authUrl);
    httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");

    Map<String, String> parameters = new HashMap<>();
    parameters.put("grant_type", "client_credentials");
    parameters.put("client_id", jwtCredential.getClientId());
    parameters.put("client_secret", jwtCredential.getClientSecret());

    String form =
        parameters.entrySet().stream()
            .map(
                e -> {
                  try {
                    return e.getKey()
                        + "="
                        + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8.toString());
                  } catch (UnsupportedEncodingException ex) {
                    throw new RuntimeException(ex);
                  }
                })
            .collect(Collectors.joining("&"));

    httpPost.setEntity(new StringEntity(form));

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
