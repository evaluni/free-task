package com.evaluni.txn_example.infra.config

import java.util.concurrent.ForkJoinPool
import scala.concurrent.ExecutionContext

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

trait MixInDefaultExecutionContext {
  implicit val executionContext: ExecutionContext = ExecutionContextCreator.create(5)
}

trait MixInDatabaseExecutionContext {
  val masterDatabaseExecutionContext: ExecutionContext = ExecutionContextCreator.create(5)
  val slaveDatabaseExecutionContext: ExecutionContext = ExecutionContextCreator.create(5)
}
