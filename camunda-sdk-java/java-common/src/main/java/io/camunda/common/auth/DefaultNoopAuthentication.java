package io.camunda.common.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Map;

/**
 * Default implementation for Authentication
 * Typically you will replace this by a proper authentication by setting the right properties
 */
public class DefaultNoopAuthentication implements Authentication {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Override
  public Authentication build() {
    LOG.error("Unable to determine authentication. Please check your configuration");
    return this;
  }

  @Override
  public Map.Entry<String, String> getTokenHeader(Product product) {
    throw new UnsupportedOperationException("Unable to determine authentication");
  }
}
