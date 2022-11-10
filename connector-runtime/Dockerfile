FROM eclipse-temurin:17.0.5_8-jre
RUN mkdir /opt/app

COPY target/connector-runtime-*-with-dependencies.jar /opt/app

# Using entry point to allow downstream images to add JVM arguments using CMD
ENTRYPOINT ["java", "-cp", "/opt/app/*", "io.camunda.connector.runtime.ConnectorRuntimeApplication"]
