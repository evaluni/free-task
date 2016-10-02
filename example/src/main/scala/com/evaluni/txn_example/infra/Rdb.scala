package com.evaluni.txn_example.infra

import scalaz.Free
import scalikejdbc.DBSession
import scalikejdbc.NamedAutoSession
import scalikejdbc.ReadOnlyNamedAutoSession
import scalikejdbc.free.Interpreter
import scalikejdbc.free.Query

object Rdb {

  type RdbIO[A] = Free[Query, A]

  val mainStore: String = "mainStore"

  def run[T](a: RdbIO[T], session: DBSession): T =
    a.foldMap(Interpreter.transaction).run(session)

  def run[T](a: RdbIO[T], access: Access[_] = Access(mainStore, readonly = false)): T =
    run(a, newSession(access))

  case class Access[R](name: String, readonly: Boolean)

  private[this] def newSession(access: Access[_]): DBSession =
    (if (access.readonly) ReadOnlyNamedAutoSession else NamedAutoSession) (access.name)

}
