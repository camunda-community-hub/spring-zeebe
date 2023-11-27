package io.camunda.common.auth;

/**
 * Contains credential for particular product. Used for JWT authentication.
 */
public class JwtCredential {

  public JwtCredential(String clientId, String clientSecret) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
  }
  String clientId;
  String clientSecret;

}
