package io.camunda.common.auth;

import java.lang.invoke.MethodHandles;
import java.util.*;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleAuthentication implements Authentication {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final SimpleConfig simpleConfig;
  private final Map<Product, Map<String, String>> tokens = new HashMap<>();

  public SimpleAuthentication(SimpleConfig simpleConfig) {
    this.simpleConfig = simpleConfig;
  }

  public static SimpleAuthenticationBuilder builder() {
    return new SimpleAuthenticationBuilder();
  }

  public SimpleConfig getSimpleConfig() {
    return simpleConfig;
  }

  private Map<String, String> retrieveToken(Product product, SimpleCredential simpleCredential) {
    try (CloseableHttpClient client = HttpClients.createSystem()) {
      HttpPost request = buildRequest(simpleCredential);

      Map<String, String> headers =
          client.execute(request, response -> Arrays.asList(response.getHeaders())).stream()
              .reduce(
                  new HashMap<>(),
                  (map, header) -> insertHeader(map, header, product),
                  (pMap, cMap) -> {
                    pMap.putAll(cMap);
                    return pMap;
                  });

      if (headers.get("Cookie") == null) {
        throw new RuntimeException("Unable to authenticate due to missing Set-Cookie");
      }
      tokens.put(product, headers);
    } catch (Exception e) {
      LOG.error("Authenticating for " + product + " failed due to " + e);
      throw new RuntimeException("Unable to authenticate", e);
    }
    return tokens.get(product);
  }

  private HashMap<String, String> insertHeader(
      HashMap<String, String> map, Header header, Product product) {
    if (header.getValue().startsWith(product.toString().toUpperCase())) {
      map.merge("Cookie", header.getValue(), (s, s2) -> s.concat("; ".concat(s2)));
    } else if (header.getName().equals(product.toString().toUpperCase() + "-X-CSRF-TOKEN")) {
      map.put(header.getName(), header.getValue());
    }
    return map;
  }

  private HttpPost buildRequest(SimpleCredential simpleCredential) {
    HttpPost httpPost = new HttpPost(simpleCredential.getBaseUrl() + "/api/login");
    List<NameValuePair> params = new ArrayList<>();
    params.add(new BasicNameValuePair("username", simpleCredential.getUser()));
    params.add(new BasicNameValuePair("password", simpleCredential.getPassword()));
    httpPost.setEntity(new UrlEncodedFormEntity(params));
    return httpPost;
  }

  @Override
  public Map<String, String> getTokenHeader(Product product) {
    if (tokens.containsKey(product)) {
      return tokens.get(product);
    } else {
      SimpleCredential simpleCredential = simpleConfig.getProduct(product);
      return retrieveToken(product, simpleCredential);
    }
  }

  @Override
  public void resetToken(Product product) {
    tokens.remove(product);
  }
}
