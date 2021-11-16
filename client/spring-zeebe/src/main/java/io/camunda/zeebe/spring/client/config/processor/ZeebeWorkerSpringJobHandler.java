package io.camunda.zeebe.spring.client.config.processor;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.spring.client.annotation.ZeebeVariable;
import io.camunda.zeebe.spring.client.bean.ParameterInfo;
import io.camunda.zeebe.spring.client.bean.value.ZeebeWorkerValue;

import java.util.ArrayList;
import java.util.List;

public class ZeebeWorkerSpringJobHandler implements JobHandler {

  private ZeebeWorkerValue workerValue;

  public ZeebeWorkerSpringJobHandler(ZeebeWorkerValue workerValue) {
    this.workerValue = workerValue;
  }

  @Override
  public void handle(JobClient jobClient, ActivatedJob job) throws Exception {
    List<ParameterInfo> parameters = workerValue.getBeanInfo().getParameters();
    List<Object> args = new ArrayList<>();

    for (ParameterInfo param: parameters) {
      Object arg = null; // parameter default null
      Class<?> clazz = param.getParameterInfo().getType();

      if (JobClient.class.isAssignableFrom(clazz)) {
        arg = jobClient;
      } else if (ActivatedJob.class.isAssignableFrom(clazz)) {
        arg = job;
      } else if (param.getParameterInfo().isAnnotationPresent(ZeebeVariable.class)) {
        arg = clazz.cast( job.getVariablesAsMap().get(param.getParameterName()) );
      }
      args.add(arg);
    }

    workerValue.getBeanInfo().invoke(args.toArray());

    if (workerValue.isAutoComplete()) {
      // TODO
    }
  }
}
