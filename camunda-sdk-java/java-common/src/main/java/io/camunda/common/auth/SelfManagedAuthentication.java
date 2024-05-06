package io.camunda.common.auth;

import io.camunda.common.auth.identity.IdentityConfig;
import io.camunda.common.auth.identity.IdentityContainer;
import io.camunda.identity.sdk.authentication.Tokens;
import java.util.AbstractMap;
import java.util.Map.Entry;

public class SelfManagedAuthentication implements Authentication {

  private final IdentityConfig identityConfig;

  public SelfManagedAuthentication(IdentityConfig identityConfig) {
    this.identityConfig = identityConfig;
  }

  public IdentityConfig getIdentityConfig() {
    return identityConfig;
  }

  public static SelfManagedAuthenticationBuilder builder() {
    return new SelfManagedAuthenticationBuilder();
  }

  @Override
  public Entry<String, String> getTokenHeader(Product product) {
    Tokens tokens = getToken(product);
    return authHeader(tokens.getAccessToken());
  }

  private Tokens getToken(Product product) {
    IdentityContainer identityContainer = identityConfig.get(product);
    String audience = identityContainer.getIdentityConfiguration().getAudience();
    return identityContainer.getIdentity().authentication().requestToken(audience);
  }

  @Override
  public void resetToken(Product product) {
    Tokens token = getToken(product);
    identityConfig.get(product).getIdentity().authentication().revokeToken(token.getRefreshToken());
  }

  private Entry<String, String> authHeader(String token) {
    return new AbstractMap.SimpleEntry<>("Authorization", "Bearer " + token);
  }
}
