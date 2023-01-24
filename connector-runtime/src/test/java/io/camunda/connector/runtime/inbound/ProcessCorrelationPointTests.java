package io.camunda.connector.runtime.inbound;

import io.camunda.connector.runtime.inbound.correlation.StartEventCorrelationPoint;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProcessCorrelationPointTests {

  @Test
  void startEvent_shouldBeSortableByProcessVersion() {
    // given
    StartEventCorrelationPoint p1 = new StartEventCorrelationPoint(0, "process1", 0);
    StartEventCorrelationPoint p2 = new StartEventCorrelationPoint(1, "process2", 0);
    StartEventCorrelationPoint p3 = new StartEventCorrelationPoint(2, "process1", 1);

    // when
    List<StartEventCorrelationPoint> sortedPoints = List.of(p1, p2, p3);
    sortedPoints = sortedPoints.stream().sorted().collect(Collectors.toList());

    // then
    Iterator<StartEventCorrelationPoint> iter = sortedPoints.iterator();
    // points are sorted by
    Assertions.assertThat(iter.next().getProcessDefinitionKey()).isEqualTo(0);
    Assertions.assertThat(iter.next().getProcessDefinitionKey()).isEqualTo(2);
    Assertions.assertThat(iter.next().getProcessDefinitionKey()).isEqualTo(1);
    Assertions.assertThat(iter.hasNext()).isFalse();
  }
}
