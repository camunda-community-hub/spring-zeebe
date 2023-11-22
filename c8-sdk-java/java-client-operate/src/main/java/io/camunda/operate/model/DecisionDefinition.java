package io.camunda.operate.model;

public class DecisionDefinition {

  private String id;
  private Long key;
  private String decisionId;
  private String name;
  private Long version;
  private String decisionRequirementsId;
  private Long decisionRequirementsKey;
  private String decisionRequirementsName;
  private Long decisionRequirementsVersion;
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

  public String getDecisionId() {
    return decisionId;
  }

  public void setDecisionId(String decisionId) {
    this.decisionId = decisionId;
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

  public String getDecisionRequirementsId() {
    return decisionRequirementsId;
  }

  public void setDecisionRequirementsId(String decisionRequirementsId) {
    this.decisionRequirementsId = decisionRequirementsId;
  }

  public Long getDecisionRequirementsKey() {
    return decisionRequirementsKey;
  }

  public void setDecisionRequirementsKey(Long decisionRequirementsKey) {
    this.decisionRequirementsKey = decisionRequirementsKey;
  }

  public String getDecisionRequirementsName() {
    return decisionRequirementsName;
  }

  public void setDecisionRequirementsName(String decisionRequirementsName) {
    this.decisionRequirementsName = decisionRequirementsName;
  }

  public Long getDecisionRequirementsVersion() {
    return decisionRequirementsVersion;
  }

  public void setDecisionRequirementsVersion(Long decisionRequirementsVersion) {
    this.decisionRequirementsVersion = decisionRequirementsVersion;
  }

  public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }
}
