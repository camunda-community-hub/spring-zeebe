package io.camunda.commons.http;

import io.camunda.commons.auth.Authentication;
import io.camunda.commons.auth.Product;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.message.BasicHeader;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Default Http Client powered by Apache HttpClient
 */
public class DefaultHttpClient implements HttpClient {

  private String host = "";
  private String basePath = "";
  private Map<Product, Map<Class<?>, String>> productMap = new HashMap<>();
  private CloseableHttpClient httpClient = HttpClients.createDefault();
  private Authentication authentication;

  public DefaultHttpClient(Authentication authentication) {
    this.authentication = authentication;
  }

  @Override
  public void init(String host, String basePath) {
    this.host = host;
    this.basePath = basePath;
  }

  @Override
  public void loadMap(Product product, Map<Class<?>, String> map) {
    this.productMap.put(product, map);
  }

  @Override
  public <T> T get(Class<T> clazz, Long key) {
    return get(clazz, String.valueOf(key));
  }

  @Override
  public <T> T get(Class<T> clazz, String id) {
    String url = host + basePath + retrievePath(clazz) + "/" + id;
    HttpGet httpGet = new HttpGet(url);
    httpGet.addHeader("Content-Type", "application/json");
    httpGet.addHeader(retrieveToken(clazz));
    T resp = null;
    try {
      CloseableHttpResponse response = httpClient.execute(httpGet);
      String tmp = new String(Java8Utils.readAllBytes(response.getEntity().getContent()), StandardCharsets.UTF_8);
      resp = JsonUtils.toResult(tmp, clazz);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return resp;
  }

  @Override
  public void get(String resource) {

  }

  @Override
  public <T> T post(Class<T> clazz) {
    return null;
  }

  private <T> String retrievePath(Class<T> clazz) {
    AtomicReference<String> path = new AtomicReference<>();
    productMap.forEach((product, map) -> {
      if (map.containsKey(clazz)) {
        path.set(map.get(clazz));
      }
    });
    return path.get();
  }

  private <T> Header retrieveToken(Class<T> clazz) {
    AtomicReference<Product> currentProduct = new AtomicReference<>();
    productMap.forEach((product, map) -> {
      if (map.containsKey(clazz)) {
        currentProduct.set(product);
      }
    });
    Map.Entry<String, String> header = authentication.getTokenHeader(currentProduct.get());
    return new BasicHeader(header.getKey(), header.getValue());
  }
}
