package io.camunda.console.impl;

import io.camunda.common.exception.SdkException;
import io.camunda.console.CamundaConsoleClient.Cluster.Backups;
import io.camunda.console.client.invoker.ApiException;
import io.camunda.console.client.model.BackupDto;
import java.util.List;

public class BackupsImpl extends AbstractCluster implements Backups {

  public BackupsImpl(AbstractCluster cluster) {
    super(cluster);
  }

  @Override
  public List<BackupDto> get() {
    try {
      return getApi().getBackups(getClusterId());
    } catch (ApiException e) {
      throw new SdkException(e);
    }
  }

  @Override
  public BackupDto post() {
    try {
      return getApi().createBackup(getClusterId());
    } catch (ApiException e) {
      throw new SdkException(e);
    }
  }
}
