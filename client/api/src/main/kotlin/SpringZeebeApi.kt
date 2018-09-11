package io.zeebe.spring.api

import io.zeebe.client.ZeebeClient
import io.zeebe.spring.api.command.CreateDeployment
import io.zeebe.spring.api.command.CreateJobWorker
import io.zeebe.spring.api.query.TopologyQuery

fun ZeebeClient.apply(cmd: CreateDeployment) = this.topicClient(cmd.topic)
  .workflowClient()
  .newDeployCommand()
  .addResourceFromClasspath(cmd.classpathResource)
  .send()!!


fun ZeebeClient.apply(cmd: CreateJobWorker) = this.topicClient(cmd.topic)
  .jobClient()
  .newWorker()
  .jobType(cmd.jobType)
  .handler(cmd.handler)
  .open()!!

fun ZeebeClient.apply(query: TopologyQuery) = this.newTopologyRequest().send()!!


