package io.camunda.zeebe.spring.client.config.processor;

import static org.springframework.util.ReflectionUtils.doWithMethods;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.worker.JobWorkerBuilderStep1.JobWorkerBuilderStep3;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import io.camunda.zeebe.spring.client.bean.ClassInfo;
import io.camunda.zeebe.spring.client.bean.MethodInfo;
import io.camunda.zeebe.spring.client.bean.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.bean.value.factory.ReadZeebeWorkerValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * Triggered by {@link SubscriptionBuilderPostProcessor#postProcessAfterInitialization(Object, String)} to add Handler subscriptions for {@link ZeebeWorker}
 * method-annotations.
 */
public class ZeebeWorkerPostProcessor extends BeanInfoPostProcessor {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private final ReadZeebeWorkerValue reader;

  public ZeebeWorkerPostProcessor(ReadZeebeWorkerValue reader) {
    this.reader = reader;
  }

  @Override
  public boolean test(final ClassInfo beanInfo) {
    return beanInfo.hasMethodAnnotation(ZeebeWorker.class);
  }

  @Override
  public Consumer<ZeebeClient> apply(final ClassInfo beanInfo) {
    LOGGER.info("zeebeWorker: {}", beanInfo);

    final List<ZeebeWorkerValue> annotatedMethods = new ArrayList<>();

    doWithMethods(
      beanInfo.getTargetClass(),
      method -> reader.apply(beanInfo.toMethodInfo(method)).ifPresent(annotatedMethods::add),
      ReflectionUtils.USER_DECLARED_METHODS);

    return client ->
      annotatedMethods.forEach(
        m -> {
          MethodInfo methodInfo = m.getBeanInfo();
          final JobWorkerBuilderStep3 builder = client
            .newWorker()
            .jobType(m.getType())
            .handler((jobClient, job) -> {
              List<Object> args = new ArrayList<>();
              Map<String, Object> variables = job.getVariablesAsMap();
              Map<String, Class<?>> fetchVariables = methodInfo.getFetchVariables();
              for (Map.Entry<String, Class<?>> entry : fetchVariables.entrySet()) {
                String key = entry.getKey();
                Class<?> clazz = entry.getValue();

                // set paramter default null
                Object arg = null;

                // set paramter from JobClient
                if (clazz.isInstance(jobClient)) {
                  arg = jobClient;
                }
                // set paramter from ActivatedJob
                if (clazz.isInstance(job)) {
                  arg = job;
                }

                // other paramter from variables
                if (Objects.nonNull(variables)) {
                  Object v = variables.get(key);
                  if (Objects.nonNull(v)) {
                    arg = OBJECT_MAPPER.readValue(OBJECT_MAPPER.writeValueAsString(v), clazz);
                  }
                }
                args.add(arg);
              }
              m.getBeanInfo().invoke(args.toArray());
            });

          // using defaults from config if null, 0 or negative
          if (m.getName() != null && m.getName().length() > 0) {
            builder.name(m.getName());
          }
          if (m.getMaxJobsActive() > 0) {
            builder.maxJobsActive(m.getMaxJobsActive());
          }
          if (m.getTimeout() > 0) {
            builder.timeout(m.getTimeout());
          }
          if (m.getPollInterval() > 0) {
            builder.pollInterval(Duration.ofMillis(m.getPollInterval()));
          }
          if (m.getRequestTimeout() > 0) {
            builder.requestTimeout(Duration.ofSeconds(m.getRequestTimeout()));
          }
          if (m.getFetchVariables().length > 0) {
            builder.fetchVariables(m.getFetchVariables());
          }

          Map<String, Class<?>> variables = methodInfo.getFetchVariables();
          if (Objects.nonNull(variables)) {
            List<String> fetchVariables = variables
              .keySet()
              .stream()
              .filter(variable -> variable != null && variable.length() > 0)
              .collect(Collectors.toList());
            builder.fetchVariables(fetchVariables);
          }

          builder.open();

          LOGGER.info("register job worker: {}", m);
        });
  }
}
