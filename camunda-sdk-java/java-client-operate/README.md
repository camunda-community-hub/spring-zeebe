# Java Operate client

The Spring Boot Starter contains the Operate client which becomes injectable as a bean.

```java
@Autowired CamundaOperateClient operateClient;
```

### List deployed process definitions

```java
  ProcessDefinitionFilter processDefinitionFilter = ProcessDefinitionFilter.builder().build();
  SearchQuery procDefQuery = new SearchQuery.Builder()
            .filter(processDefinitionFilter)
            .size(1000)
            .sort(new Sort("version", SortOrder.DESC))
            .build();
  return camundaOperateClient.searchProcessDefinitions(procDefQuery);
```

### Read process definitions content

```java
  camundaOperateClient.getProcessDefinitionXml(ProcessDefinitionKey);
```

### List variables

```java
  return camundaOperateClient.searchVariables(new SearchQuery.Builder().filter(new VariableFilter()).size(100).build());
```
