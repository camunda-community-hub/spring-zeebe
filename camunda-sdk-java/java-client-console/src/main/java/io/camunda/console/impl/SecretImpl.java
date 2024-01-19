package io.camunda.console.impl;

import io.camunda.common.exception.SdkException;
import io.camunda.console.CamundaConsoleClient.Cluster.Secret;
import io.camunda.console.client.invoker.ApiException;

public class SecretImpl extends AbstractCluster implements Secret {
  private final String secretName;

  public SecretImpl(AbstractCluster cluster, String secretName) {
    super(cluster);
    this.secretName = secretName;
  }

  @Override
  public void delete() {
    try {
      getApi().deleteSecret(getClusterId(), secretName);
    } catch (ApiException e) {
      throw new SdkException(e);
    }
  }
}
