package io.camunda.common.auth;

import io.camunda.common.json.JsonMapper;
import io.camunda.common.json.SdkObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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
  private JwtConfig jwtConfig;
  private Map<Product, String> tokens;
  private Map<Product, LocalDateTime> expirations;

  // TODO: have a single object mapper to be used all throughout the SDK, i.e.bean injection
  private JsonMapper jsonMapper = new SdkObjectMapper();

  public SelfManagedAuthentication() {
    tokens = new HashMap<>();
    expirations = new HashMap<>();
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

  public void setJwtConfig(JwtConfig jwtConfig) {
    this.jwtConfig = jwtConfig;
  }

  @Override
  public Authentication build() {
    authUrl = keycloakUrl+"/auth/realms/"+keycloakRealm+"/protocol/openid-connect/token";
    jwtConfig.getMap().forEach(this::retrieveToken);
    return this;
  }

  private void retrieveToken(Product product, JwtCredential jwtCredential) {
    try {
      HttpPost httpPost = new HttpPost(authUrl);
      httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");

      Map<String, String> parameters = new HashMap<>();
      parameters.put("grant_type", "client_credentials");
      parameters.put("client_id", jwtCredential.clientId);
      parameters.put("client_secret", jwtCredential.clientSecret);

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
      CloseableHttpClient client = HttpClient.getInstance();
      CloseableHttpResponse response = client.execute(httpPost);
      TokenResponse tokenResponse =  jsonMapper.fromJson(EntityUtils.toString(response.getEntity()), TokenResponse.class);
      // TODO: verify JWT has the desired permission vs what the user requested
      tokens.put(product, tokenResponse.getAccessToken());
      expirations.put(product, LocalDateTime.now().plusSeconds(tokenResponse.getExpiresIn()));
    } catch (Exception e) {
      LOG.warn("Authenticating for " + product + " failed due to " + e);
      throw new RuntimeException("Unable to authenticate", e);
    }
  }

  private void retrieveToken(Product product) {
    JwtCredential jwtCredential = jwtConfig.getMap().get(product);
    retrieveToken(product, jwtCredential);
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
