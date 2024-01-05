package io.camunda.common.auth;

import java.util.Map;

public interface Authentication {

  Authentication build();

  Map.Entry<String, String> getTokenHeader(Product product);

  void resetToken(Product product);
}
