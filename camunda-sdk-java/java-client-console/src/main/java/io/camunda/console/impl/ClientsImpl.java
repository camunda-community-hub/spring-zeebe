package io.camunda.console.impl;

import io.camunda.common.exception.SdkException;
import io.camunda.console.CamundaConsoleClient.Cluster.Clients;
import io.camunda.console.client.invoker.ApiException;
import io.camunda.console.client.model.ClusterClient;
import io.camunda.console.client.model.CreateClusterClientBody;
import io.camunda.console.client.model.CreatedClusterClient;
import java.util.List;

public class ClientsImpl extends AbstractCluster implements Clients {

  public ClientsImpl(AbstractCluster cluster) {
    super(cluster);
  }

  @Override
  public List<ClusterClient> get() {
    try {
      return getApi().getClients(getClusterId());
    } catch (ApiException e) {
      throw new SdkException(e);
    }
  }

  @Override
  public CreatedClusterClient post(CreateClusterClientBody request) {
    try {
      return getApi().createClient(getClusterId(), request);
    } catch (ApiException e) {
      throw new SdkException(e);
    }
  }
}
