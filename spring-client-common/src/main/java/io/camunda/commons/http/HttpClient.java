package io.camunda.commons.http;

import com.google.common.reflect.TypeToken;
import io.camunda.commons.auth.Product;

import java.util.Collection;
import java.util.Map;

/**
 * Interface to enable swappable http client implementations
 */
public interface HttpClient {

  void init(String host, String basePath);

  void loadMap(Product product, Map<Class<?>, String> map);

  <T> T get(Class<T> clazz, Long key);

  <T> T get(Class<T> clazz, String id);

  <T, V, W> T get(Class<T> clazz, Class<V> vj, TypeToken<W> typeToken, Long key);

  <T> String getXml(Class<T> clazz, Long key);

  <T, V, W, U> T post(Class<T> clazz, Class<V> vj, TypeToken<W> typeToken, U body);

  <T, V> V delete(Class<T> clazz, Class<V> responseClass, Long key);

}
