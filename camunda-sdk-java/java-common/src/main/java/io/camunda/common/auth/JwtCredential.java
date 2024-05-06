package io.camunda.common.auth;

/** Contains credential for particular product. Used for JWT authentication. */
public class JwtCredential {

  public JwtCredential(String clientId, String clientSecret, String audience, String authUrl) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.audience = audience;
    this.authUrl = authUrl;
  }

  private final String clientId;
  private final String clientSecret;
  private final String audience;
  private final String authUrl;

  public String getClientId() {
    return clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public String getAudience() {
    return audience;
  }

  public String getAuthUrl() {
    return authUrl;
  }
}
