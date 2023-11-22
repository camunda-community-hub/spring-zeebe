package io.camunda.common.auth;

/**
 * Contains credential for particular product. Used for authentication.
 */
public class Credential {

  public Credential(String clientId, String clientSecret) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
  }
  String clientId;
  String clientSecret;

}
