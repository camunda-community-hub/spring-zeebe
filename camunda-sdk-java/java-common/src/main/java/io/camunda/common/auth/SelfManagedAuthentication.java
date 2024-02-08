package io.camunda.common.auth;

import io.camunda.common.auth.identity.IdentityConfig;
import io.camunda.identity.sdk.Identity;
import io.camunda.identity.sdk.authentication.Tokens;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelfManagedAuthentication extends JwtAuthentication {

  private final IdentityConfig identityConfig;

  public SelfManagedAuthentication(JwtConfig jwtConfig, IdentityConfig identityConfig) {
    super(jwtConfig);
    this.identityConfig = identityConfig;
  }

  public static SelfManagedAuthenticationBuilder builder() {
    return new SelfManagedAuthenticationBuilder();
  }

  @Override
  protected JwtToken generateToken(Product product, JwtCredential credential) {
    Tokens token = getIdentityToken(product, credential);
    return new JwtToken(
        token.getAccessToken(), LocalDateTime.now().plusSeconds(token.getExpiresIn()));
  }

  private Tokens getIdentityToken(Product product, JwtCredential credential) {
    Identity identity = identityConfig.get(product).getIdentity();
    String audience = credential.getAudience();
    return identity.authentication().requestToken(audience);
  }
}
