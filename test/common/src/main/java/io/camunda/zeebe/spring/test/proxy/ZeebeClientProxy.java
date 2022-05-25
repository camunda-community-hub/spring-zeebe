package io.camunda.zeebe.spring.test.proxy;

import io.camunda.zeebe.client.ZeebeClient;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.CheckForNull;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Dynamic proxy to delegate to a {@link ZeebeClient} which allows to swap the ZeebeClient object under the hood.
 * This is used in test environments, where the while Zeebe engine is re-initialized for every test case
 */
public class ZeebeClientProxy extends AbstractInvocationHandler  {

  private ZeebeClient delegate;

  public void swapZeebeClient(ZeebeClient client) {
    this.delegate = client;
  }

  public void removeZeebeClient() {
    this.delegate = null;
  }

  @Override
  protected Object handleInvocation(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
    if (delegate==null) {
      throw new RuntimeException("Cannot invoke " + method + " on ZeebeClient, as ZeebeClient is currently not initialized. Maybe you run outside of a testcase?");
    }
    return method.invoke(delegate, args);
  }
}
