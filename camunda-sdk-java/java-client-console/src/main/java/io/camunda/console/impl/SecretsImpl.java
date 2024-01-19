package io.camunda.console.impl;

import io.camunda.common.exception.SdkException;
import io.camunda.console.CamundaConsoleClient.Cluster.Secrets;
import io.camunda.console.client.invoker.ApiException;
import io.camunda.console.client.model.CreateSecretBody;
import java.util.Map;

public class SecretsImpl extends AbstractCluster implements Secrets {
  public SecretsImpl(AbstractCluster cluster) {
    super(cluster);
  }

  @Override
  public Map<String, String> get() {
    try {
      return getApi().getSecrets(getClusterId());
    } catch (ApiException e) {
      throw new SdkException(e);
    }
  }

  @Override
  public void post(CreateSecretBody request) {
    try {
      getApi().createSecret(getClusterId(), request);
    } catch (ApiException e) {
      throw new SdkException(e);
    }
  }
}
