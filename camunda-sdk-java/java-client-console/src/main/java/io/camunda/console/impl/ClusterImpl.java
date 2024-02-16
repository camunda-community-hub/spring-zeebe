package io.camunda.console.impl;

import io.camunda.common.exception.SdkException;
import io.camunda.console.CamundaConsoleClient.Cluster;
import io.camunda.console.client.invoker.ApiException;
import io.camunda.console.client.model.UpdateClusterRequest;

public class ClusterImpl extends AbstractCluster implements Cluster {

  public ClusterImpl(AbstractCamundaConsoleClient client, String clusterId) {
    super(client, clusterId);
  }

  @Override
  public io.camunda.console.client.model.Cluster get() {
    try {
      return getApi().getCluster(getClusterId());
    } catch (ApiException e) {
      throw new SdkException(e);
    }
  }

  @Override
  public void delete() {
    try {
      getApi().deleteCluster(getClusterId());
    } catch (ApiException e) {
      throw new SdkException(e);
    }
  }

  @Override
  public void patch(UpdateClusterRequest request) {
    try {
      getApi().updateCluster(getClusterId(), request);
    } catch (ApiException e) {
      throw new SdkException(e);
    }
  }

  @Override
  public Backups backups() {
    return new BackupsImpl(this);
  }

  @Override
  public Backup backups(String backupId) {
    return new BackupImpl(this, backupId);
  }

  @Override
  public IpAllowList ipAllowList() {
    return new IpAllowListImpl(this);
  }

  @Override
  public Wake wake() {
    return new WakeImpl(this);
  }

  @Override
  public Clients clients() {
    return new ClientsImpl(this);
  }

  @Override
  public Client clients(String clientId) {
    return new ClientImpl(this, clientId);
  }

  @Override
  public Secrets secrets() {
    return new SecretsImpl(this);
  }

  @Override
  public Secret secrets(String secretName) {
    return new SecretImpl(this, secretName);
  }
}
