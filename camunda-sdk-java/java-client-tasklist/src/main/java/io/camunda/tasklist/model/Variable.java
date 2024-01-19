package io.camunda.tasklist.model;

public class Variable {

  private String id;
  private String name;
  private String value;
  private DraftVariable draft;
  private String tenantId;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public DraftVariable getDraft() {
    return draft;
  }

  public void setDraft(DraftVariable draft) {
    this.draft = draft;
  }

  public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }
}
