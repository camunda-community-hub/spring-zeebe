package io.camunda.connector.runtime;

import io.camunda.zeebe.spring.client.connector.MetricsRecorder;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

public class SimpleMetricsRecorder implements MetricsRecorder {
  public HashMap<String, AtomicLong> counters = new HashMap<>();

  @Override
  public void increase(String metricName, String action, String type) {
    String key = key(metricName, action, type);
    if (!counters.containsKey(key)) {
      counters.put(key, new AtomicLong(1));
    } else {
      counters.get(key).incrementAndGet();
    }
  }

  private String key(String metricName, String action, String type) {
    String key = metricName + "#" + action + "#" + type;
    return key;
  }

  public long getCount(String metricName, String action, String type) {
    return counters.get(key(metricName, action, type)).get();
  }
}
