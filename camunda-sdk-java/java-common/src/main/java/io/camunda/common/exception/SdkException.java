package io.camunda.common.exception;

import java.io.Serial;

public class SdkException extends RuntimeException {

  @Serial private static final long serialVersionUID = 1L;

  public SdkException(final Throwable cause) {
    super(cause);
  }

  public SdkException(final String message) {
    super(message);
  }

  public SdkException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
