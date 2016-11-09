package com.evaluni.txn_example.infra

import scala.concurrent.ExecutionContext
import scalaz.Free
import scalikejdbc.DBSession
import scalikejdbc.NamedAutoSession
import scalikejdbc.ReadOnlyNamedAutoSession
import scalikejdbc.free.Interpreter
import scalikejdbc.free.Query

object SampleDatabase {

  type IO[A] = Free[Query, A]

  val mainStore: String = "mainStore"

  def run[T](a: IO[T], session: DBSession): T =
    a.foldMap(Interpreter.transaction).run(session)

  def run[T](a: IO[T], access: Access[_]): T =
    run(a, newSession(access))

  case class Access[R](name: String, ex: ExecutionContext, readOnly: Boolean)

  private[this] def newSession(access: Access[_]): DBSession =
    (if (access.readOnly) ReadOnlyNamedAutoSession else NamedAutoSession) (access.name)

}
