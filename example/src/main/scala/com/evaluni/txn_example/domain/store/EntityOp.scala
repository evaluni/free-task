package com.evaluni.txn_example.domain.store

import com.evaluni.freetask.CovariantFree
import scala.language.implicitConversions

trait EntityOp[A]

object EntityOp {

  implicit final def send[A](a: EntityOp[A]): Txn[Any, A] = Txn.liftMT[EntityIO, A](CovariantFree(a))

}
