[![Community Extension](https://img.shields.io/badge/Community%20Extension-An%20open%20source%20community%20maintained%20project-FF4700)](https://github.com/camunda-community-hub/community) ![Compatible with: Camunda Platform 8](https://img.shields.io/badge/Compatible%20with-Camunda%20Platform%208-0072Ce) [![](https://img.shields.io/badge/Lifecycle-Stable-brightgreen)](https://github.com/Camunda-Community-Hub/community/blob/main/extension-lifecycle.md#stable-)

# Spring-Zeebe -> Camunda Spring Zeebe SDK

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.camunda/spring-zeebe/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.camunda/spring-zeebe)
[![Project Stats](https://www.openhub.net/p/spring-zeebe/widgets/project_thin_badge.gif)](https://www.openhub.net/p/spring-zeebe)

Spring-Zeebe (this repository) will evolve into Spring Zeebe SDK in the [Zeebe repository](https://github.com/camunda/zeebe) that will be officially supported.

Later on, Camunda will expand Spring Zeebe SDK to deliver a Camunda Spring SDK providing a unified experience for interacting with all Camunda APIs in the automation cluster (Operate, Tasklist, Optimize and Identity) in Java Spring. We recommend migrating to the officially supported Spring Zeebe and Camunda Spring SDK.




## Table of Contents

**Getting Started**

-   [ Version compatibility ](#version-compatibility)
-   [ Examples ](#examples)
-   [ Quickstart ](#quickstart)
-   [ Add Spring Boot Starter to your project](#add-spring-boot-starter-to-your-project)
-   [ Configuring Camunda 8 SaaS connection ](#configuring-camunda-8-saas-connection)
-   [ Connect to Zeebe ](#connect-to-zeebe)
-   [ Implement job worker ](#implement-job-worker)
-   [ Writing test cases ](#writing-test-cases)
-   [ Run Connectors ](#run-connectors)
-   [ Connect to Operate ](#connect-to-operate)

**Documentation**

-   [ Job worker configuration options ](#job-worker-configuration-options)
-   [ Additional configuration options ](#additional-configuration-options)
-   [ Observing metrics ](#observing-metrics)

# Getting started
This project allows you to leverage Zeebe and Operate within your Spring or Spring Boot environment.

## Version compatibility

| Spring Zeebe version | JDK   | Camunda version | Bundled Spring Boot version | Compatible Spring Boot versions |
|----------------------|-------|-----------------|-----------------------------|---------------------------------|
| >= 8.4.0             | >= 17 | 8.4.0           | 3.2.0                       | >= 2.7.x, 3.x.x                 |
| >= 8.3.4             | >= 17 | 8.3.4           | 3.2.0                       | >= 2.7.x, 3.x.x                 |
| >= 8.3.0             | >= 17 | 8.3.1           | 2.7.7                       | >= 2.7.x, 3.x.x                 |
| >= 8.3.0             | >= 8  | 8.3.1           | 2.7.7                       | >= 2.7.x                        |
| >= 8.2.4             | >= 17 | 8.2.4           | 2.7.7                       | >= 2.7.x, 3.x.x                 |
| >= 8.2.4             | >= 8  | 8.2.4           | 2.7.7                       | >= 2.7.x                        |
| >= 8.1.15            | >= 17 | 8.1.x           | 2.7.7                       | >= 2.7.6, 3.x.x                 |
| >= 8.1.15            | >= 8  | 8.1.x           | 2.7.7                       | >= 2.7.6                        |
| <= 8.1.14            | >= 8  | 8.1.x           | 2.7.5                       | = 2.7.x                         |

## Examples

Full examples, including test cases, are available here: [Twitter review example](https://github.com/camunda-community-hub/camunda-cloud-examples/tree/main/twitter-review-java-springboot), [process solution template](https://github.com/camunda-community-hub/camunda-8-process-solution-template). Further, you might want to have a look into the [example/](example/) folder.

## Quickstart

Create a new Spring Boot project (e.g. using [Spring initializr](https://start.spring.io/)), open a pre-existing one you already have, or fork our [Camunda 8 Process Solution Template](https://github.com/camunda-community-hub/camunda-8-process-solution-template).

## Add Spring Boot Starter to your project

Add the following Maven dependency to your Spring Boot Starter project:

```xml
<dependency>
  <groupId>io.camunda.spring</groupId>
  <artifactId>spring-boot-starter-camunda</artifactId>
  <version>8.4.0</version>
</dependency>
```

Although Spring Zeebe has a transitive dependency to the [Zeebe Java client](https://docs.camunda.io/docs/apis-clients/java-client/), you could also add a direct dependency if you need to specify the concrete version in your `pom.xml` (even this is rarely necessary):

```xml
<dependency>
  <groupId>io.camunda</groupId>
  <artifactId>zeebe-client-java</artifactId>
  <version>8.4.0</version>
</dependency>
```

Note that if you are using [@Variables](https://github.com/camunda-community-hub/spring-zeebe#using-variable), compiler flag `-parameters` is required for Spring-Zeebe versions higher than 8.3.1.

If using Maven:
```xml
<build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <configuration>
          <compilerArgs>
            <arg>-parameters</arg>
          </compilerArgs>
        </configuration>
      </plugin>
    </plugins>
  </build>
```

If using Gradle:

```xml
tasks.withType(JavaCompile) {
    options.compilerArgs << '-parameters'
}
```

If using Intellij:

```agsl
Settings > Build, Execution, Deployment > Compiler > Java Compiler
```

## Configuring Camunda 8 connection

The default properties for setting up all connection details are hidden in modes. Each connection mode has meaningful defaults that will make your life easier.

The mode is set on `camunda.client.mode` and can be `simple`, `oidc` or `saas`. Further usage of each mode is explained below.

>Zeebe will now also be configured with an URL (`http://localhost:26500` instead of `localhost:26500` + plaintext connection flag)

### Saas

Connections to Camunda SaaS can be configured by creating the following entries in your `src/main/resources/application.yaml`:

```yaml
camunda:
  client:
    mode: saas
    auth:
      client-id: <your client id>
      client-secret: <your client secret>
    cluster-id: <your cluster id>
    region: <your cluster region>
```

### Simple

If you set up a local dev cluster, your applications will use a cookie to authenticate. As long as the port config is default, there is nothing to configure rather than the according spring profile:

```yaml
camunda:
  client:
    mode: simple
```

If you have different endpoints for your applications, disable a client or adjust the username or password used, you can configure this:

```yaml
camunda:
  client:
    mode: simple
    auth:
      username: demo
      password: demo
    zeebe:
      enabled: true
      base-url: http://localhost:26500
    operate:
      enabled: true
      base-url: http://localhost:8081
    tasklist:
      enabled: true
      base-url: http://localhost:8082
```

### Oidc

If you set up a self-managed cluster with identity, keycloak is used as default identity provider. As long as the port config (from docker-compose or port-forward with the helm charts) is default, you need to configure the according spring profile plus client credentials:

```yaml
camunda:
  client:
    mode: oidc
    auth:
      client-id: <your client id>
      client-secret: <your client secret>
```

If you have different endpoints for your applications or want to disable a client, you can configure this:

```yaml
camunda:
  client:
    mode: oidc
    tenant-ids:
    - <default>
    auth:
      oidc-type: keycloak
      issuer: http://localhost:18080/auth/realms/camunda-platform
    zeebe:
      enabled: true
      base-url: http://localhost:26500
      audience: zeebe-api
    operate:
      enabled: true
      base-url: http://localhost:8081
      audience: operate-api
    tasklist:
      enabled: true
      base-url: http://localhost:8082
      audience: tasklist-api
    optimize:
      enabled: true
      base-url: http://localhost:8083
      audience: optimize-api
    identity:
      enabled: true
      base-url: http://localhost:8084
      audience: identity-api
```

## Connect to Zeebe

You can inject the ZeebeClient and work with it, e.g. to create new workflow instances:

```java
@Autowired
private ZeebeClient client;
```

## Deploy process models

Use the `@Deployment` annotation:

```java
@SpringBootApplication
@EnableZeebeClient
@Deployment(resources = "classpath:demoProcess.bpmn")
public class MySpringBootApplication {
```

This annotation internally uses [the Spring resource loader](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#resources-resourceloader) mechanism which is pretty powerful and can for example also deploy multiple files at once:

```java
@Deployment(resources = {"classpath:demoProcess.bpmn" , "classpath:demoProcess2.bpmn"})
```
or define wildcard patterns:
```java
@Deployment(resources = "classpath*:/bpmn/**/*.bpmn")
```

## Implement job worker

```java
@JobWorker(type = "foo")
public void handleJobFoo(final ActivatedJob job) {
  // do whatever you need to do
}
```

See documentation below for a more in-depth discussion on parameters and configuration options of job workers.

## Writing test cases

You can start up an in-memory test engine and do assertions by adding this Maven dependency:

```xml
<dependency>
  <groupId>io.camunda</groupId>
  <artifactId>spring-zeebe-test</artifactId>
  <version>${spring-zeebe.version}</version>
  <scope>test</scope>
</dependency>
```

Note that **the test engines requires Java version >= 21**. If you cannot run on this Java version, you can use [Testcontainers](https://www.testcontainers.org/) **instead**. Testcontainers require that you have a Docker installation locally available on the developer machine. Use this dependency:

```xml
<!--
  Alternative dependency if you cannot run Java 21, so you will leverage Testcontainer
  Make sure NOT to have spring-zeebe-test on the classpath in parallel!
-->
<dependency>
  <groupId>io.camunda</groupId>
  <artifactId>spring-zeebe-test-testcontainer</artifactId>
  <version>${spring-zeebe.version}</version>
  <scope>test</scope>
</dependency>
```
Using Maven profiles you can also [switch the test dependencies based on the available Java version](https://github.com/camunda-community-hub/camunda-8-process-solution-template/commit/128be0ead988404c5c746ae96b47fe1138bf2a83).

Then, start up the test engine in your test case by adding `@ZeebeSpringTest`

```java
@SpringBootTest
@ZeebeSpringTest
public class TestMyProcess {
  // ...
```

An example test case is [available here](https://github.com/camunda-community-hub/camunda-cloud-examples/blob/main/twitter-review-java-springboot/src/test/java/org/camunda/community/examples/twitter/TestTwitterProcess.java).

## Run Connectors

Spring Zeebe project previously included the Runtime for Camunda 8 Connectors. It has been moved to a separate [Connectors](https://github.com/camunda/connectors) project.
To run Connectors, you can now use the following dependency in your project:

```xml
<dependency>
  <groupId>io.camunda.connector</groupId>
  <artifactId>spring-boot-starter-camunda-connectors</artifactId>
  <version>${connectors.version}</version>
</dependency>
```
To configure the Connector Runtime use the properties explained here:
[Camunda Connector Runtime](https://github.com/camunda/connectors/blob/main/connector-runtime/README.md)

If you have previously used the pure Spring Zeebe project to run Connectors, you should migrate to the new dependency.

You can find the latest version of Connectors on [this page](https://github.com/camunda/connectors/releases).
Consult the [Connector SDK](https://github.com/camunda/connectors/tree/main/connector-sdk/core#connector-core) for details on Connectors in general.

## Connect to Operate

You can inject the CamundaOperateClient and work with it, e.g. to getting and searching process instances:

```java
@Autowired
private CamundaOperateClient client;
```

# Documentation

## Job worker configuration options

### Job type

You can configure the job type via the `JobWorker` annotation:

```java
@JobWorker(type = "foo")
public void handleJobFoo() {
  // handles jobs of type 'foo'
}
```

If you don't specify the `type` the **method name** is used as default:

```java
@JobWorker
public void foo() {
    // handles jobs of type 'foo'
}
```

As a third possibility, you can set a default job type:

```yaml
camunda:
  client:
    zeebe:
      defaults:
        type: foo
```

This is used for all workers that do **not** set a task type via the annotation.


### Define variables to fetch

You can specify that you only want to fetch some variables (instead of all) when executing a job, which can decrease load and improve performance:

```java
@JobWorker(type = "foo", fetchVariables={"variable1", "variable2"})
public void handleJobFoo(final JobClient client, final ActivatedJob job) {
  String variable1 = (String)job.getVariablesAsMap().get("variable1");
  System.out.println(variable1);
  // ...
}
```

### Using `@Variable`

By using the `@Variable` annotation there is a shortcut to make variable retrieval simpler, including the type cast:

```java
@JobWorker(type = "foo")
public void handleJobFoo(final JobClient client, final ActivatedJob job, @Variable String variable1) {
  System.out.println(variable1);
  // ...
}
```



### Using `@VariablesAsType`

You can also use your own class into which the process variables are mapped to (comparable to `getVariablesAsType()` in the Java Client API). Therefore, use the `@VariablesAsType` annotation. In the below example, `MyProcessVariables` refers to your own class:

```java
@JobWorker(type = "foo")
public ProcessVariables handleFoo(@VariablesAsType MyProcessVariables variables){
  // do whatever you need to do
  variables.getMyAttributeX();
  variables.setMyAttributeY(42);

  // return variables object if something has changed, so the changes are submitted to Zeebe
  return variables;
}
```

### Fetch variables via Job

You can access variables of a process via the ActivatedJob object, which is passed into the method if it is a parameter:

```java
@JobWorker(type = "foo")
public void handleJobFoo(final ActivatedJob job) {
  String variable1 = (String)job.getVariablesAsMap().get("variable1");
  sysout(variable1);
  // ...
}
```

### Variable fetching behaviour

With `@Variable`, `@VariablesAsType` or `fetchVariables` you limit which variables are loaded from the workflow engine. You can also override this and force that all variables are loaded anyway:

```java
@JobWorker(type = "foo", fetchAllVariables = true)
public void handleJobFoo(@Variable String variable1) {
}
```

Implicit `fetchVariables` (with `@Variable` or `@VariablesAsType`) will be disabled as soon as you inject yourself the `ActivatedJob`.

### Auto-completing jobs

By default, the `autoComplete` attribute is set to `true` for any job worker.

**Note that the described default behavior of auto-completion was introduced with 8.1 and was different before, see https://github.com/camunda-community-hub/spring-zeebe/issues/239 for details.**

In this case, the Spring integration will take care about job completion for you:

```java
@JobWorker(type = "foo")
public void handleJobFoo(final ActivatedJob job) {
  // do whatever you need to do
  // no need to call client.newCompleteCommand()...
}
```
Which is the same as:

```java
@JobWorker(type = "foo", autoComplete = true)
public void handleJobFoo(final ActivatedJob job) {
  // ...
}
```

Note that the code within the handler method needs to be synchronously executed, as the completion will be triggered right after the method has finished.

When using `autoComplete` you can:

* Return a `Map`, `String`, `InputStream`, or `Object`, which then will be added to the process variables
* Throw a `ZeebeBpmnError` which results in a BPMN error being sent to Zeebe
* Throw any other `Exception` that leads in a failure handed over to Zeebe

```java
@JobWorker(type = "foo")
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

### Programmatically completing jobs

Your job worker code can also complete the job itself. This gives you more control about when exactly you want to complete the job (e.g. allowing the completion to be moved to reactive callbacks):

```java
@JobWorker(type = "foo", autoComplete = false)
public void handleJobFoo(final JobClient client, final ActivatedJob job) {
  // do whatever you need to do
  client.newCompleteCommand(job.getKey())
     .send()
     .exceptionally( throwable -> { throw new RuntimeException("Could not complete job " + job, throwable); });
}
```

Ideally, you **don't** use blocking behavior like `send().join()`, as this is a blocking call to wait for the issues command to be executed on the workflow engine. While this is very straightforward to use and produces easy-to-read code, blocking code is limited in terms of scalability.

That's why the worker above showed a different pattern (using `exceptionally`), often you might also want to use the `whenComplete` callback:

```java
send().whenComplete((result, exception) -> {})
```

This registers a callback to be executed if the command on the workflow engine was executed or resulted in an exception. This allows for parallelism.
This is discussed in more detail in [this blog post about writing good workers for Camunda Cloud](https://blog.bernd-ruecker.com/writing-good-workers-for-camunda-cloud-61d322cad862).

Note that when completing jobs programmatically, you must specify `autoComplete = false`.  Otherwise, there is a race condition between your programmatic job completion and the Spring integration job completion, this can lead to unpredictable results.



### `@CustomHeaders`

You can use the `@CustomHeaders` annotation for a parameter to retrieve [custom headers](https://docs.camunda.io/docs/components/concepts/job-workers/) for a job:

```java
@JobWorker(type = "foo")
public void handleFoo(@CustomHeaders Map<String, String> headers){
  // do whatever you need to do
}
```

Of course, you can combine annotations, for example `@VariablesAsType` and `@CustomHeaders`

```java
@JobWorker
public ProcessVariables foo(@VariablesAsType ProcessVariables variables, @CustomHeaders Map<String, String> headers){
  // do whatever you need to do
  return variables;
}
```

### Throwing `ZeebeBpmnError`s

Whenever your code hits a problem that should lead to a <a href="https://docs.camunda.io/docs/reference/bpmn-processes/error-events/error-events/">BPMN error</a> being raised, you can simply throw a ZeebeBpmnError providing the error code used in BPMN:

```java
@JobWorker(type = "foo")
public void handleJobFoo() {
  // some work
  if (!successful) {
   // problem shall be indicated to the process:
   throw new ZeebeBpmnError("DOESNT_WORK", "This does not work because...");
  }
}
```




## Additional Configuration Options

### Disabling ZeebeClient

If you don't want to use a ZeebeClient for certain scenarios, you can switch it off by setting:

```yaml
camunda:
  client:
    zeebe:
      enabled: false
```

### Default task type


If you build a worker that only serves one thing, it might also be handy to define the worker job type globally - and not in the annotation:

```yaml
camunda:
  client:
    zeebe:
      defaults:
        type: foo
```

### Configure jobs in flight and thread pool

Number of jobs that are polled from the broker to be worked on in this client and thread pool size to handle the jobs:

```yaml
camunda:
  client:
    zeebe:
      defaults:
        max-jobs-active: 32
      execution-threads: 1
```

For a full set of configuration options please see [CamundaClientProperties.java](spring-boot-starter-camunda/src/main/java/io/camunda/zeebe/spring/client/properties/CamundaClientProperties.java)

Note that we generally do not advise to use a thread pool for workers, but rather implement asynchronous code, see [Writing Good Workers](https://docs.camunda.io/docs/components/best-practices/development/writing-good-workers/).


### ObjectMapper customization

If you need to customize the ObjectMapper that the Zeebe client uses to work with variables, you can declare a bean with type `io.camunda.zeebe.client.api.JsonMapper` like this:

```java
@Configuration
class MyConfiguration {
  @Bean
  public JsonMapper jsonMapper() {
    ObjectMapper objectMapper = new ObjectMapper()
      .configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
    new ZeebeObjectMapper(objectMapper);
  }
}
```

### Disable worker

You can disable workers via the `enabled` parameter of the `@JobWorker` annotation :

```java
class SomeClass {
  @JobWorker(type = "foo", enabled = false)
  public void handleJobFoo() {
    // worker's code - now disabled
  }
}
```

You can also override this setting via your `application.yaml` file:

```yaml
camunda:
  client:
    zeebe:
      override:
        foo:
          enabled: false
```

This is especially useful, if you have a bigger code base including many workers, but want to start only some of them. Typical use cases are

* Testing: You only want one specific worker to run at a time
* Load Balancing: You want to control which workers run on which instance of cluster nodes
* Migration: There are two applications, and you want to migrate a worker from one to another. With this switch, you can simply disable workers via configuration in the old application once they are available within the new.

To disable all workers, but still have the zeebe client available, you can use:

```yaml
camunda:
  client:
    zeebe:
      defaults:
        enabled: false
```

### Overriding `JobWorker` values via configuration file

You can override the `JobWorker` annotation's values, as you could see in the example above where the `enabled` property is overridden:

```yaml
camunda:
  client:
    zeebe:
      override:
        foo:
          enabled: false
```

In this case, `foo` is the type of the worker that we want to customize.

You can override all supported configuration options for a worker, e.g.:

```yaml
camunda:
  client:
    zeebe:
      override:
        foo:
          timeout: PT10S
```

You could also provide a custom class that can customize the `JobWorker` configuration values by implementing the `io.camunda.zeebe.spring.client.annotation.customizer.ZeebeWorkerValueCustomizer` interface.

### Enable job streaming

>Please read about this feature in the [docs](https://docs.camunda.io/docs/apis-tools/java-client/job-worker/#job-streaming) upfront.

To control job streaming on the zeebe client, you can configure it:

```yaml
camunda:
  client:
    zeebe:
      defaults:
        stream-enabled: true
```

This also works for every worker individual:

```yaml
camunda:
  client:
    zeebe:
      override:
        foo:
          stream-enabled: true
```

### Control tenant usage

When using multi-tenancy, the zeebe client will connect to the `<default>` tenant. To control this, you can configure:

```yaml
camunda:
  client:
    tenant-ids:
    - <default>
    - foo
```

Additionally, you can set tenant ids on job worker level by using the annotation:

```java
@JobWorker(tenantIds="myOtherTenant")
```

You can override this property as well:

```yaml
camunda:
  client:
    zeebe:
      override:
        foo:
          tenants-ids:
          - <default>
          - foo
```

## Observing metrics

Spring-zeebe-starter will provide some out-of-the-box metrics, that can be leveraged via [Spring Actuator](https://docs.spring.io/spring-boot/docs/current/actuator-api/htmlsingle/). Whenever actuator is on the classpath, you can access the following metrics:

* `camunda.job.invocations`: Number of invocations of job workers (tagging the job type)
* `camunda.connector.inbound.invocations`: Number of invocations of any inbound connectors (tagging the connector type)
* `camunda.connector.outbound.invocations`: Number of invocations of any outbound connectors (tagging the connector type)

For all of those metrics, the following actions are recorded:

* `activated`: The job/connector was activated and started to process an item
* `completed`: The processing was completed successfully
* `failed`: The processing failed with some exception
* `bpmn-error`: The processing completed by throwing an BpmnError (which means there was no technical problem)

In a default setup, you can enable metrics to be served via http:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: metrics
```

And then access them via http://localhost:8080/actuator/metrics/.

# Community Code of Conduct

This project adheres to the Contributor Covenant [Code of Conduct](/.github/CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code. Please [report unacceptable behavior](https://camunda.com/events/code-conduct/reporting-violations/) as soon as possible.
