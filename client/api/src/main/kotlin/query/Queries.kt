package io.zeebe.spring.api.query

sealed class Query

object TopologyQuery : Query()

data class WorkflowQuery(val key: String = "*") : Query()
