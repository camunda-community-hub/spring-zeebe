package io.camunda.console;

import io.camunda.common.auth.Authentication;
import io.camunda.common.auth.AuthenticationInterceptor;
import io.camunda.common.auth.Product;
import io.camunda.console.client.api.DefaultApi;
import io.camunda.console.client.invoker.ApiClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpRequestInterceptor;

public class DefaultApiFactory {
  private final Authentication authentication;
  private final String consoleUrl;
  private DefaultApi api;

  private DefaultApiFactory(Authentication authentication, String consoleUrl) {
    this.authentication = authentication;
    this.consoleUrl = consoleUrl;
  }

  public static DefaultApiFactory getInstance(Authentication authentication, String consoleUrl) {
    return new DefaultApiFactory(authentication, consoleUrl);
  }

  public DefaultApi get() {
    ensureApiClient();
    return api;
  }

  private void ensureApiClient() {
    if (api == null) {
      HttpRequestInterceptor interceptor =
          new AuthenticationInterceptor(Product.CONSOLE, authentication);
      CloseableHttpClient httpClient =
          HttpClients.custom().addRequestInterceptorFirst(interceptor).build();
      ApiClient apiClient = new ApiClient(httpClient);
      apiClient.setBasePath(consoleUrl);
      api = new DefaultApi(apiClient);
    }
  }
}
