package io.zeebe.spring.api.command

import io.zeebe.client.api.subscription.JobHandler

sealed class Command(open val topic: String)

data class CreateDeployment(
  override val topic: String,
  val classpathResource: String
) : Command(topic)


data class CreateJobWorker(
  override val topic: String,
  val jobType: String,
  val handler: JobHandler
) : Command(topic)

