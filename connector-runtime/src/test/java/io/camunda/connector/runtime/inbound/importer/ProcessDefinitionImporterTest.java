package io.camunda.connector.runtime.inbound.importer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import io.camunda.connector.runtime.inbound.lifecycle.InboundConnectorManager;
import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.dto.ProcessDefinition;
import io.camunda.operate.dto.SearchResult;
import io.camunda.operate.exception.OperateException;
import io.camunda.operate.search.SearchQuery;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProcessDefinitionImporterTest {

  private InboundConnectorManager manager;
  private CamundaOperateClient operate;

  @BeforeEach
  public void initMocks() {
    manager = mock(InboundConnectorManager.class);
    operate = mock(CamundaOperateClient.class);
  }

  @Test
  public void shouldRequestProcessDefinition_Pagination() throws OperateException {
    // given
    List<ProcessDefinition> firstPage = List.of(
      new ProcessDefinition(), new ProcessDefinition(), new ProcessDefinition());
    List<ProcessDefinition> secondPage = List.of();

    List<Object> paginationIdx = List.of(new Object());

    var firstOperateResponse = new SearchResult<ProcessDefinition>();
    firstOperateResponse.setItems(firstPage);
    firstOperateResponse.setSortValues(paginationIdx);

    var secondOperateResponse = new SearchResult<ProcessDefinition>();
    secondOperateResponse.setItems(secondPage);

    when(operate.search(any(), eq(ProcessDefinition.class)))
      .thenReturn(firstOperateResponse)
      .thenReturn(secondOperateResponse);

    var importer = new ProcessDefinitionImporter(operate, manager);

    // when
    importer.scheduleImport();

    // then
    var queryCaptor = ArgumentCaptor.forClass(SearchQuery.class);

    verify(operate, times(2)).search(queryCaptor.capture(), eq(ProcessDefinition.class));
    verifyNoMoreInteractions(operate);

    var queries = queryCaptor.getAllValues();

    var firstQuery = queries.get(0);
    assertNull(firstQuery.getSearchAfter());
    assertEquals(20, firstQuery.getSize());

    var secondQuery = queries.get(1);
    assertSame(paginationIdx, secondQuery.getSearchAfter());
    assertEquals(20, secondQuery.getSize());

    verify(manager, times(1)).registerProcessDefinitions(firstPage);
  }
}
