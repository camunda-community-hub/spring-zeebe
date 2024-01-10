package io.camunda.common.auth;

import io.camunda.common.exception.SdkException;
import io.camunda.common.json.JsonMapper;
import io.camunda.common.json.SdkObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SelfManagedAuthentication extends JwtAuthentication {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private String authUrl;

  // TODO: Check with Identity about upcoming IDPs to abstract this
  private String keycloakRealm = "camunda-platform";
  private String keycloakUrl;
  private String keycloakTokenUrl;
  private JwtConfig jwtConfig;
  private Map<Product, String> tokens;

  // TODO: have a single object mapper to be used all throughout the SDK, i.e.bean injection
  private JsonMapper jsonMapper = new SdkObjectMapper();

  public SelfManagedAuthentication() {
    tokens = new HashMap<>();
  }

  public static SelfManagedAuthenticationBuilder builder() {
    return new SelfManagedAuthenticationBuilder();
  }

  public void setKeycloakRealm(String keycloakRealm) {
    this.keycloakRealm = keycloakRealm;
  }

  public void setKeycloakUrl(String keycloakUrl) {
    this.keycloakUrl = keycloakUrl;
  }

  public void setKeycloakTokenUrl(String keycloakTokenUrl) {
    this.keycloakTokenUrl = keycloakTokenUrl;
  }

  public JwtConfig getJwtConfig() {
    return jwtConfig;
  }

  public void setJwtConfig(JwtConfig jwtConfig) {
    this.jwtConfig = jwtConfig;
  }

  @Override
  public Authentication build() {
    if (keycloakTokenUrl != null) {
      authUrl = keycloakTokenUrl;
    } else {
      authUrl = keycloakUrl+"/auth/realms/"+keycloakRealm+"/protocol/openid-connect/token";
    }
    return this;
  }

  @Override
  public void resetToken(Product product) {
    tokens.remove(product);
  }

  private String retrieveToken(Product product, JwtCredential jwtCredential) {
    try(CloseableHttpClient client = HttpClient.getInstance()) {
      HttpPost request = buildRequest(jwtCredential);
      TokenResponse tokenResponse =
          client.execute(
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
      tokens.put(product, tokenResponse.getAccessToken());
    } catch (Exception e) {
      LOG.error("Authenticating for " + product + " failed due to " + e);
      throw new SdkException("Unable to authenticate", e);
    }
    return tokens.get(product);
  }

  private HttpPost buildRequest(JwtCredential jwtCredential) {
    HttpPost httpPost = new HttpPost(authUrl);
    httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");

    Map<String, String> parameters = new HashMap<>();
    parameters.put("grant_type", "client_credentials");
    parameters.put("client_id", jwtCredential.getClientId());
    parameters.put("client_secret", jwtCredential.getClientSecret());

    String form = parameters.entrySet()
      .stream()
      .map(e -> {
        try {
          return e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
          throw new RuntimeException(ex);
        }
      })
      .collect(Collectors.joining("&"));

    httpPost.setEntity(new StringEntity(form));

    return  httpPost;
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
