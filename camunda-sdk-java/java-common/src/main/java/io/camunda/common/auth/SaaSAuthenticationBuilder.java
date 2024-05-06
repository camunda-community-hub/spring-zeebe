package io.camunda.common.auth;

import io.camunda.common.auth.Authentication.AuthenticationBuilder;
import io.camunda.common.json.JsonMapper;

public class SaaSAuthenticationBuilder implements AuthenticationBuilder {
  private JsonMapper jsonMapper;
  private JwtConfig jwtConfig;

  public SaaSAuthenticationBuilder withJwtConfig(JwtConfig jwtConfig) {
    this.jwtConfig = jwtConfig;
    return this;
  }

  public SaaSAuthenticationBuilder withJsonMapper(JsonMapper jsonMapper) {
    this.jsonMapper = jsonMapper;
    return this;
  }

  @Override
  public Authentication build() {
    return new SaaSAuthentication(jwtConfig, jsonMapper);
  }
}
