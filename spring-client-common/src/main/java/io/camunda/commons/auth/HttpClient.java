package io.camunda.commons.auth;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;

public class HttpClient {

  private static CloseableHttpClient httpClient;

  private HttpClient() { }

  // TODO: do we need to synchronized here for thread safety?
  public static CloseableHttpClient getInstance() {
    if (httpClient == null) {
      httpClient = HttpClients.createDefault();
    }
    return httpClient;
  }

}
