package com.evaluni.freetask

import scala.language.higherKinds
import scalaz.Monad
import scalaz.syntax.all._

class CovariantWrapper[M[_], +A](private val raw: M[_ <: A]) {
  def cast[B >: A](implicit M: Monad[M]): M[B] = raw.map(e => e: B)
}

object CovariantWrapper {

  implicit def monad[F[_]]
  (implicit F: Monad[F]): Monad[({ type f[a] = CovariantWrapper[F, a] })#f] = new Monad[({type f[a] = CovariantWrapper[F, a]})#f] {

    override def bind[A, B](fa: CovariantWrapper[F, A])(f: A => CovariantWrapper[F, B]): CovariantWrapper[F, B] =
      new CovariantWrapper(
        fa.cast[A].flatMap { a => f(a).cast[B] }
      )

    override def point[A](a: => A): CovariantWrapper[F, A] =
      new CovariantWrapper(a.point)
  }
}
