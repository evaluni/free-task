package com.evaluni.txn

import scala.language.higherKinds
import scalaz.Monad
import scalaz.syntax.all._

class TxnT[M[_], -R, A] private[txn] (val raw: M[A]) {

  import TxnTFunctions._

  def flatMap[R2 <: R, B](f: A => TxnT[M, R2, B])(implicit M: Monad[M]): TxnT[M, R2, B] =
    liftMT(raw flatMap f.andThen(_.raw))

  def map[B](f: A => B)(implicit M: Monad[M]): TxnT[M, R, B] = flatMap(a => apply(f(a)))

}

trait TxnTFunctions { self =>

  def liftMT[M[_], A](ma: M[A]): TxnT[M, Any, A] = new TxnT[M, Any, A](ma)

  def apply[M[_], A](a: => A)(implicit M: Monad[M]): TxnT[M, Any, A] = liftMT(M.point(a))

  implicit def monad[M[_], R]
  (implicit M: Monad[M]): Monad[({ type f[a] = TxnT[M, R, a] })#f] = new Monad[({ type f[a] = TxnT[M, R, a] })#f] {

    override def point[A](a: => A): TxnT[M, R, A] = self[M, A](a)

    override def bind[A, B](fa: TxnT[M, R, A])(f: A => TxnT[M, R, B]): TxnT[M, R, B] = fa flatMap f

  }

}

object TxnTFunctions extends TxnTFunctions
