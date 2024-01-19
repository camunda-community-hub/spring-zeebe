package io.camunda.console.impl;

import io.camunda.common.exception.SdkException;
import io.camunda.console.CamundaConsoleClient.Member;
import io.camunda.console.client.invoker.ApiException;
import io.camunda.console.client.model.PostMemberBody;

public class MemberImpl extends AbstractCamundaConsoleClient implements Member {
  private final String email;

  public MemberImpl(AbstractCamundaConsoleClient consoleClient, String email) {
    super(consoleClient);
    this.email = email;
  }

  @Override
  public void post(PostMemberBody request) {
    try {
      getApi().updateMembers(email, request);
    } catch (ApiException e) {
      throw new SdkException(e);
    }
  }

  @Override
  public void delete() {
    try {
      getApi().deleteMember(email);
    } catch (ApiException e) {
      throw new SdkException(e);
    }
  }
}
