package io.camunda.console.impl;


import io.camunda.console.CamundaConsoleClient;
import io.camunda.console.client.api.DefaultApi;

public class CamundaConsoleClientImpl extends AbstractCamundaConsoleClient
  implements CamundaConsoleClient {

  public CamundaConsoleClientImpl(DefaultApi api) {
    super(api);
  }

  @Override
  public Clusters clusters() {
    return new ClustersImpl(this);
  }

  @Override
  public Cluster clusters(String id) {
    return new ClusterImpl(this, id);
  }

  @Override
  public Members members() {
    return new MembersImpl(this);
  }

  @Override
  public Member members(String email) {
    return new MemberImpl(this, email);
  }


}
