package io.camunda.console.impl;

import io.camunda.common.exception.SdkException;
import io.camunda.console.CamundaConsoleClient.Members;
import io.camunda.console.client.invoker.ApiException;
import io.camunda.console.client.model.Member;
import java.util.List;

public class MembersImpl extends AbstractCamundaConsoleClient implements Members {
  public MembersImpl(AbstractCamundaConsoleClient consoleClient) {
    super(consoleClient);
  }

  @Override
  public List<Member> get() {
    try {
      return getApi().getMembers();
    } catch (ApiException e) {
      throw new SdkException(e);
    }
  }
}
