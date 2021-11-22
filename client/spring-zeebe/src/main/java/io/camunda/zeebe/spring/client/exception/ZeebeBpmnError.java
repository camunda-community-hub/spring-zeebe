package io.camunda.zeebe.spring.client.exception;

/**
 * Indicates an error in sense of BPMN occured, that should be handled by the BPMN process,
 * see https://docs.camunda.io/docs/reference/bpmn-processes/error-events/error-events/
 */
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
