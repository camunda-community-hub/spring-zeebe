package io.camunda.common.auth;

import io.camunda.common.auth.identity.IdentityConfig;
import io.camunda.identity.sdk.Identity;
import io.camunda.identity.sdk.authentication.Tokens;
import java.lang.invoke.MethodHandles;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelfManagedAuthentication extends JwtAuthentication {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private JwtConfig jwtConfig;
  private IdentityConfig identityConfig;
  private Map<Product, String> tokens;

  public SelfManagedAuthentication() {
    tokens = new HashMap<>();
  }

  public static SelfManagedAuthenticationBuilder builder() {
    return new SelfManagedAuthenticationBuilder();
  }

  public JwtConfig getJwtConfig() {
    return jwtConfig;
  }

  public void setJwtConfig(JwtConfig jwtConfig) {
    this.jwtConfig = jwtConfig;
  }

  public void setIdentityConfig(IdentityConfig identityConfig) {
    this.identityConfig = identityConfig;
  }

  @Override
  public Authentication build() {
    return this;
  }

  @Override
  public void resetToken(Product product) {
    tokens.remove(product);
  }

  @Override
  public Map.Entry<String, String> getTokenHeader(Product product) {
    String token;
    if (tokens.containsKey(product)) {
      token = tokens.get(product);
    } else {
      token = getIdentityToken(product);
      saveToken(product, token);
    }
    return new AbstractMap.SimpleEntry<>("Authorization", "Bearer " + token);
  }

  private String getIdentityToken(Product product) {
    Identity identity = identityConfig.get(product).getIdentity();
    String audience = jwtConfig.getProduct(product).getAudience();
    Tokens identityTokens = identity.authentication().requestToken(audience);
    return identityTokens.getAccessToken();
  }

  private void saveToken(Product product, String token) {
    tokens.put(product, token);
  }
}
