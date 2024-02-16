package io.camunda.console.impl;

import io.camunda.common.exception.SdkException;
import io.camunda.console.CamundaConsoleClient.Cluster.Wake;
import io.camunda.console.client.invoker.ApiException;

public class WakeImpl extends AbstractCluster implements Wake {
  public WakeImpl(AbstractCluster cluster) {
    super(cluster);
  }

  @Override
  public void put() {
    try {
      getApi().wake(getClusterId());
    } catch (ApiException e) {
      throw new SdkException(e);
    }
  }
}
