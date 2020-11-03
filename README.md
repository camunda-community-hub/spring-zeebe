# Spring Zeebe

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.zeebe.spring/spring-zeebe/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.zeebe.spring/spring-zeebe)

[![Build Status](https://travis-ci.org/zeebe-io/spring-zeebe.svg?branch=master)](https://travis-ci.org/zeebe-io/spring-zeebe)
[![codecov](https://codecov.io/gh/zeebe-io/spring-zeebe/branch/master/graph/badge.svg)](https://codecov.io/gh/zeebe-io/spring-zeebe)
[![Project Stats](https://www.openhub.net/p/spring-zeebe/widgets/project_thin_badge.gif)](https://www.openhub.net/p/spring-zeebe)

This project allows to leverage Zeebe within your Spring or Spring Boot environment easily. It is basically a wrapper around the [Zeebe Java Client](https://docs.zeebe.io/java-client/).



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

## Deploy Workflow Models

Use the `@ZeebeDeployment` annotation:

```
@SpringBootApplication
@EnableZeebeClient
@ZeebeDeployment(classPathResources = "demoProcess.bpmn")
public class MySpringBootApplication {
```

You can also deploy multiple files at once: 

```
@ZeebeDeployment(classPathResources ={"demoProcess.bpmn", "demoProcess2.bpmn"})
```

## Implement Job Worker

```
@ZeebeWorker(type = "foo")
public void handleJobFoo(final JobClient client, final ActivatedJob job) {
  // do whatever you need to do
  client.newCompleteCommand(job.getKey()) 
     .variables("{\"fooResult\": 1}")
     .send().join();
}
```

## Configuring Zeebe Connection

```
zeebe.client.worker.defaultName=foo-worker
zeebe.client.broker.gatewayAddress=127.0.0.1:26500
zeebe.client.security.plaintext=true
```

If you build a worker that only serves one thing, it might also be handy to define the worker job type

```
zeebe.client.worker.defaultType=foo
```

For a full set of configuration options please see [ZeebeClientConfigurationProperties.java](client/spring-zeebe-starter/src/main/java/io/zeebe/spring/client/properties/ZeebeClientConfigurationProperties.java)

## Configuring Camunda Cloud Connection

Connections to the Camunda Cloud can be easily configured:

```
zeebe.client.cloud.clusterId=xxx
zeebe.client.cloud.clientId=xxx
zeebe.client.cloud.clientSecret=xxx
```

If you don't connect to the Camunda Cloud production environment you might have to also adjust these two properties:

```
zeebe.client.cloud.baseUrl=zeebe.camunda.io
zeebe.client.cloud.port=443
zeebe.client.cloud.authUrl=https://login.cloud.camunda.io/oauth/token
```

As an alternative you can use the [Zeebe Client environment variables](https://docs.zeebe.io/operations/authorization.html#environment-variables). 


## Add Spring Boot Starter to Your Project

Just add the following Maven dependency to your Spring Boot Starter project:

```
<dependency>
	<groupId>io.zeebe.spring</groupId>
	<artifactId>spring-zeebe-starter</artifactId>
	<version>${CURRENT_VERSION}</version>
</dependency>
```

## Examples

Have a look into the [examples/](examples/) folder for working Maven projects that might serve as inspiration.

# Code of Conduct

This project adheres to the Contributor Covenant [Code of
Conduct](/.github/CODE_OF_CONDUCT.md). By participating, you are expected to uphold
this code. Please report unacceptable behavior to
code-of-conduct@zeebe.io.
