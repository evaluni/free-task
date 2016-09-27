package com.evaluni.txn_example.domain

import com.evaluni.freetask.TxnT
import com.evaluni.freetask.TxnTFunctions
import scalaz.Coyoneda
import scalaz.Free
import scalaz.Free.FreeC

package object store {

  type EntityIO[A] = FreeC[EntityOp, A]

  implicit val entityIOMonad = Free.freeMonad[({type f[x] = Coyoneda[EntityOp, x]})#f]

  type Txn[-R, A] = TxnT[EntityIO, R, A]

  def Txn = TxnTFunctions

}
