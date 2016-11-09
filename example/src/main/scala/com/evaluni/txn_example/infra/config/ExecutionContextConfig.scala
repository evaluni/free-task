package com.evaluni.txn_example.infra.config

import java.util.concurrent.ForkJoinPool
import scala.concurrent.ExecutionContext

trait UsesExecutionContextConfig {
  val executionContextConfig: ExecutionContextConfig
}

case class ExecutionContextConfig(
  defaultParallelism: Int,
  masterDatabaseParallelism: Int,
  slaveDatabaseParallelism: Int,
  masterLDAPParallelism: Int
)

object ExecutionContextCreator {
  private[config] def create(parallelism: Int) = ExecutionContext.fromExecutorService(new ForkJoinPool(parallelism))
}

trait UsesDefaultExecutionContext {
  implicit val executionContext: ExecutionContext
}

trait UsesDatabaseExecutionContext {
  val masterDatabaseExecutionContext: ExecutionContext
  val slaveDatabaseExecutionContext: ExecutionContext
}
