package com.evaluni.txn_example.infra.dao

import com.evaluni.txn_example.domain.User
import com.evaluni.txn_example.domain.UserId
import com.evaluni.txn_example.infra.SampleDatabase.IO
import scalikejdbc.free.Query
import scalikejdbc.free.ScalikeJDBC

object UserTable {

  import scalikejdbc._

  def selectById(id: UserId)(implicit s: ScalikeJDBC[Query]): IO[Option[User]] =
    s.first(sql"select * from user where id = ${id.raw}".map(rs =>
      User(id, rs.string("name"), rs.int("age"))
    ))

  def selectByName(name: String)(implicit s: ScalikeJDBC[Query]): IO[Option[User]] =
    s.first(sql"select * from user where name = $name".map(rs =>
      User(UserId(rs.long("id")), rs.string("name"), rs.int("age"))
    ))

  def insert(name: String, age: Int)(implicit s: ScalikeJDBC[Query]): IO[UserId] =
    s.generateKey(sql"insert into user (name, age) values ($name, $age)").map(UserId)

}
