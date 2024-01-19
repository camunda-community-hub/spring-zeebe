package io.camunda.console.impl;

public abstract class AbstractCluster extends AbstractCamundaConsoleClient {
  private final String clusterId;

  public AbstractCluster(AbstractCamundaConsoleClient consoleClient, String clusterId) {
    super(consoleClient);
    this.clusterId = clusterId;
  }

  public AbstractCluster(AbstractCluster cluster) {
    super(cluster);
    this.clusterId = cluster.clusterId;
  }

  protected String getClusterId() {
    return clusterId;
  }
}
