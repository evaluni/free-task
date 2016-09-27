package com.evaluni.txn_example.infra.dao

import com.evaluni.txn_example.domain.User
import com.evaluni.txn_example.domain.UserId
import com.evaluni.txn_example.domain.UserRepository._
import com.evaluni.txn_example.domain.store.EntityOp
import com.evaluni.txn_example.infra.EntityIOHandler
import com.evaluni.txn_example.infra.Rdb.RdbIO
import scalikejdbc.free.Query
import scalikejdbc.free.ScalikeJDBC

object UserDAO extends EntityIOHandler {

  override def handle[A](msg: EntityOp[A]): Option[RdbIO[A]] = Some(msg).collect {
    case Find(id) =>
      UserTable.selectById(id)

    case Create(name, age) =>
      UserTable.insert(name, age)
  }

}

object UserTable {

  import scalikejdbc._

  def selectById(id: UserId)(implicit s: ScalikeJDBC[Query]): RdbIO[Option[User]] =
    s.first(
      sql"select * from user where user_id = ${id.raw}".map(rs =>
        User(id,
          rs.get("name"),
          rs.get("age"))
      )
    )

  def insert(name: String, age: Int)(implicit s: ScalikeJDBC[Query]): RdbIO[UserId] =
    s.generateKey(sql"insert into user (name, age) values ($name, $age)").map(UserId)

}
