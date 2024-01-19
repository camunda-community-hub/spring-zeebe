package io.camunda.tasklist;

import io.camunda.common.auth.Authentication;
import io.camunda.common.auth.Product;
import io.camunda.common.http.DefaultHttpClient;
import io.camunda.common.http.HttpClient;
import io.camunda.tasklist.model.Form;
import io.camunda.tasklist.model.SearchVariables;

import java.util.HashMap;
import java.util.Map;

public class CamundaTaskListClientBuilder {

  private CamundaTaskListClient client;
  private Authentication authentication;
  private String taskListUrl;
  private HttpClient httpClient;

  public CamundaTaskListClientBuilder authentication(Authentication authentication) {
    this.authentication = authentication;
    return this;
  }

  public CamundaTaskListClientBuilder taskListUrl(String taskListUrl) {
    this.taskListUrl = formatUrl(taskListUrl);
    return this;
  }

  public CamundaTaskListClientBuilder setup() {
    httpClient = new DefaultHttpClient(authentication);
    httpClient.init(taskListUrl, "/v1");
    // load the config map
    Map<Class<?>, String> map = new HashMap<>();
    map.put(Form.class, "/forms");
    map.put(Void.class, "/tasks/{key}/variables");
    map.put(SearchVariables.class, "/tasks/{key}/variables/search");

    httpClient.loadMap(Product.TASKLIST, map);
    return this;
  }

  private String formatUrl(String url) {
    if (url.endsWith("/")) {
      return url.substring(0, url.length()-1);
    }
    return url;
  }

  public CamundaTaskListClient build() {
    client = new CamundaTaskListClient();
    client.setHttpClient(httpClient);
    return client;
  }
}
