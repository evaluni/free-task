package com.evaluni.txn_example.domain.store

import scala.language.implicitConversions
import scalaz.Free

trait EntityOp[A]

object EntityOp {

  implicit final def send[A](a: EntityOp[A]): Txn[Any, A] = Txn.liftMT[EntityIO, A](Free.liftFC(a))

}
