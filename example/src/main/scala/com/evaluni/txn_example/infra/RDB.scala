package com.evaluni.txn_example.infra

import scalaz.Free
import scalikejdbc.DBSession
import scalikejdbc.NamedAutoSession
import scalikejdbc.ReadOnlyNamedAutoSession
import scalikejdbc.free.Interpreter
import scalikejdbc.free.Query

object RDB {

  type IO[A] = Free[Query, A]

  val mainStore: String = "mainStore"

  def run[T](a: IO[T], session: DBSession): T =
    a.foldMap(Interpreter.transaction).run(session)

  def run[T](a: IO[T], access: Access[_] = Access(mainStore, readonly = false)): T =
    run(a, newSession(access))

  case class Access[R](name: String, readonly: Boolean)

  private[this] def newSession(access: Access[_]): DBSession =
    (if (access.readonly) ReadOnlyNamedAutoSession else NamedAutoSession) (access.name)

}
