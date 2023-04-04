[![Community Extension](https://img.shields.io/badge/Community%20Extension-An%20open%20source%20community%20maintained%20project-FF4700)](https://github.com/camunda-community-hub/community) ![Compatible with: Camunda Platform 8](https://img.shields.io/badge/Compatible%20with-Camunda%20Platform%208-0072Ce) [![](https://img.shields.io/badge/Lifecycle-Stable-brightgreen)](https://github.com/Camunda-Community-Hub/community/blob/main/extension-lifecycle.md#stable-)

***We search YOU to improve Spring Zeebe!***

We are currently searching for a software engineer with profound Java and Spring Boot experience to take care of the Java SDK of Camunda Platform 8, which includes Spring Zeebe as an important part. You can find the job description and more information here: [Senior Developer Experience Engineer](https://camunda.com/jobs/?gh_jid=5391333003). We can hire globally for a remote setup and are looking forward to your application!

Thanks for listening - now back to documentation :-) 

# Spring Zeebe

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.camunda/spring-zeebe/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.camunda/spring-zeebe)
[![Project Stats](https://www.openhub.net/p/spring-zeebe/widgets/project_thin_badge.gif)](https://www.openhub.net/p/spring-zeebe)

This project allows to leverage Zeebe (the orchestration engine that comes as part of Camunda Platform 8) within your Spring or Spring Boot environment easily. It is basically a wrapper around the [Zeebe Java Client](https://docs.camunda.io/docs/product-manuals/clients/java-client).

## Version Compatibility

| Spring Zeebe version | JDK | Camunda Platform version | Bundled Spring Boot version | Compatible Spring Boot versions |
|----------------------| --- | ------------------------ |-----------------------------| ----------------------------|
| >= 8.1.15            | > 11 | 8.1.x                    | 2.7.7  | >= 2.7.6, 3.0.x  |
| < 8.1.14             | > 11 | 8.1.x                    | 2.7.5  | = 2.7.x (no 3.0.x)            |

## Examples

There are full examples, including test cases, are available here: [Twitter Review example](https://github.com/camunda-community-hub/camunda-cloud-examples/tree/main/twitter-review-java-springboot), [Process Solution Template](https://github.com/camunda-community-hub/camunda-8-process-solution-template). Further, you might want to have a look into the [example/](example/) folder.

# Get Started

Create a new Spring Boot project (e.g. using [Spring initializr](https://start.spring.io/)), or open a pre-existing one you already have, or simply fork our [Camunda Platform 8 Process Solution Template](https://github.com/camunda-community-hub/camunda-8-process-solution-template).

## Add Spring Boot Starter to Your Project

Add the following Maven dependency to your Spring Boot Starter project:

```xml
<dependency>
  <groupId>io.camunda</groupId>
  <artifactId>spring-zeebe-starter</artifactId>
  <version>8.1.17</version>
</dependency>
```

Although Spring Zeebe has a transitive dependency to the [Zeebe Java Client](https://docs.camunda.io/docs/apis-clients/java-client/), you could also add a direct dependency if you need to specify the concrete version in your `pom.xml` (even this is rarely necessary):

```xml
<dependency>
  <groupId>io.camunda</groupId>
  <artifactId>zeebe-client-java</artifactId>
  <version>8.1.9</version>
</dependency>
```


## Configuring Camunda Platform 8 SaaS Connection

Connections to the Camunda SaaS can be easily configured, create the following entries in your `src/main/resources/application.properties`:

```properties
zeebe.client.cloud.cluster-id=xxx
zeebe.client.cloud.client-id=xxx
zeebe.client.cloud.client-secret=xxx
zeebe.client.cloud.region=bru-2
```

You can also configure the connection to a self-managed Zeebe broker:

```properties
zeebe.client.broker.gateway-address=127.0.0.1:26500
zeebe.client.security.plaintext=true
```

You can enforce the right connection mode, for example if multiple contradicting properties are set:

```properties
zeebe.client.connection-mode=CLOUD
zeebe.client.connection-mode=ADDRESS
```

## Connect to Zeebe

Add the `@EnableZeebeClient` annotation to your Spring Boot Application:

```java
@SpringBootApplication
@EnableZeebeClient
public class MySpringBootApplication {
```

Now you can inject the ZeebeClient and work with it, e.g. to create new workflow instances:

```java
@Autowired
private ZeebeClient client;
```

## Deploy Process Models

Use the `@Deployment` annotation:

```java
@SpringBootApplication
@EnableZeebeClient
@Deployment(resources = "classpath:demoProcess.bpmn")
public class MySpringBootApplication {
```

This annotation uses (which internally uses [https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#resources-resourceloader] (the Spring resource loader) mechanism which is pretty powerful and can for example also deploy multiple files at once:

```java
@Deployment(resources = {"classpath:demoProcess.bpmn" , "classpath:demoProcess2.bpmn"})
```
or define wildcard patterns:
```java
@Deployment(resources = "classpath*:/bpmn/**/*.bpmn")
```

## Implement Job Worker

```java
@JobWorker(type = "foo")
public void handleJobFoo(final ActivatedJob job) {
  // do whatever you need to do
}
```

See documentation below for a more in-depth discussion on parameters and configuration options of JobWorkers.

## Writing test cases

You can startup an in-memory test engine and do assertions by adding this Maven dependency:

```xml
<dependency>
  <groupId>io.camunda</groupId>
  <artifactId>spring-zeebe-test</artifactId>
  <version>${spring-zeebe.version}</version>
  <scope>test</scope>
</dependency>
```

Note that **the test engines requires Java version >= 17**. If you cannot run on this Java version, you can use [Testcontainers](https://www.testcontainers.org/) **instead**. Testcontainers require that you have a docker installation locally available on the developer machine. Use this dependency:

```xml
<!-- 
  Alternative dependency if you cannot run Java 17, so you will leverage Testcontainer 
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

Then you need to startup the test engine in your test case by adding `@ZeebeSpringTest`

```java
@SpringBootTest
@ZeebeSpringTest
public class TestMyProcess {
  // ...  
```

An example test case is [available here](https://github.com/camunda-community-hub/camunda-cloud-examples/blob/main/twitter-review-java-springboot/src/test/java/org/camunda/community/examples/twitter/TestTwitterProcess.java).



## Run OutboundConnectors

Consult the [Connector SDK](https://github.com/camunda/connector-sdk) for details on Connectors in general.

You can run `OutboundConnectorFunction`s using Spring Zeebe with any of the following options:

* Expose them as Spring beans and run them directly in your own application.
* Run them using the pre-packaged [Connector runtime](./connector-runtime) application.

### Run OutboundConnector as Spring bean

If you have the following outbound Connector function:

```
@OutboundConnector(
        name = "Twitter",
        inputVariables = {"tweetContent"},
        type = "io.berndruecker.example.TwitterConnector:1"
)
public class TwitterOutboundConnector implements OutboundConnectorFunction {
```

You can just expose it as a Spring bean:

```
@Configuration
public class ConnectorFactory {
    @Bean
    public TwitterOutboundConnector twitterOutboundConnector() {
        return new TwitterOutboundConnector();
    }
}
```

Now a worker for this Connector will be started in the background. You can disable this by:

```properties
zeebe.client.worker.connectors.enabled=false
```

You can also use `ZeebeWorkerValueCustomizer` or specific properties to configure the workers for the connector, for example disabling specific connectors (using the worker type). When the worker type contains a dot (.) or colon (:) replace this by an underscore (_):

```properties
zeebe.client.worker.override.io_berndruecker_example_TwitterConnector_1.enabled=false
```


# Documentation


## Job worker configuration options

### Job Type

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

```properties
zeebe.client.worker.default-type=foo
```

This is used for all workers that do **not** set a task type via the annoation.


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

With `@Variable` or `fetchVariables` you limit which variables are loaded from the workflow engine. You can also override this and force that all variables are loaded anyway:

```java
@JobWorker(type = "foo", fetchAllVariables = true)
public void handleJobFoo(final JobClient client, final ActivatedJob job, @Variable String variable1) {
}
```

### Using `@VariablesAsType`

You can also use your own class into which the process variables are mapped to (comparable to `getVariablesAsType()` in the Java Client API). Therefore use the `@VariablesAsType` annotation. In the below example, `MyProcessVariables` refers to your own class:

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
* Throw any other `Exception` that leads in an failure handed over to Zeebe

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

Of course you can combine annotations, for example `@VariablesAsType` and `@CustomHeaders`

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

```properties
zeebe.client.enabled=false
```

### Configuring Self-managed Zeebe Connection

```properties
zeebe.client.broker.gateway-address=127.0.0.1:26500
zeebe.client.security.plaintext=true
```

### Configure different cloud environments

If you don't connect to the Camunda SaaS production environment you might have to also adjust these properties:

```properties
zeebe.client.cloud.base-url=zeebe.camunda.io
zeebe.client.cloud.port=443
zeebe.client.cloud.auth-url=https://login.cloud.camunda.io/oauth/token
```

As an alternative you can use the [Zeebe Client environment variables](https://docs.camunda.io/docs/components/clients/java-client/index/#bootstrapping). 


### Default task type


If you build a worker that only serves one thing, it might also be handy to define the worker job type globally - and not in the annotation:

```properties
zeebe.client.worker.defaultType=foo
```

### Configure jobs in flight and thread pool

Number of jobs that are polled from the broker to be worked on in this client and thread pool size to handle the jobs:

```properties
zeebe.client.worker.max-jobs-active=32
zeebe.client.worker.threads=1
```

For a full set of configuration options please see [ZeebeClientConfigurationProperties.java](client/spring-zeebe-starter/src/main/java/io/camunda/zeebe/spring/client/config/ZeebeClientStarterAutoConfiguration.java)

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

You can also override this setting via your `application.properties` file:

```properties
zeebe.client.worker.override.foo.enabled=false
```

This is especially useful, if you have a bigger code base including many workers, but want to start only some of them. Typical use cases are 

* Testing: You only want one specific worker to run at a time
* Load Balancing: You want to control which workers run on which instance of cluster nodes
* Migration: There are two applications, and you want to migrate a worker from one to another. With this switch, you can simply disable workers via configuration in the old application once they are available within the new. 


### Overriding `JobWorker` values via configuration file

You can override the `JobWorker` annotation's values, as you could see in the example above where the `enabled` property is overridden:

```properties
zeebe.client.worker.override.foo.enabled=false
```

In this case, `foo` is the type of the worker that we want to customize.

You can override all supported configuration options for a worker, e.g.:

```properties
zeebe.client.worker.override.foo.timeout=10000
```

You could also provide a custom class that can customize the `JobWorker` configuration values by implementing the `io.camunda.zeebe.spring.client.annotation.customizer.ZeebeWorkerValueCustomizer` interface.


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

In a default setup, you can can enable metrics to be served via http:

```properties
management.endpoints.web.exposure.include=metrics
```

And then access them via http://localhost:8080/actuator/metrics/. 

# Code of Conduct

This project adheres to the Contributor Covenant [Code of Conduct](/.github/CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code. Please report unacceptable behavior to code-of-conduct@zeebe.io.
