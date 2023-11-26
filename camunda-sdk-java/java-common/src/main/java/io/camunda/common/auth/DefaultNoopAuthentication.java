package io.camunda.common.auth;

import java.util.Map;

/**
 * Default implementation for Authentication
 * Typically you will replace this by a proper authentication by setting the right properties
 */
public class DefaultNoopAuthentication implements Authentication {
  @Override
  public Authentication build() {
    throw new UnsupportedOperationException("Unable to determine authentication");
  }

  @Override
  public Map.Entry<String, String> getTokenHeader(Product product) {
    throw new UnsupportedOperationException("Unable to determine authentication");
  }
}
