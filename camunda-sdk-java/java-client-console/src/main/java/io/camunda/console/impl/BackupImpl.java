package io.camunda.console.impl;

import io.camunda.common.exception.SdkException;
import io.camunda.console.CamundaConsoleClient.Cluster.Backup;
import io.camunda.console.client.invoker.ApiException;
import io.camunda.console.client.model.BackupDto;

public class BackupImpl extends AbstractCluster implements Backup {
  private final String backupId;

  public BackupImpl(AbstractCluster abstractCluster, String backupId) {
    super(abstractCluster);
    this.backupId = backupId;
  }

  @Override
  public BackupDto delete() {
    try {
      return getApi().deleteBackup(getClusterId(), backupId);
    } catch (ApiException e) {
      throw new SdkException(e);
    }
  }
}
