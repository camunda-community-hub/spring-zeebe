package io.camunda.commons.auth;

import io.camunda.commons.http.HttpClient;

import java.util.Map;

public interface Authentication {

  Authentication build();

  Map.Entry<String, String> getTokenHeader(Product product);
}
