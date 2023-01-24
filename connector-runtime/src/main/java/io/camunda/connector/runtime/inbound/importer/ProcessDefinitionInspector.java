package io.camunda.connector.runtime.inbound.importer;

import io.camunda.connector.api.inbound.InboundConnectorProperties;
import io.camunda.connector.api.inbound.ProcessCorrelationPoint;
import io.camunda.connector.runtime.inbound.correlation.MessageCorrelationPoint;
import io.camunda.connector.runtime.inbound.correlation.StartEventCorrelationPoint;
import io.camunda.operate.dto.ProcessDefinition;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import io.camunda.zeebe.model.bpmn.instance.BaseElement;
import io.camunda.zeebe.model.bpmn.instance.IntermediateCatchEvent;
import io.camunda.zeebe.model.bpmn.instance.Message;
import io.camunda.zeebe.model.bpmn.instance.MessageEventDefinition;
import io.camunda.zeebe.model.bpmn.instance.Process;
import io.camunda.zeebe.model.bpmn.instance.ReceiveTask;
import io.camunda.zeebe.model.bpmn.instance.StartEvent;
import io.camunda.zeebe.model.bpmn.instance.zeebe.ZeebeProperties;
import io.camunda.zeebe.model.bpmn.instance.zeebe.ZeebeSubscription;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Inspects the imported process definitions and extracts
 * Inbound Connector definitions as {@link ProcessCorrelationPoint}.
 */
public class ProcessDefinitionInspector {

  private static final Logger LOG = LoggerFactory.getLogger(ProcessDefinitionInspector.class);

  private final static List<Class<? extends BaseElement>> INBOUND_ELIGIBLE_TYPES = new ArrayList<>();

  static {
    INBOUND_ELIGIBLE_TYPES.add(StartEvent.class);
    INBOUND_ELIGIBLE_TYPES.add(IntermediateCatchEvent.class);
    INBOUND_ELIGIBLE_TYPES.add(ReceiveTask.class);
  }

  private final ProcessDefinition processDefinition;
  private final BpmnModelInstance modelInstance;

  public ProcessDefinitionInspector(ProcessDefinition processDefinition,
    BpmnModelInstance modelInstance) {
    this.processDefinition = processDefinition;
    this.modelInstance = modelInstance;
  }

  public List<InboundConnectorProperties> findInboundConnectors() {
    return modelInstance.getDefinitions()
      .getChildElementsByType(Process.class)
      .stream()
      .flatMap(process -> inspectBpmnProcess(process).stream())
      .collect(Collectors.toList());
  }

  private List<InboundConnectorProperties> inspectBpmnProcess(Process process) {
    List<BaseElement> inboundEligibleElements = INBOUND_ELIGIBLE_TYPES.stream()
      .flatMap(type -> process.getChildElementsByType(type).stream())
      .collect(Collectors.toList());

    List<InboundConnectorProperties> discoveredConnectors = new ArrayList<>();

    for (BaseElement element : inboundEligibleElements) {
      ZeebeProperties zeebeProperties = element.getSingleExtensionElement(ZeebeProperties.class);
      if (zeebeProperties == null) {
        continue;
      }
      Optional<ProcessCorrelationPoint> maybeTarget = handleElement(element);
      if (!maybeTarget.isPresent()) {
        continue;
      }
      ProcessCorrelationPoint target = maybeTarget.get();

      InboundConnectorProperties properties = new InboundConnectorProperties(
        target,
        zeebeProperties.getProperties().stream()
          // Avoid issue with OpenJDK when collecting null values
          // -->
          // https://stackoverflow.com/questions/24630963/nullpointerexception-in-collectors-tomap-with-null-entry-values
          // .collect(Collectors.toMap(ZeebeProperty::getName, ZeebeProperty::getValue)));
          .collect(
            HashMap::new,
            (m, zeebeProperty) -> m.put(zeebeProperty.getName(), zeebeProperty.getValue()),
            HashMap::putAll));

      discoveredConnectors.add(properties);
    }
    return discoveredConnectors;
  }

  private Optional<ProcessCorrelationPoint> handleElement(BaseElement element) {
    if (element instanceof StartEvent) {
      return handleStartEvent((StartEvent) element);
    } else if (element instanceof IntermediateCatchEvent) {
      return handleIntermediateCatchEvent((IntermediateCatchEvent) element);
    } else if (element instanceof ReceiveTask) {
      return handleReceiveTask((ReceiveTask) element);
    }
    LOG.warn("Unsupported Inbound element type: " + element.getClass());
    return Optional.empty();
  }

  private Optional<ProcessCorrelationPoint> handleIntermediateCatchEvent(
    IntermediateCatchEvent catchEvent) {

    MessageEventDefinition msgDef = (MessageEventDefinition) catchEvent.getEventDefinitions()
      .stream()
      .filter(def -> def instanceof MessageEventDefinition)
      .findAny().orElseThrow(() ->
        new IllegalStateException("Sanity check failed: IntermediateCatchEvent " +
          catchEvent + " must contain at least one event definition"));

    ZeebeSubscription subscription = msgDef.getMessage()
      .getSingleExtensionElement(ZeebeSubscription.class);
    if (subscription == null) {
      LOG.warn("Sanity check failed: no ZeebeSubscription matching the event definition");
      return Optional.empty();
    }

    String key = subscription.getCorrelationKey();
    String name = msgDef.getMessage().getName();

    return Optional.of(new MessageCorrelationPoint(
      processDefinition.getKey(),
      processDefinition.getBpmnProcessId(),
      processDefinition.getVersion().intValue(),
      name,
      key));
  }

  private Optional<ProcessCorrelationPoint> handleStartEvent(StartEvent startEvent) {
    return Optional.of(new StartEventCorrelationPoint(
      processDefinition.getKey(),
      processDefinition.getBpmnProcessId(),
      processDefinition.getVersion().intValue()));
  }

  private Optional<ProcessCorrelationPoint> handleReceiveTask(ReceiveTask receiveTask) {
    Message message = receiveTask.getMessage();
    ZeebeSubscription subscription = message.getSingleExtensionElement(ZeebeSubscription.class);

    return Optional.of(new MessageCorrelationPoint(
      processDefinition.getKey(),
      processDefinition.getBpmnProcessId(),
      processDefinition.getVersion().intValue(),
      message.getName(),
      subscription.getCorrelationKey()));
  }
}
