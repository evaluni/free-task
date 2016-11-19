package com.evaluni.txn_example.domain.store

import scala.language.implicitConversions

trait EntityOp[A]

object EntityOp {

  implicit final def send[A](a: EntityOp[A]): Txn[Any, A] = Txn.send(a)

}
