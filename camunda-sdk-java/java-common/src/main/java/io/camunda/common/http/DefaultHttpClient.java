package io.camunda.common.http;

import com.google.common.reflect.TypeToken;
import io.camunda.common.auth.Authentication;
import io.camunda.common.auth.Product;
import io.camunda.common.json.JsonMapper;
import io.camunda.common.json.SdkObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.io.entity.StringEntity;
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

  private JsonMapper jsonMapper = new SdkObjectMapper();

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
  public <T> T get(Class<T> responseType, Long key) {
    return get(responseType, String.valueOf(key));
  }

  @Override
  public <T> T get(Class<T> responseType, String id) {
    String url = host + basePath + retrievePath(responseType) + "/" + id;
    HttpGet httpGet = new HttpGet(url);
    httpGet.addHeader(retrieveToken(responseType));
    T resp = null;
    try {
      CloseableHttpResponse response = httpClient.execute(httpGet);
      String tmp = new String(Java8Utils.readAllBytes(response.getEntity().getContent()), StandardCharsets.UTF_8);
      resp = jsonMapper.fromJson(tmp, responseType);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return resp;
  }

  @Override
  public <T, V, W> T get(Class<T> responseType, Class<V> parameterType, TypeToken<W> selector, Long key) {
    return get(responseType, parameterType, selector, String.valueOf(key));
  }

  private  <T, V, W> T get(Class<T> responseType, Class<V> parameterType, TypeToken<W> selector, String id) {
    String resourcePath = retrievePath(selector.getClass());
    if (resourcePath.contains("{key}")) {
      resourcePath = resourcePath.replace("{key}", String.valueOf(id));
    } else {
      resourcePath = resourcePath + "/" + id;
    }
    String url = host + basePath + resourcePath;
    HttpGet httpGet = new HttpGet(url);
    httpGet.addHeader(retrieveToken(selector.getClass()));
    T resp = null;
    try {
      CloseableHttpResponse response = httpClient.execute(httpGet);
      String tmp = new String(Java8Utils.readAllBytes(response.getEntity().getContent()), StandardCharsets.UTF_8);
      resp = jsonMapper.fromJson(tmp, responseType, parameterType);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return resp;
  }


    @Override
  public <T> String getXml(Class<T> selector, Long key) {
    String url = host + basePath + retrievePath(selector) + "/" + key + "/xml";
    HttpGet httpGet = new HttpGet(url);
    httpGet.addHeader(retrieveToken(selector));
    String xml = null;
    try {
      CloseableHttpResponse response = httpClient.execute(httpGet);
      xml = new String(Java8Utils.readAllBytes(response.getEntity().getContent()), StandardCharsets.UTF_8);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return xml;
  }

  @Override
  public <T, V, W, U> T post(Class<T> responseType, Class<V> parameterType, TypeToken<W> selector, U body) {
    String url = host + basePath + retrievePath(selector.getClass());
    HttpPost httpPost = new HttpPost(url);
    httpPost.addHeader("Content-Type", "application/json");
    httpPost.addHeader(retrieveToken(selector.getClass()));
    T resp = null;
    try {
      String data = jsonMapper.toJson(body);
      httpPost.setEntity(new StringEntity(data));
      CloseableHttpResponse response = httpClient.execute(httpPost);
      String tmp = new String(Java8Utils.readAllBytes(response.getEntity().getContent()), StandardCharsets.UTF_8);
      resp = jsonMapper.fromJson(tmp, responseType, parameterType);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return resp;
  }

  @Override
  public <T, V> V delete(Class<T> selector, Class<V> responseType, Long key) {
    String resourcePath = retrievePath(selector) + "/" + key;
    String url = host + basePath + resourcePath;
    HttpDelete httpDelete = new HttpDelete(url);
    httpDelete.addHeader(retrieveToken(selector));
    V resp = null;
    try {
      CloseableHttpResponse response = httpClient.execute(httpDelete);
      String tmp = new String(Java8Utils.readAllBytes(response.getEntity().getContent()), StandardCharsets.UTF_8);
      resp = jsonMapper.fromJson(tmp, responseType);
      boolean f = false;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return resp;
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
