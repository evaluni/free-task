package com.evaluni.txn_example.infra

import com.evaluni.txn_example.domain.store.EntityStores
import com.evaluni.txn_example.domain.store.MainStore
import com.evaluni.txn_example.domain.store.Txn
import scala.util.Try

class RDBEntityStores(handler: EntityIOHandler) extends EntityStores[Try] {

  override type Access[a] = RDB.Access[a]

  import RDB._

  override implicit def readonlyMainStore: Access[MainStore.R] = Access(mainStore, readonly =  true)
  override implicit def writableMainStore: Access[MainStore.W] = Access(mainStore, readonly = false)

  override def invoke[R, A](txn: Txn[R, A])(implicit R: Access[R]): Try[A] =
    Try {
      RDB.run(handler.convert(txn.raw), R)
    }
}
