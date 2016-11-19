package com.evaluni.freetask

import scala.language.higherKinds
import scalaz.Free
import scalaz.Monad

class CovariantFree[F[_], +A] private (private val raw: Free[F, _ <: A]) {
  def get[B >: A]: Free[F, B] = raw.map(e => e: B)
}

object CovariantFree {

  implicit def monad[F[_]]: Monad[({ type f[a] = CovariantFree[F, a] })#f] = new Monad[({type f[a] = CovariantFree[F, a]})#f] {

    override def bind[A, B](fa: CovariantFree[F, A])(f: A => CovariantFree[F, B]): CovariantFree[F, B] =
      new CovariantFree(
        fa.get[A].flatMap { a => f(a).get[B] }
      )

    override def point[A](a: => A): CovariantFree[F, A] =
      new CovariantFree(Free.freeMonad[F].point(a))
  }

  def apply[F[_], A](fa: F[A]): CovariantFree[F, A] = new CovariantFree(Free.liftF(fa))
}
