package io.camunda.common.auth;

import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.protocol.HttpContext;

import java.io.IOException;
import java.util.Map.Entry;

public class AuthenticationInterceptor implements HttpRequestInterceptor {
  private final Product product;
  private final Authentication authentication;

  public AuthenticationInterceptor(Product product, Authentication authentication) {
    this.product = product;
    this.authentication = authentication;
  }

  @Override
  public void process(HttpRequest httpRequest, EntityDetails entityDetails, HttpContext httpContext) throws HttpException, IOException {
    Entry<String, String> tokenHeader = authentication.getTokenHeader(product);
    httpRequest.setHeader(tokenHeader.getKey(),tokenHeader.getValue());
  }
}
