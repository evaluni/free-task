package com.evaluni.txn_example.util

import java.util.concurrent.Executor
import scala.concurrent.ExecutionContext

class BlockingExecutor extends Executor {
  def execute(command: Runnable): Unit = {
    command.run()
  }
}

trait MixInBlockingDatabaseExecutionContext {
  val masterDatabaseExecutionContext: ExecutionContext = BlockingExecutionContext.global
  val slaveDatabaseExecutionContext: ExecutionContext = BlockingExecutionContext.global
}

trait MixInBlockingExecutionContext {
  implicit lazy val executionContext: ExecutionContext = BlockingExecutionContext.global
}

object BlockingExecutionContext {
  val global: ExecutionContext = ExecutionContext.fromExecutor(new BlockingExecutor)
}
