package com.evaluni.txn_example.infra

import com.evaluni.txn_example.domain.store.EntityOp
import scalaz.Coyoneda
import scalaz.Free
import scalaz.Free._
import scalaz._
import scalikejdbc.free.Query

trait EntityIOHandler {

  import Rdb.RdbIO

  def handle[A](op: EntityOp[A]): Option[RdbIO[A]]

  final def convert[A](next: FreeC[EntityOp, A]): RdbIO[A] =
    Free.runFC[EntityOp, RdbIO, A](next)(new (EntityOp ~> RdbIO) {
      override def apply[A](fa: EntityOp[A]): RdbIO[A] = handle(fa) getOrElse {
        throw new IllegalStateException(
          "No handler which can recognize a given action: " + fa
        )
      }
    })(Free.freeMonad[({type f[x] = Coyoneda[Query, x]})#f])
}

object EntityIOHandler {
  def apply(handlers: EntityIOHandler *): EntityIOHandler = new EntityIOHandlerSet(handlers)
}

private[infra] final class EntityIOHandlerSet(handlers: Seq[EntityIOHandler]) extends EntityIOHandler {

  import Rdb.RdbIO

  def handle[A](msg: EntityOp[A]): Option[RdbIO[A]] =
    handlers.toStream.map(_.handle(msg)).collectFirst { case Some(e) => e }
}
