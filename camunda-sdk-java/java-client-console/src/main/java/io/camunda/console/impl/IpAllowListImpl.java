package io.camunda.console.impl;

import io.camunda.common.exception.SdkException;
import io.camunda.console.CamundaConsoleClient.Cluster.IpAllowList;
import io.camunda.console.client.invoker.ApiException;
import io.camunda.console.client.model.IpAllowListBody;

public class IpAllowListImpl extends AbstractCluster implements IpAllowList {
  public IpAllowListImpl(AbstractCluster cluster) {
    super(cluster);
  }

  @Override
  public void put(IpAllowListBody request) {
    try {
      getApi().updateIpAllowlist(getClusterId(), request);
    } catch (ApiException e) {
      throw new SdkException(e);
    }
  }
}
