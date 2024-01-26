package io.camunda.common.auth;

public class SimpleAuthenticationBuilder {

  SimpleAuthentication simpleAuthentication;

  SimpleAuthenticationBuilder() {
    simpleAuthentication = new SimpleAuthentication();
  }

  public SimpleAuthenticationBuilder simpleConfig(SimpleConfig simpleConfig) {
    simpleAuthentication.setSimpleConfig(simpleConfig);
    return this;
  }

  public SimpleAuthenticationBuilder simpleUrl(String simpleUrl) {
    simpleAuthentication.setSimpleUrl(simpleUrl);
    return this;
  }

  public Authentication build() {
    return simpleAuthentication.build();
  }
}
