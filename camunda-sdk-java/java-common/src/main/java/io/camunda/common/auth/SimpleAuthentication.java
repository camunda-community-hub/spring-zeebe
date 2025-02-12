package io.camunda.common.auth;

import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleAuthentication implements Authentication {
  private static final Set<String> CSRF_HEADER_CANDIDATES =
      Set.of("X-CSRF-TOKEN", "OPERATE-X-CSRF-TOKEN", "TASKLIST-X-CSRF-TOKEN");
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final SimpleConfig simpleConfig;
  private final Map<Product, SimpleAuthToken> tokens = new HashMap<>();

  public SimpleAuthentication(SimpleConfig simpleConfig) {
    this.simpleConfig = simpleConfig;
  }

  public static SimpleAuthenticationBuilder builder() {
    return new SimpleAuthenticationBuilder();
  }

  public SimpleConfig getSimpleConfig() {
    return simpleConfig;
  }

  private SimpleAuthToken retrieveToken(Product product, SimpleCredential simpleCredential) {
    try (CloseableHttpClient client = HttpClients.createSystem()) {
      HttpPost request = buildRequest(simpleCredential);

      SimpleAuthToken simpleAuthToken =
          client.execute(
              request,
              response -> {
                if (response.getCode() > 299) {
                  throw new RuntimeException(
                      "Unable to login, response code " + response.getCode());
                }
                String csrfTokenCandidate = null;
                String csrfTokenHeaderName = null;
                Header csrfTokenHeader = findCsrfTokenHeader(product, response);
                if (csrfTokenHeader != null) {
                  csrfTokenCandidate = csrfTokenHeader.getValue();
                  csrfTokenHeaderName = csrfTokenHeader.getName();
                }
                Header[] cookieHeaders = response.getHeaders("Set-Cookie");
                String sessionCookie = null;
                String csrfCookie = null;
                String sessionCookieName = product.name().toUpperCase() + "-SESSION";
                for (Header cookieHeader : cookieHeaders) {
                  if (cookieHeader.getValue().startsWith(sessionCookieName)) {
                    sessionCookie = cookieHeader.getValue();
                  }
                  for (String candidate : CSRF_HEADER_CANDIDATES) {
                    if (cookieHeader.getValue().startsWith(candidate)) {
                      csrfCookie = cookieHeader.getValue();
                    }
                  }
                }
                return new SimpleAuthToken(
                    sessionCookie,
                    csrfCookie,
                    csrfTokenCandidate,
                    csrfTokenHeaderName,
                    LocalDateTime.now().plus(Duration.ofMinutes(5)));
              });
      tokens.put(product, simpleAuthToken);
    } catch (Exception e) {
      throw new RuntimeException("Unable to authenticate to " + product, e);
    }
    return tokens.get(product);
  }

  private Header findCsrfTokenHeader(Product product, ClassicHttpResponse response)
      throws ProtocolException {
    SimpleAuthToken token = tokens.get(product);
    if (token != null) {
      return response.getHeader(token.csrfTokenHeaderName());
    }
    for (String candidate : CSRF_HEADER_CANDIDATES) {
      if (response.containsHeader(candidate)) {
        return response.getHeader(candidate);
      }
    }
    return null;
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
    SimpleAuthToken token = tokens.get(product);
    SimpleCredential simpleCredential = simpleConfig.getProduct(product);
    if (token == null || token.sessionTimeout().isBefore(LocalDateTime.now())) {
      token = retrieveToken(product, simpleCredential);
    }
    Map<String, String> headers = new HashMap<>();
    if (token.csrfToken() != null) {
      headers.put(token.csrfTokenHeaderName(), token.csrfToken());
    }
    headers.put(
        "Cookie",
        Stream.of(token.sessionCookie(), token.csrfCookie())
            .filter(Objects::nonNull)
            .collect(Collectors.joining("; ")));
    return headers;
  }

  @Override
  public void resetToken(Product product) {
    tokens.remove(product);
  }

  private record SimpleAuthToken(
      String sessionCookie,
      String csrfCookie,
      String csrfToken,
      String csrfTokenHeaderName,
      LocalDateTime sessionTimeout) {}
}
