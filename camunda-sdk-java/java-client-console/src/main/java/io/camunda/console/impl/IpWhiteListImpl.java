package io.camunda.console.impl;

import io.camunda.common.exception.SdkException;
import io.camunda.console.CamundaConsoleClient.Cluster.IpWhiteList;
import io.camunda.console.client.invoker.ApiException;
import io.camunda.console.client.model.IpWhiteListBody;

public class IpWhiteListImpl extends AbstractCluster implements IpWhiteList {

  public IpWhiteListImpl(AbstractCluster cluster) {
    super(cluster);
  }

  @Override
  public void put(IpWhiteListBody request) {
    try {
      getApi().updateIpWhitelist(getClusterId(), request);
    } catch (ApiException e) {
      throw new SdkException(e);
    }
  }
}
