package io.camunda.zeebe.spring.client.properties.common;


import java.util.List;

public class ApiProperties extends AuthProperties {
  private Boolean enabled;
  private String baseUrl;
  private String audience;


  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }



  public String getAudience() {
    return audience;
  }

  public void setAudience(String audience) {
    this.audience = audience;
  }

  public static ApiProperties disabled(){
    ApiProperties apiProperties = new ApiProperties();
    apiProperties.setEnabled(false);
    return apiProperties;
  }

  public static ApiProperties enabled(){
    ApiProperties apiProperties = new ApiProperties();
    apiProperties.setEnabled(true);
    return apiProperties;
  }
}
