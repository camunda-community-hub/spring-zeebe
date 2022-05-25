package io.camunda.zeebe.spring.test.proxy;

import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.CheckForNull;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * Dynamic proxy to delegate to a {@link ZeebeTestEngine} which allows to swap the {@link ZeebeTestEngine} object under the hood.
 * This is used in test environments, where the while ZeebeEngine is re-initialized for every test case
 */
public class ZeebeTestEngineProxy extends AbstractInvocationHandler {

  private ZeebeTestEngine delegate;

  public void swapZeebeEngine(ZeebeTestEngine client) {
    this.delegate = client;
  }

  public void removeZeebeEngine() {
  this.delegate = null;
}

  @Override
  protected Object handleInvocation(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
    if (delegate==null) {
      throw new RuntimeException("Cannot invoke " + method + " on ZeebeTestEngine, as ZeebeTestEngine is currently not initialized. Maybe you run outside of a testcase?");
    }
    return method.invoke(delegate, args);
  }

}
