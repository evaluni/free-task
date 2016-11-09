package com.evaluni.txn_example.infra

import com.evaluni.txn_example.domain.store.EntityStore
import com.evaluni.txn_example.domain.store.MainStore
import com.evaluni.txn_example.domain.store.Txn
import com.evaluni.txn_example.infra.config.MixInDatabaseExecutionContext
import com.evaluni.txn_example.infra.config.UsesDatabaseExecutionContext
import scala.concurrent.Future

trait UsesSampleDatabaseEntityStore {
  val databaseEntityStore: EntityStore[Future]
}

trait MixInSampleDatabaseEntityStore extends UsesSampleDatabaseEntityStore {
  override lazy val databaseEntityStore: EntityStore[Future] = new SampleDatabaseEntityStore with MixInDatabaseExecutionContext with MixInDefaultEntityIOHandler
}

trait SampleDatabaseEntityStore extends EntityStore[Future] with UsesDatabaseExecutionContext with UsesEntityIOHandler {

  override type Access[a] = SampleDatabase.Access[a]

  import SampleDatabase._
  override implicit def readonlyMainStore: Access[MainStore.R] = Access(mainStore, slaveDatabaseExecutionContext, readOnly = true)
  override implicit def writableMainStore: Access[MainStore.W] = Access(mainStore, masterDatabaseExecutionContext, readOnly = false)

  override def invoke[R, A](txn: Txn[R, A])(implicit R: Access[R]): Future[A] =
    Future(SampleDatabase.run(entityIOHandler.convert(txn.raw), R))(R.ex)
}
