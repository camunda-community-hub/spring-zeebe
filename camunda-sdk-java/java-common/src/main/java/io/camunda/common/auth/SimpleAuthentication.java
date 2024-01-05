package io.camunda.common.auth;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.*;

public class SimpleAuthentication implements Authentication {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private String simpleUrl;
  private SimpleConfig simpleConfig;
  private Map<Product, String> tokens;

  private String authUrl;

  public void setSimpleUrl(String simpleUrl) {
    this.simpleUrl = simpleUrl;
  }

  public SimpleConfig getSimpleConfig() {
    return simpleConfig;
  }

  public void setSimpleConfig(SimpleConfig simpleConfig) {
    this.simpleConfig = simpleConfig;
  }

  public SimpleAuthentication() {
    tokens = new HashMap<>();
  }

  public static SimpleAuthenticationBuilder builder() { return new SimpleAuthenticationBuilder(); }

  @Override
  public Authentication build() {
    authUrl = simpleUrl+"/api/login";
    return this;
  }

  private String retrieveToken(Product product, SimpleCredential simpleCredential) {
    try {
      HttpPost httpPost = new HttpPost(authUrl);
      List<NameValuePair> params = new ArrayList<>();
      params.add(new BasicNameValuePair("username", simpleCredential.getUser()));
      params.add(new BasicNameValuePair("password", simpleCredential.getPassword()));
      httpPost.setEntity(new UrlEncodedFormEntity(params));

      CloseableHttpClient client = HttpClient.getInstance();
      CloseableHttpResponse response = client.execute(httpPost);
      Header[] cookieHeaders = response.getHeaders("Set-Cookie");
      String cookie = null;
      for (Header cookieHeader : cookieHeaders) {
        if (cookieHeader.getValue().startsWith("OPERATE-SESSION")) {
          cookie = response.getHeader("Set-Cookie").getValue();
        }
      }
      if (cookie == null) {
        throw new RuntimeException("Unable to authenticate due to missing Set-Cookie");
      }
      tokens.put(product, cookie);
    } catch (Exception e) {
      LOG.error("Authenticating for " + product + " failed due to " + e);
      throw new RuntimeException("Unable to authenticate", e);
    }
    return tokens.get(product);
  }


    @Override
  public Map.Entry<String, String> getTokenHeader(Product product) {
    String token;
    if (tokens.containsKey(product)) {
      token = tokens.get(product);
    } else {
      SimpleCredential simpleCredential = simpleConfig.getProduct(product);
      token = retrieveToken(product, simpleCredential);
    }

    return new AbstractMap.SimpleEntry<>("Cookie", token);
  }

  @Override
  public void resetToken(Product product) {
    tokens.remove(product);
  }
}
