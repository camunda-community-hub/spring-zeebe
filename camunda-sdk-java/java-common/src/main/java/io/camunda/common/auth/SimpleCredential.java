package io.camunda.common.auth;

/**
 * Contains credential for particular product. Used for Simple authentication.
 */
public class SimpleCredential {

  public SimpleCredential(String user, String password, String authUrl) {
    this.user = user;
    this.password = password;
    this.authUrl = authUrl;
  }

  private String user;
  private String password;
  private String authUrl;

  public String getUser() {
    return user;
  }

  public String getPassword() {
    return password;
  }

  public String getAuthUrl() {
    return authUrl;
  }

}
