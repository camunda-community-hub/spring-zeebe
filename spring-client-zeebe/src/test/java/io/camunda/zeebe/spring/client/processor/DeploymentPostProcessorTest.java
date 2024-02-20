package io.camunda.zeebe.spring.client.processor;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.ZeebeFuture;
import io.camunda.zeebe.client.api.command.DeployResourceCommandStep1;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.client.api.response.Process;
import io.camunda.zeebe.spring.client.annotation.Deployment;
import io.camunda.zeebe.spring.client.annotation.processor.ZeebeDeploymentAnnotationProcessor;
import io.camunda.zeebe.spring.client.bean.ClassInfo;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class DeploymentPostProcessorTest {

  @Mock private ZeebeClient client;

  @Mock private DeployResourceCommandStep1 deployStep1;

  @Mock private DeployResourceCommandStep1.DeployResourceCommandStep2 deployStep2;

  @Mock private ZeebeFuture<DeploymentEvent> zeebeFuture;

  @Mock private DeploymentEvent deploymentEvent;

  private ZeebeDeploymentAnnotationProcessor deploymentPostProcessor;

  @BeforeEach
  public void init() {
    MockitoAnnotations.initMocks(this);
    deploymentPostProcessor = Mockito.spy(new ZeebeDeploymentAnnotationProcessor());
  }

  @Deployment(resources = "/1.bpmn")
  private static class WithSingleClassPathResource {}

  @Test
  public void shouldDeploySingleResourceTest() {
    // given
    ClassInfo classInfo = ClassInfo.builder().bean(new WithSingleClassPathResource()).build();

    Resource resource = Mockito.mock(FileSystemResource.class);

    when(resource.getFilename()).thenReturn("1.bpmn");

    when(client.newDeployResourceCommand()).thenReturn(deployStep1);

    when(deploymentPostProcessor.getResources(anyString())).thenReturn(new Resource[] {resource});

    when(deployStep1.addResourceStream(any(), anyString())).thenReturn(deployStep2);

    when(deployStep2.send()).thenReturn(zeebeFuture);

    when(zeebeFuture.join()).thenReturn(deploymentEvent);

    when(deploymentEvent.getProcesses()).thenReturn(Collections.singletonList(getProcess()));

    // when
    deploymentPostProcessor.configureFor(classInfo);
    deploymentPostProcessor.start(client);

    // then
    verify(deployStep1).addResourceStream(any(), eq("1.bpmn"));
    verify(deployStep2).send();
    verify(zeebeFuture).join();
  }

  @Deployment(resources = {"classpath*:/1.bpmn", "classpath*:/2.bpmn"})
  private static class WithDoubleClassPathResource {}

  @Test
  public void shouldDeployMultipleResourcesTest() {
    // given
    ClassInfo classInfo = ClassInfo.builder().bean(new WithDoubleClassPathResource()).build();

    Resource[] resources = {
      Mockito.mock(FileSystemResource.class), Mockito.mock(FileSystemResource.class)
    };

    when(resources[0].getFilename()).thenReturn("1.bpmn");
    when(resources[1].getFilename()).thenReturn("2.bpmn");

    when(client.newDeployResourceCommand()).thenReturn(deployStep1);

    when(deploymentPostProcessor.getResources("classpath*:/1.bpmn"))
        .thenReturn(new Resource[] {resources[0]});

    when(deploymentPostProcessor.getResources("classpath*:/2.bpmn"))
        .thenReturn(new Resource[] {resources[1]});

    when(deployStep1.addResourceStream(any(), anyString())).thenReturn(deployStep2);

    when(deployStep2.send()).thenReturn(zeebeFuture);

    when(zeebeFuture.join()).thenReturn(deploymentEvent);

    when(deploymentEvent.getProcesses()).thenReturn(Collections.singletonList(getProcess()));

    // when
    deploymentPostProcessor.configureFor(classInfo);
    deploymentPostProcessor.start(client);

    // then
    verify(deployStep1).addResourceStream(any(), eq("1.bpmn"));
    verify(deployStep1).addResourceStream(any(), eq("1.bpmn"));

    verify(deployStep2).send();
    verify(zeebeFuture).join();
  }

  @Deployment(resources = {})
  private static class WithNoClassPathResource {}

  @Test
  public void shouldThrowExceptionOnNoResourcesToDeploy() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          // given
          ClassInfo classInfo = ClassInfo.builder().bean(new WithNoClassPathResource()).build();

          when(client.newDeployResourceCommand()).thenReturn(deployStep1);

          when(deployStep1.addResourceStream(any(), anyString())).thenReturn(deployStep2);

          // when
          deploymentPostProcessor.configureFor(classInfo);
          deploymentPostProcessor.start(client);
        });
  }

  private Process getProcess() {
    return new Process() {
      @Override
      public String getBpmnProcessId() {
        return "12345-abcd";
      }

      @Override
      public int getVersion() {
        return 1;
      }

      @Override
      public long getProcessDefinitionKey() {
        return 101010;
      }

      @Override
      public String getResourceName() {
        return "TestProcess";
      }

      @Override
      public String getTenantId() {
        return "TestTenantId";
      }
    };
  }
}
