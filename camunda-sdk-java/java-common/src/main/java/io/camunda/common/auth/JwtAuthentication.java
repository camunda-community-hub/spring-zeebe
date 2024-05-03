package io.camunda.common.auth;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JwtAuthentication implements Authentication {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final JwtConfig jwtConfig;
  private final Map<Product, JwtToken> tokens = new HashMap<>();

  protected JwtAuthentication(JwtConfig jwtConfig) {
    this.jwtConfig = jwtConfig;
  }

  public JwtConfig getJwtConfig() {
    return jwtConfig;
  }

  @Override
  public final void resetToken(Product product) {
    tokens.remove(product);
  }

  @Override
  public final Entry<String, String> getTokenHeader(Product product) {
    if (!tokens.containsKey(product) || !isValid(tokens.get(product))) {
      JwtToken newToken = generateToken(product, jwtConfig.getProduct(product));
      tokens.put(product, newToken);
    }
    return authHeader(tokens.get(product).getToken());
  }

  protected abstract JwtToken generateToken(Product product, JwtCredential credential);

  private Entry<String, String> authHeader(String token) {
    return new AbstractMap.SimpleEntry<>("Authorization", "Bearer " + token);
  }

  private boolean isValid(JwtToken jwtToken) {
    // a token is only counted valid if the expiry is later than in 30 seconds
    return jwtToken.getExpiry().isAfter(LocalDateTime.now().plusSeconds(30));
  }

  protected static class JwtToken {
    private final String token;
    private final LocalDateTime expiry;

    public JwtToken(String token, LocalDateTime expiry) {
      this.token = token;
      this.expiry = expiry;
    }

    public String getToken() {
      return token;
    }

    public LocalDateTime getExpiry() {
      return expiry;
    }
  }
}
