package io.camunda.common.auth;

/**
 * Contains credential for particular product. Used for JWT authentication.
 */
public class JwtCredential {

  public JwtCredential(String clientId, String clientSecret, String audience, String authUrl) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.audience = audience;
    this.authUrl = authUrl;
  }

  String clientId;
  String clientSecret;
  String audience;
  String authUrl;

}
