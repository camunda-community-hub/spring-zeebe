package io.camunda.common.auth;

import io.camunda.common.json.JsonMapper;

public class SelfManagedAuthenticationBuilder extends JwtAuthenticationBuilder<SelfManagedAuthenticationBuilder> {
  private String keycloakUrl;
  private String keycloakRealm;
  private String keycloakTokenUrl;
  private JsonMapper jsonMapper;


  public SelfManagedAuthenticationBuilder withKeycloakUrl(String keycloakUrl) {
    this.keycloakUrl = keycloakUrl;
    return this;
  }

  public SelfManagedAuthenticationBuilder withKeycloakRealm(String keycloakRealm) {
    this.keycloakRealm = keycloakRealm;
    return this;
  }

  public SelfManagedAuthenticationBuilder withKeycloakTokenUrl(String keycloakTokenUrl) {
    this.keycloakTokenUrl = keycloakTokenUrl;
    return this;
  }

  public SelfManagedAuthenticationBuilder withJsonMapper(JsonMapper jsonMapper){
    this.jsonMapper = jsonMapper;
    return this;
  }


  @Override
  protected SelfManagedAuthenticationBuilder self() {
    return this;
  }

  @Override
  protected Authentication build(JwtConfig jwtConfig) {
    String authUrl = keycloakTokenUrl != null ? keycloakTokenUrl : keycloakUrl+"/auth/realms/"+keycloakRealm+"/protocol/openid-connect/token";
    return new SelfManagedAuthentication(jwtConfig,authUrl,jsonMapper);
  }
}
