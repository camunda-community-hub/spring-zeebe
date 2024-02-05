package io.camunda.operate.model;

public class DecisionRequirements {

  private String id;
  private Long key;
  private String decisionRequirements;
  private String name;
  private Long version;
  private String resourceName;
  private String tenantId;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Long getKey() {
    return key;
  }

  public void setKey(Long key) {
    this.key = key;
  }

  public String getDecisionRequirements() {
    return decisionRequirements;
  }

  public void setDecisionRequirements(String decisionRequirements) {
    this.decisionRequirements = decisionRequirements;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  public String getResourceName() {
    return resourceName;
  }

  public void setResourceName(String resourceName) {
    this.resourceName = resourceName;
  }

  public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }
}
