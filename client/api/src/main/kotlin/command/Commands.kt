package io.zeebe.spring.api.command

import io.zeebe.client.api.subscription.JobHandler

sealed class Command()

data class CreateDeployment(
  val classpathResource: String
) : Command()


data class CreateJobWorker(
  val jobType: String,
  val handler: JobHandler
) : Command()

