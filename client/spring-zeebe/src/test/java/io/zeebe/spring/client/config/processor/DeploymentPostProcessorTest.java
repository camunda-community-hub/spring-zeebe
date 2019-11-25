package io.zeebe.spring.client.config.processor;

import io.zeebe.client.ZeebeClient;
import io.zeebe.client.api.ZeebeFuture;
import io.zeebe.client.api.command.DeployWorkflowCommandStep1;
import io.zeebe.client.api.response.DeploymentEvent;
import io.zeebe.client.api.response.Workflow;
import io.zeebe.spring.client.bean.ClassInfo;
import io.zeebe.spring.client.bean.value.ZeebeDeploymentValue;
import io.zeebe.spring.client.bean.value.factory.ReadZeebeDeploymentValue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeploymentPostProcessorTest {

  @Mock
  private ReadZeebeDeploymentValue reader;

  @Mock
  private ZeebeClient client;

  @Mock
  private DeployWorkflowCommandStep1 deployStep1;

  @Mock
  private DeployWorkflowCommandStep1.DeployWorkflowCommandBuilderStep2 deployStep2;

  @Mock
  private ZeebeFuture<DeploymentEvent> zeebeFuture;

  @Mock
  private DeploymentEvent deploymentEvent;

  private DeploymentPostProcessor deploymentPostProcessor;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    deploymentPostProcessor = new DeploymentPostProcessor(reader);
  }

  @Test
  public void shouldDeploySingleResourceTest() {
    //given
    ClassInfo classInfo = ClassInfo.builder()
      .build();

    ZeebeDeploymentValue zeebeDeploymentValue = ZeebeDeploymentValue.builder()
      .classPathResources(Collections.singletonList("/1.bpmn"))
      .build();

    when(reader.applyOrThrow(classInfo)).thenReturn(zeebeDeploymentValue);

    when(client.newDeployCommand()).thenReturn(deployStep1);

    when(deployStep1.addResourceFromClasspath(anyString())).thenReturn(deployStep2);

    when(deployStep2.send()).thenReturn(zeebeFuture);

    when(zeebeFuture.join()).thenReturn(deploymentEvent);

    when(deploymentEvent.getWorkflows()).thenReturn(Collections.singletonList(getWorkFlow()));

    //when
    deploymentPostProcessor.apply(classInfo).accept(client);

    //then
    verify(deployStep1).addResourceFromClasspath(eq("/1.bpmn"));
    verify(deployStep2).send();
    verify(zeebeFuture).join();
  }

  @Test
  public void shouldDeployMultipleResourcesTest() {
    //given
    ClassInfo classInfo = ClassInfo.builder()
      .build();

    ZeebeDeploymentValue zeebeDeploymentValue = ZeebeDeploymentValue.builder()
      .classPathResources(Arrays.asList("/1.bpmn", "/2.bpmn"))
      .build();

    when(reader.applyOrThrow(classInfo)).thenReturn(zeebeDeploymentValue);

    when(client.newDeployCommand()).thenReturn(deployStep1);

    when(deployStep1.addResourceFromClasspath(anyString())).thenReturn(deployStep2);

    when(deployStep2.send()).thenReturn(zeebeFuture);

    when(zeebeFuture.join()).thenReturn(deploymentEvent);

    when(deploymentEvent.getWorkflows()).thenReturn(Collections.singletonList(getWorkFlow()));

    //when
    deploymentPostProcessor.apply(classInfo).accept(client);

    //then
    verify(deployStep1).addResourceFromClasspath(eq("/1.bpmn"));
    verify(deployStep1).addResourceFromClasspath(eq("/2.bpmn"));
    verify(deployStep2).send();
    verify(zeebeFuture).join();
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionOnNoResourcesToDeploy() {
    //given
    ClassInfo classInfo = ClassInfo.builder()
      .build();

    ZeebeDeploymentValue zeebeDeploymentValue = ZeebeDeploymentValue.builder()
      .classPathResources(Collections.emptyList())
      .build();

    when(reader.applyOrThrow(classInfo)).thenReturn(zeebeDeploymentValue);

    when(client.newDeployCommand()).thenReturn(deployStep1);

    when(deployStep1.addResourceFromClasspath(anyString())).thenReturn(deployStep2);

    //when
    deploymentPostProcessor.apply(classInfo).accept(client);
  }

  private Workflow getWorkFlow() {
    return new Workflow() {
      @Override
      public String getBpmnProcessId() {
        return "12345-abcd";
      }

      @Override
      public int getVersion() {
        return 1;
      }

      @Override
      public long getWorkflowKey() {
        return 101010;
      }

      @Override
      public String getResourceName() {
        return "TestProcess";
      }
    };
  }
}
