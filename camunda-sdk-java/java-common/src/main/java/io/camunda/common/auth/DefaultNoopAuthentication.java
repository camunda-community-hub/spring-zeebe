package io.camunda.common.auth;

import java.util.Map;

/**
 * Default implementation for Authentication Typically you will replace this by a proper
 * authentication by setting the right properties
 */
public class DefaultNoopAuthentication implements Authentication {

  @Override
  public void resetToken(Product product) {
    throw new UnsupportedOperationException("noop authentication does not support resetToken");
  }

  @Override
  public Map.Entry<String, String> getTokenHeader(Product product) {
    throw new UnsupportedOperationException("noop authentication does not support getTokenHeader");
  }
}
