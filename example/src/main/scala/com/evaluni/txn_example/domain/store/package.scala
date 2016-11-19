package com.evaluni.txn_example.domain

import com.evaluni.freetask.CovariantTxnT
import com.evaluni.freetask.CovariantTxnTFunctions
import com.evaluni.freetask.CovariantWrapper
import scala.language.reflectiveCalls
import scalaz.Coyoneda
import scalaz.Free

package object store {

  type InvariantEntityIO[A] = Free[EntityOp, A]

  implicit val invariantEntityIOMonad = Free.freeMonad[({type f[x] = Coyoneda[EntityOp, x]})#f]

  type EntityIO[+A] = CovariantWrapper[InvariantEntityIO, A]

  type Txn[-R, +A] = CovariantTxnT[EntityIO, R, A]

  object Txn extends CovariantTxnTFunctions {

    def apply[A](a: A): Txn[Any, A] = super.apply(a)

    def send[A](fa: EntityOp[A]): Txn[Any, A] =
      liftMT[EntityIO, A](
        new CovariantWrapper[InvariantEntityIO, A](Free.liftF(fa))
      )
  }
}
