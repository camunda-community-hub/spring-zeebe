package io.camunda.zeebe.spring.client.config.processor;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.ZeebeFuture;
import io.camunda.zeebe.client.api.command.DeployProcessCommandStep1;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.client.api.response.Process;
import io.camunda.zeebe.spring.client.bean.ClassInfo;
import io.camunda.zeebe.spring.client.bean.value.ZeebeDeploymentValue;
import io.camunda.zeebe.spring.client.bean.value.factory.ReadZeebeDeploymentValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeploymentPostProcessorTest {

  @Mock
  private ReadZeebeDeploymentValue reader;

  @Mock
  private ZeebeClient client;

  @Mock
  private DeployProcessCommandStep1 deployStep1;

  @Mock
  private DeployProcessCommandStep1.DeployProcessCommandBuilderStep2 deployStep2;

  @Mock
  private ZeebeFuture<DeploymentEvent> zeebeFuture;

  @Mock
  private DeploymentEvent deploymentEvent;

  private DeploymentPostProcessor deploymentPostProcessor;

  @BeforeEach
  public void init() {
    MockitoAnnotations.initMocks(this);
    deploymentPostProcessor = Mockito.spy(new DeploymentPostProcessor(reader));
  }

  @Test
  public void shouldDeploySingleResourceTest() {
    //given
    ClassInfo classInfo = ClassInfo.builder()
      .build();

    ZeebeDeploymentValue zeebeDeploymentValue = ZeebeDeploymentValue.builder()
      .resources(Collections.singletonList("classpath*:/1.bpmn"))
      .build();

    Resource resource = Mockito.mock(FileSystemResource.class);

    when(resource.getFilename()).thenReturn("1.bpmn");

    when(reader.applyOrThrow(classInfo)).thenReturn(zeebeDeploymentValue);

    when(client.newDeployCommand()).thenReturn(deployStep1);

    when(deploymentPostProcessor.getResources(anyString())).thenReturn(new Resource[]{resource});

    when(deployStep1.addResourceStream(any(), anyString())).thenReturn(deployStep2);

    when(deployStep2.send()).thenReturn(zeebeFuture);

    when(zeebeFuture.join()).thenReturn(deploymentEvent);

    when(deploymentEvent.getProcesses()).thenReturn(Collections.singletonList(getProcess()));

    //when
    deploymentPostProcessor.apply(classInfo).accept(client);

    //then
    verify(deployStep1).addResourceStream(any(), eq("1.bpmn"));
    verify(deployStep2).send();
    verify(zeebeFuture).join();
  }

  @Test
  public void shouldDeployMultipleResourcesTest() {
    //given
    ClassInfo classInfo = ClassInfo.builder()
      .build();

    ZeebeDeploymentValue zeebeDeploymentValue = ZeebeDeploymentValue.builder()
      .resources(Arrays.asList("classpath*:/1.bpmn", "classpath*:/2.bpmn"))
      .build();

    Resource[] resources = {Mockito.mock(FileSystemResource.class), Mockito.mock(FileSystemResource.class)};

    when(resources[0].getFilename()).thenReturn("1.bpmn");
    when(resources[1].getFilename()).thenReturn("2.bpmn");

    when(reader.applyOrThrow(classInfo)).thenReturn(zeebeDeploymentValue);

    when(client.newDeployCommand()).thenReturn(deployStep1);

    when(deploymentPostProcessor.getResources("classpath*:/1.bpmn")).thenReturn(new Resource[]{resources[0]});

    when(deploymentPostProcessor.getResources("classpath*:/2.bpmn")).thenReturn(new Resource[]{resources[1]});

    when(deployStep1.addResourceStream(any(), anyString())).thenReturn(deployStep2);

    when(deployStep2.send()).thenReturn(zeebeFuture);

    when(zeebeFuture.join()).thenReturn(deploymentEvent);

    when(deploymentEvent.getProcesses()).thenReturn(Collections.singletonList(getProcess()));

    //when
    deploymentPostProcessor.apply(classInfo).accept(client);

    //then
    verify(deployStep1).addResourceStream(any(), eq("1.bpmn"));
    verify(deployStep1).addResourceStream(any(), eq("1.bpmn"));

    verify(deployStep2).send();
    verify(zeebeFuture).join();
  }

  @Test
  public void shouldThrowExceptionOnNoResourcesToDeploy() {
    assertThrows(IllegalArgumentException.class, () -> {
      //given
      ClassInfo classInfo = ClassInfo.builder()
        .build();

      ZeebeDeploymentValue zeebeDeploymentValue = ZeebeDeploymentValue.builder()
        .resources(Collections.emptyList())
        .build();

      when(reader.applyOrThrow(classInfo)).thenReturn(zeebeDeploymentValue);

      when(client.newDeployCommand()).thenReturn(deployStep1);

      when(deployStep1.addResourceStream(any(), anyString())).thenReturn(deployStep2);

      //when
      deploymentPostProcessor.apply(classInfo).accept(client);
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
    };
  }
}
