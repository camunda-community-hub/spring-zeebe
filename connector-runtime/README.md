# Camunda Connector Runtime

This runtime can execute Camunda Connectors, especially:

* Outbound [Connector functions](../core) via [Zeebe Job Workers](https://docs.camunda.io/docs/components/concepts/job-workers/)
* Inbound [Webhooks](../inbound)

# Concept

# How-to run?

## Via Maven

```bash
mvn exec:java
```

## Via Java

Build via Maven:

```bash
mvn package
```

And afterwards run:

```bash
java -jar target/connector-runtime-VERSION-with-dependencies.jar
```

## Via Docker

Refer to the [Connector Runtime Docker image documentation](https://github.com/camunda/connector-runtime-docker) for further details.

To use the Camunda-provided Connectors with the runtime out of the box, refer to the [Connectors Bundle](https://github.com/camunda/connectors-bundle).

# Building Connector runtime bundles

Often, you want to build one application/deployment, that not only contains the pure runtime, but also some connectors themselves.

There are two ways of achieving this:

1. Create a Maven build that depends on this runtime, but also add connectors as dependencies. 
2. Add connectors to the classpath of the runtime.

The **first approach (Maven)** has the clear advantage, that Maven resolves possible dependency conflicts, 
for example because two connectors use different versions of Jackson.

For example:

```xml
  <dependencies>
    <dependency>
      <groupId>io.camunda</groupId>
      <artifactId>spring-zeebe-connector-runtime</artifactId>
      <version>${version.spring-zeebe}</version>
    </dependency>
    <dependency>
      <groupId>io.camunda.connector</groupId>
      <artifactId>connector-http-json</artifactId>
      <version>${version.connector-http-json}</version>
    </dependency>
    <dependency>
      <groupId>io.camunda.connector</groupId>
      <artifactId>connector-sqs</artifactId>
      <version>${version.connector-sqs}</version>
    </dependency>
  </dependencies>
```

We generally recommend preferring this approach.


The **second approach (Classpath)** has the advantage, that you con build a runtime by pure 
command line or Docker instructions, without any Java knowledge.


To minimize the risk of connectors bringing different versions of the same library, 
we recommend to use [Maven Shade Relocate](https://maven.apache.org/plugins/maven-shade-plugin/examples/class-relocation.html)
to relocate common libraries. A [example can be found in the SQS connector](https://github.com/camunda/connector-sqs/blob/main/pom.xml#L111): 

```xml
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-shade-plugin</artifactId>
        <configuration>
          <relocations>
            <relocation>
              <pattern>com.fasterxml.jackson</pattern>
              <shadedPattern>connectorsqs.com.fasterxml.jackson</shadedPattern>
            </relocation>
          </relocations>
        </configuration>
      </plugin>
    </plugins>
  </build>
```

Now SQS bundles its own copy of Jackson.

See also [Docker: Custom set of connectors](https://docs.camunda.io/docs/self-managed/platform-deployment/docker/#custom-set-of-connectors).

# Configuration Options

The following configuration properties can be set via `src/main/application.properties` if you run Java directly.

You can also set those configuration options via environment variables (then named `ZEEBE_CLIENT_CLOUD_CLUSTER-ID` instead of `zeebe.client.cloud.cluster-id`), especially useful if you run via DOCKER.

In general, the Connector Runtime will respect all properties known to [Spring Zeebe](https://github.com/camunda-community-hub/spring-zeebe), which allows to specify some more options.

## Configure Camunda Platform

### SaaS

To use Camunda Platform 8 SaaS specify the connection properties:

```properties
zeebe.client.cloud.cluster-id=xxx
zeebe.client.cloud.client-id=xxx
zeebe.client.cloud.client-secret=xxx
zeebe.client.cloud.region=bru-2
```

You can further configure separate connection properties for Camunda Operate (othewise it will use the properties configured for Zeebe above):

```properties
camunda.operate.client.client-id=xxx
camunda.operate.client.client-secret=xxx
```

### Local installation

Zeebe:

```properties
zeebe.client.broker.gateway-address=127.0.0.1:26500
zeebe.client.security.plaintext=true
```

Connect to Operate locally using username and password:

```properties
camunda.operate.client.url=http://localhost:8081
camunda.operate.client.username=demo
camunda.operate.client.password=demo
```

When running against a self-managed environment you might also need to configure the keycloak endpoint to not use Operate username/password authentication:

```properties
camunda.operate.client.keycloak-url=http://localhost:18080
camunda.operate.client.keycloak-realm=camunda-platform
camunda.operate.client.client-id=xxx
camunda.operate.client.client-secret=xxx
```

## Adding Outbound Connector Function(s)

### Automatic Connector Discovery

The runtime picks up outbound connectors available on the classpath automatically unless [overriden through manual configuration](#manual-discovery).

It uses the default configuration specified through the `@OutboundConnector` annotation in these cases.

```bash
java -cp 'connector-runtime-VERSION-with-dependencies.jar:connector-http-json-VERSION-with-dependencies.jar' \
    io.camunda.connector.runtime.ConnectorRuntimeApplication
```

Note that you need to use `;` instead of `:` on Windows machines:

```bash
java -cp 'connector-runtime-VERSION-with-dependencies.jar;connector-http-json-VERSION-with-dependencies.jar' \
    io.camunda.connector.runtime.ConnectorRuntimeApplication
```

### Manual Discovery

Use environment variables to configure connectors and their configuration explicitly, without [auto-discovery](#automatic-connector-discovery):

| Environment variable                          | Purpose                                                       |
|:----------------------------------------------|:--------------------------------------------------------------|
| `CONNECTOR_{NAME}_FUNCTION` (required)        | Function to be registered as job worker with the given `NAME` |
| `CONNECTOR_{NAME}_TYPE` (optional)            | Job type to register for worker with `NAME`                   |
| `CONNECTOR_{NAME}_INPUT_VARIABLES` (optional) | Variables to fetch for worker with `NAME`                     |

Through that configuration you define all job workers to run.
Specifying optional values allow you to override `@OutboundConnector` provided connector configuration.

```bash
CONNECTOR_HTTPJSON_FUNCTION=io.camunda.connector.http.HttpJsonFunction
CONNECTOR_HTTPJSON_TYPE=non-default-httpjson-task-type

java -cp 'connector-runtime-VERSION-with-dependencies.jar:connector-http-json-VERSION-with-dependencies.jar' \
    io.camunda.connector.runtime.ConnectorRuntimeApplication
```

### Secrets

#### Local secrets

To inject secrets during connector function execution, export them as environment variables

```bash
export MY_SECRET='foo'
```

Reference the secret in the request payload prefixed with `secrets.MY_SECRET`.

#### Docker Image Secrets

Refer to the [Connector Runtime Docker image documentation](https://github.com/camunda/connector-runtime-docker/blob/main/README.md#secrets) for further details.
