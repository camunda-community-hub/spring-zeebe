package io.camunda.commons.http;

import io.camunda.commons.auth.Product;

import java.util.Map;

/**
 * Interface to enable swappable http client implementations
 */
public interface HttpClient {

  void init(String host, String basePath);

  void loadMap(Product product, Map<Class<?>, String> map);

  <T> T get(Class<T> clazz, Long key);

  <T> T get(Class<T> clazz, String id);

  void get(String resource);

  <T> T post(Class<T> clazz);
}
