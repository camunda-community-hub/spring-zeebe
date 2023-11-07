package io.camunda.commons.auth;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains mapping between products and their credentials
 */
public class JwtConfig {

  private Map<Product, Credential> map;

  public JwtConfig() {
    map = new HashMap<>();
  }

  public void addProduct(Product product, Credential credential) {
    map.put(product, credential);
  }

  public Map<Product, Credential> getMap() {
    return map;
  }

}



