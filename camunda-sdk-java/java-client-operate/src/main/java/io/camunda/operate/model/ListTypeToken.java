package io.camunda.operate.model;

import com.google.common.reflect.TypeToken;
import java.util.List;

// See https://github.com/google/guava/wiki/ReflectionExplained
public class ListTypeToken {
  public static TypeToken<List<FlowNodeStatistics>> listFlowNodeStatistics =
      new TypeToken<List<FlowNodeStatistics>>() {};
  public static TypeToken<List<String>> listSequenceFlows = new TypeToken<List<String>>() {};
}
