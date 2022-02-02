[![Community Extension](https://img.shields.io/badge/Community%20Extension-An%20open%20source%20community%20maintained%20project-FF4700)](https://github.com/camunda-community-hub/community)

# Spring Zeebe

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.camunda/spring-zeebe/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.camunda/spring-zeebe)

[![Build Status](https://travis-ci.org/zeebe-io/spring-zeebe.svg?branch=master)](https://travis-ci.org/zeebe-io/spring-zeebe)
[![codecov](https://codecov.io/gh/zeebe-io/spring-zeebe/branch/master/graph/badge.svg)](https://codecov.io/gh/zeebe-io/spring-zeebe)
[![Project Stats](https://www.openhub.net/p/spring-zeebe/widgets/project_thin_badge.gif)](https://www.openhub.net/p/spring-zeebe)

This project allows to leverage Zeebe within your Spring or Spring Boot environment easily. It is basically a wrapper around the [Zeebe Java Client](https://docs.camunda.io/docs/product-manuals/clients/java-client/index).


## Add Spring Boot Starter to Your Project

Just add the following Maven dependency to your Spring Boot Starter project:

```
<dependency>
	<groupId>io.camunda</groupId>
	<artifactId>spring-zeebe-starter</artifactId>
	<version>1.3.0</version>
</dependency>
```

# How to use

## Connect to Zeebe Broker

Just add the `@EnableZeebeClient` annotation to your Spring Boot Application:

```
@SpringBootApplication
@EnableZeebeClient
public class MySpringBootApplication {
```

Now you can inject the ZeebeClient and work with it, e.g. to create new workflow instances:

```
@Autowired
private ZeebeClient client;
```

## Deploy Process Models

Use the `@ZeebeDeployment` annotation:

```
@SpringBootApplication
@EnableZeebeClient
@ZeebeDeployment(resources = "classpath:demoProcess.bpmn")
public class MySpringBootApplication {
```

This annotation uses (which internally uses [https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#resources-resourceloader] (the Spring resource loader) mechanism which is pretty powerful and can for example also deploy multiple files at once:

```
@ZeebeDeployment(resources = {"classpath:demoProcess.bpmn" , "classpath:demoProcess2.bpmn"})
```
or define wildcard patterns:
```
@ZeebeDeployment(resources = "classpath*:/bpmn/**/*.bpmn")
```

## Implement Job Worker

```
@ZeebeWorker(type = "foo")
public void handleJobFoo(final JobClient client, final ActivatedJob job) {
  // do whatever you need to do
  client.newCompleteCommand(job.getKey()) 
     .variables("{\"fooResult\": 1}")
     .send()
     .exceptionally( throwable -> { throw new RuntimeException("Could not complete job " + job, throwable); });
}
```

### Fetch Variables

You can access all variables of a process via the job:

```
@ZeebeWorker(type = "foo")
public void handleJobFoo(final JobClient client, final ActivatedJob job) {
  String variable1 = (String)job.getVariablesAsMap().get("variable1");
  sysout(variable1);
  // ...
}
```


You can specify that you only want to fetch some variables (instead of all) when executing a job, which can decrease load and improve performance:

```
@ZeebeWorker(type = "foo", fetchVariables={"variable1", "variable2"})
public void handleJobFoo(final JobClient client, final ActivatedJob job) {
  String variable1 = (String)job.getVariablesAsMap().get("variable1");
  System.out.println(variable1);
  // ...
}
```

By using the `@ZeebeVariable` annotation there is a shortcut to make variable retrieval simpler, including the type cast:

```
@ZeebeWorker(type = "foo")
public void handleJobFoo(final JobClient client, final ActivatedJob job, @ZeebeVariable String variable1) {
  System.out.println(variable1);
  // ...
}
```

With `@ZeebeVariable` or `fetchVariables` you limit which variables are loaded from the workflow engine. You can also overwrite this and force that all variables are loaded anyway:

```
@ZeebeWorker(type = "foo", forceFetchAllVariables = true)
public void handleJobFoo(final JobClient client, final ActivatedJob job, @ZeebeVariable String variable1) {
}
```

### Completing the job

As a default, your job handler code has to also complete the job, otherwise Zeebe will not know you did your work correctly:

```
@ZeebeWorker(type = "foo")
public void handleJobFoo(final JobClient client, final ActivatedJob job) {
  // do whatever you need to do
  client.newCompleteCommand(job.getKey()) 
     .send()
     .exceptionally( throwable -> { throw new RuntimeException("Could not complete job " + job, throwable); });
}
```

Ideally, you **don't** use blocking behavior like `send().join()`, as this is a blocking call to wait for the issues command to be executed on the workflow engine. While this is very straightforward to use and produces easy-to-read code, blocking code is limited in terms of scalability.

That's why the worker showed a different pattern:
```
send().whenComplete((result, exception) -> {})
```
This registers a callback to be executed if the command on the workflow engine was executed or resulted in an exception. This allows for parallelism.
This is discussed in more detail in [this blog post about writing good workers for Camunda Cloud](https://blog.bernd-ruecker.com/writing-good-workers-for-camunda-cloud-61d322cad862).

To ease things, you can also set `autoComplete=true` for the worker, than the Spring integration will take care if job completion for you:

```
@ZeebeWorker(type = "foo", autoComplete = true)
public void handleJobFoo(final ActivatedJob job) {
  // do whatever you need to do
  // but no need to call client.newCompleteCommand()...
}
```

Note that the code within the handler method needs to be synchronously executed, as the completion will be triggered right after the method has finished.

When using `autoComplete` you can:

* Return a `Map`, `String`, `InputStream`, or `Object`, which then will be added to the process variables
* Throw a `ZeebeBpmnError` which results in a BPMN error being sent to Zeebe
* Throw any other `Exception` that leads in an failure handed over to Zeebe

```
@ZeebeWorker(type = "foo", autoComplete = true)
public Map<String, Object> handleJobFoo(final ActivatedJob job) {
  // some work
  if (successful) {
    // some data is returned to be stored as process variable
    return variablesMap;
  } else {
   // problem shall be indicated to the process:
   throw new ZeebeBpmnError("DOESNT_WORK", "This does not work because...");
  }
}
```

When using `autoComplete` you can also use your own class variables are mapped to (comparable to `getVariablesAsType()` in the API). Therefore use the `@ZeebeVariablesAsType` annotation:

```java
@ZeebeWorker(type = "foo", autoComplete = true)
public ProcessVariables handleFoo(@ZeebeVariablesAsType ProcessVariables variables){
  // do whatever you need to do
  variables.getMyAttribueX();
  variables.setMyAttribueY(42);
  // return variables object if something has changed, so the changes are submitted to Zeebe
  return variables;
}
```


### Throwing ZeebeBpmnError's

Whenever your code hits a problem that should lead to a <a href="https://docs.camunda.io/docs/reference/bpmn-processes/error-events/error-events/">BPMN error</a> being raised, you can simply throw a ZeebeBpmnError providing the error code used in BPMN:

```
@ZeebeWorker(type = "foo")
public void handleJobFoo() {
  // some work
  if (!successful) {
   // problem shall be indicated to the process:
   throw new ZeebeBpmnError("DOESNT_WORK", "This does not work because...");
  }
}
```



## Configuring Camunda Cloud Connection

Connections to the Camunda Cloud can be easily configured:

```
zeebe.client.cloud.cluster-id=xxx
zeebe.client.cloud.client-id=xxx
zeebe.client.cloud.client-secret=xxx
zeebe.client.cloud.region=bru-2
```

If you don't connect to the Camunda Cloud production environment you might have to also adjust these two properties:

```
zeebe.client.cloud.base-url=zeebe.camunda.io
zeebe.client.cloud.port=443
zeebe.client.cloud.auth-url=https://login.cloud.camunda.io/oauth/token
```

As an alternative you can use the [Zeebe Client environment variables](https://docs.camunda.io/docs/components/clients/java-client/index/#bootstrapping). 

## Configuring Self-managed Zeebe Connection

```
zeebe.client.broker.gateway-address=127.0.0.1:26500
zeebe.client.security.plaintext=true
```

## Additional Configuration Options

If you build a worker that only serves one thing, it might also be handy to define the worker job type globally - and not in the annotation:

```
zeebe.client.worker.defaultType=foo
```

Number of jobs that are polled from the broker to be worked on in this client and thread pool size to handle the jobs:

```
zeebe.client.worker.max-jobs-active=32
zeebe.client.worker.threads=1
```

For a full set of configuration options please see [ZeebeClientConfigurationProperties.java](client/spring-zeebe-starter/src/main/java/io/camunda/zeebe/spring/client/config/ZeebeClientStarterAutoConfiguration.java)

## ObjectMapper customization
If you need to customize the ObjectMapper that the Zeebe client uses to work with variables, you can declare a bean with type `io.camunda.zeebe.client.api.JsonMapper` like this:
```java
@Configuration
class MyConfiguration {
  @Bean
  public JsonMapper jsonMapper() {
    new ZeebeObjectMapper().enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
  }
}
```


## Examples

Have a look into the [examples/](examples/) folder for working Maven projects that might serve as inspiration.

# Code of Conduct

This project adheres to the Contributor Covenant [Code of
Conduct](/.github/CODE_OF_CONDUCT.md). By participating, you are expected to uphold
this code. Please report unacceptable behavior to
code-of-conduct@zeebe.io.
