package com.evaluni.txn_example.domain

import com.evaluni.freetask.TxnT
import com.evaluni.freetask.TxnTFunctions
import scala.language.reflectiveCalls
import scalaz.Coyoneda
import scalaz.Free

package object store {

  type EntityIO[A] = Free[EntityOp, A]

  implicit val entityIOMonad = Free.freeMonad[({type f[x] = Coyoneda[EntityOp, x]})#f]

  type Txn[-R, A] = TxnT[EntityIO, R, A]

  def Txn = TxnTFunctions

}
