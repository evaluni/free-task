package com.evaluni.txn_example.domain

import com.evaluni.freetask.CovariantFree
import com.evaluni.freetask.CovariantTxnT
import com.evaluni.freetask.CovariantTxnTFunctions
import scala.language.reflectiveCalls
import scalaz.Coyoneda

package object store {

  type EntityIO[+A] = CovariantFree[EntityOp, A]

  implicit val entityIOMonad = CovariantFree.monad[({type f[x] = Coyoneda[EntityOp, x]})#f]

  type Txn[-R, +A] = CovariantTxnT[EntityIO, R, A]

  object Txn extends CovariantTxnTFunctions

}
