package io.camunda.operate.util;

import io.camunda.operate.model.*;
import io.camunda.operate.exception.OperateException;
import io.camunda.operate.search.*;

import java.util.HashMap;
import java.util.Map;

public class QueryValidatorUtils {

  private static Map<Class<?>, Class<?>> TYPE_FILTERS = new HashMap<>();

  private QueryValidatorUtils() {
  }

  private static Class<?> getFilterClass(Class<?> resultType) {
    if (TYPE_FILTERS.isEmpty()) {
      TYPE_FILTERS.put(ProcessDefinition.class, ProcessDefinitionFilter.class);
      TYPE_FILTERS.put(ProcessInstance.class, ProcessInstanceFilter.class);
      TYPE_FILTERS.put(FlowNodeInstance.class, FlownodeInstanceFilter.class);
      TYPE_FILTERS.put(Incident.class, IncidentFilter.class);
      TYPE_FILTERS.put(Variable.class, VariableFilter.class);
    }
    return TYPE_FILTERS.get(resultType);
  }

  public static <T> void verifyQuery(SearchQuery query, Class<T> resultType) throws OperateException {
    if (query.getFilter() != null && query.getFilter().getClass() != getFilterClass(resultType)) {
      throw new OperateException(
          "You should rely on "+TYPE_FILTERS.get(resultType).getSimpleName()+" for searching on "+resultType.getSimpleName());
    }
  }
}
