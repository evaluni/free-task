package com.evaluni.txn_example.infra

import com.evaluni.txn_example.domain.RoleHandler
import com.evaluni.txn_example.domain.UserHandler
import com.evaluni.txn_example.domain.store.EntityOp
import scalaz.Free
import scalaz.~>

trait MixInDefaultEntityIOHandler {
  val entityIOHandler: EntityIOHandler = EntityIOHandler(
    UserHandler,
    RoleHandler
  )
}

trait UsesEntityIOHandler {
  val entityIOHandler: EntityIOHandler
}

trait EntityIOHandler {

  import SampleDatabase.IO

  def handle[A](op: EntityOp[A]): Option[IO[A]]

  final def convert[A](next: Free[EntityOp, A]): IO[A] =
    next.foldMap(new (EntityOp ~> IO) {
      override def apply[T](fa: EntityOp[T]): IO[T] = handle(fa) getOrElse {
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

  import SampleDatabase.IO

  def handle[A](msg: EntityOp[A]): Option[IO[A]] =
    handlers.toStream.map(_.handle(msg)).collectFirst { case Some(e) => e }
}
