package com.evaluni.freetask

import scala.language.higherKinds
import scalaz.Monad
import scalaz.syntax.all._

class CovariantTxnT[M[+_], -R, +A] private[freetask] (val raw: M[A]) {

  import CovariantTxnTFunctions._

  def flatMap[R2 <: R, B](f: A => CovariantTxnT[M, R2, B])(implicit M: Monad[M]): CovariantTxnT[M, R2, B] =
    liftMT(raw flatMap f.andThen(_.raw))

  def map[B](f: A => B)(implicit M: Monad[M]): CovariantTxnT[M, R, B] = flatMap(a => apply(f(a)))

}

trait CovariantTxnTFunctions { self =>

  def liftMT[M[+_], A](ma: M[A]): CovariantTxnT[M, Any, A] = new CovariantTxnT[M, Any, A](ma)

  def apply[M[+_], A](a: => A)(implicit M: Monad[M]): CovariantTxnT[M, Any, A] = liftMT(M.point(a))

  implicit def monad[M[+_], R]
  (implicit M: Monad[M]): Monad[({ type f[a] = CovariantTxnT[M, R, a] })#f] = new Monad[({ type f[a] = CovariantTxnT[M, R, a] })#f] {

    override def point[A](a: => A): CovariantTxnT[M, R, A] = self[M, A](a)

    override def bind[A, B](fa: CovariantTxnT[M, R, A])(f: A => CovariantTxnT[M, R, B]): CovariantTxnT[M, R, B] = fa flatMap f

  }

}

object CovariantTxnTFunctions extends CovariantTxnTFunctions
