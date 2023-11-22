package io.camunda.zeebe.spring.client.properties.common;

public class Keycloak {

  private String url;
  private String realm;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getRealm() {
    return realm;
  }

  public void setRealm(String realm) {
    this.realm = realm;
  }
}
