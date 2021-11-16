package io.camunda.zeebe.spring.client.exception;

public class ZeebeBpmnError extends RuntimeException {

  private String errorCode;
  private String errorMessage;

  public ZeebeBpmnError(String errorCode, String errorMessage) {
    super("[" + errorCode + "] " + errorMessage);
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public String getErrorCode() {
    return errorCode;
  }
}
