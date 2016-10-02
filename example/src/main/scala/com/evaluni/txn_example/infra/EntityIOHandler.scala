package com.evaluni.txn_example.infra

import com.evaluni.txn_example.domain.store.EntityOp
import scalaz.Free
import scalaz.~>

trait EntityIOHandler {

  import Rdb.RdbIO

  def handle[A](op: EntityOp[A]): Option[RdbIO[A]]

  final def convert[A](next: Free[EntityOp, A]): RdbIO[A] =
    next.foldMap(new (EntityOp ~> RdbIO) {
      override def apply[T](fa: EntityOp[T]): RdbIO[T] = handle(fa) getOrElse {
        throw new IllegalStateException(
          "No handler which can recognize a given action: " + fa
        )
      }
    })
}

object EntityIOHandler {
  def apply(handlers: EntityIOHandler *): EntityIOHandler = new EntityIOHandlerSet(handlers)
}

private[infra] final class EntityIOHandlerSet(handlers: Seq[EntityIOHandler]) extends EntityIOHandler {

  import Rdb.RdbIO

  def handle[A](msg: EntityOp[A]): Option[RdbIO[A]] =
    handlers.toStream.map(_.handle(msg)).collectFirst { case Some(e) => e }
}
