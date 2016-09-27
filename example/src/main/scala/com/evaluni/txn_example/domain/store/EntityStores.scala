package com.evaluni.txn_example.domain.store

import scala.language.higherKinds

trait EntityStores[M[_], Access[_]] {

  implicit def readonlyMainStore: Access[MainStore.R]
  implicit def writableMainStore: Access[MainStore.W]

  def invoke[R, A](txn: Txn[R, A])(implicit R: Access[R]): M[A]

}
