package io.camunda.common.auth;

import java.util.Map;

public interface Authentication {

  Map<String, String> getTokenHeader(Product product);

  void resetToken(Product product);

  interface AuthenticationBuilder {
    Authentication build();
  }
}
