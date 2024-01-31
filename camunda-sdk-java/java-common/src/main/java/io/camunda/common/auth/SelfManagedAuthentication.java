package io.camunda.common.auth;

import io.camunda.common.auth.identity.IdentityConfig;
import io.camunda.common.exception.SdkException;
import io.camunda.identity.sdk.Identity;
import io.camunda.identity.sdk.authentication.Tokens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class SelfManagedAuthentication extends JwtAuthentication {

  private static class Token {

    public static final long EXPIRATION_BUFFER = 60 * 1000; // 1 minute
    private final String accessToken;
    private final long expiresAtMillis;

    public Token(String accessToken, long expiresInSeconds) {
      this.accessToken = accessToken;
      expiresAtMillis = System.currentTimeMillis() + expiresInSeconds * 1000 - EXPIRATION_BUFFER;
    }

    public String getAccessToken() {
      return accessToken;
    }

    public boolean isExpired() {
      return expiresAtMillis < System.currentTimeMillis();
    }
  }

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private JwtConfig jwtConfig;
  private IdentityConfig identityConfig;
  private final Map<Product, Token> tokens;

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
    Token token = tokens.computeIfAbsent(product, k -> getIdentityToken(product));
    if (token.isExpired()) {
      LOG.debug("Token for product {} is expired. Requesting new token", product);
      token = getIdentityToken(product);
      saveToken(product, token);
    }
    return new AbstractMap.SimpleEntry<>("Authorization", "Bearer " + token.getAccessToken());
  }

  private Token getIdentityToken(Product product) {
    Identity identity = identityConfig.get(product).getIdentity();
    String audience = jwtConfig.getProduct(product).getAudience();
    Tokens identityTokens = identity.authentication().requestToken(audience);
    if (identityTokens.getAccessToken() == null) {
      throw new SdkException("Unable to get access token from identity");
    }
    LOG.debug("Received new token for product {}", product);
    return new Token(identityTokens.getAccessToken(), identityTokens.getExpiresIn());
  }

  private void saveToken(Product product, Token token) {
    tokens.put(product, token);
  }
}
