package io.camunda.zeebe.spring.client.jobhandling;

import io.camunda.zeebe.client.ZeebeClient;
import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoExtendTimeoutManager {
  private static final Logger LOG = LoggerFactory.getLogger(AutoExtendTimeoutManager.class);
  private final ZeebeClientExecutorService zeebeClientExecutorService;
  private final ZeebeClient zeebeClient;

  public AutoExtendTimeoutManager(
      ZeebeClientExecutorService zeebeClientExecutorService, ZeebeClient zeebeClient) {
    this.zeebeClientExecutorService = zeebeClientExecutorService;
    this.zeebeClient = zeebeClient;
  }

  public ScheduledFuture<?> startAutoExtendTimeout(long deadline, long jobKey, Duration period) {
    return zeebeClientExecutorService
        .get()
        .scheduleAtFixedRate(
            () -> extendTimeout(jobKey, period),
            Integer.max(0, (int) (deadline - System.currentTimeMillis() - 5000)),
            period.minusSeconds(5).getSeconds(),
            TimeUnit.SECONDS);
  }

  private void extendTimeout(long jobKey, Duration timeout) {
    LOG.trace("Updating job timeout of job {} by {}",jobKey,timeout.toString());
    zeebeClient.newUpdateTimeoutCommand(jobKey).timeout(timeout).send().join();
  }
}
