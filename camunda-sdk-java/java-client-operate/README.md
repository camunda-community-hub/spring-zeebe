# Java Operate client

## Build the client

### SaaS Authentication

```java
  JwtConfig jwtConfig = new JwtConfig();
  jwtConfig.addProduct(Product.OPERATE, new JwtCredential(clientId, clientSecret));
  targetOperateUrl = "https://" + region + ".operate.camunda.io/" + clusterId;
  auth = SaaSAuthentication.builder().jwtConfig(jwtConfig).build();

  client = CamundaOperateClient.builder()
              .operateUrl(targetOperateUrl)
              .authentication(auth)
              .setup()
              .build();
```

### SelfManaged Authentication

```java
  JwtConfig jwtConfig = new JwtConfig();
  jwtConfig.addProduct(Product.OPERATE, new JwtCredential(clientId, clientSecret));
  auth = SelfManagedAuthentication.builder().jwtConfig(jwtConfig).keycloakUrl(keycloakUrl).build();

  client = CamundaOperateClient.builder()
              .operateUrl(operateUrl)
              .authentication(auth)
              .setup()
              .build();
```

## Use the client

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
