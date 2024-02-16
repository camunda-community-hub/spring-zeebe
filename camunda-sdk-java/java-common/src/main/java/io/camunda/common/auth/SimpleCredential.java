package io.camunda.common.auth;

/** Contains credential for particular product. Used for Simple authentication. */
public class SimpleCredential {

  public SimpleCredential(String baseUrl,String user, String password) {
    this.baseUrl = baseUrl;
    this.user = user;
    this.password = password;
  }
  private final String baseUrl;
  private final String user;
  private final String password;

  public String getUser() {
    return user;
  }

  public String getPassword() {
    return password;
  }

  public String getBaseUrl() {
    return baseUrl;
  }
}
