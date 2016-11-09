package com.evaluni.txn_example.infra.dao

import com.evaluni.txn_example.domain.Role
import com.evaluni.txn_example.domain.RoleId
import com.evaluni.txn_example.domain.UserId
import com.evaluni.txn_example.infra.SampleDatabase.IO
import scalikejdbc.free.Query
import scalikejdbc.free.ScalikeJDBC

object RoleTable {

  import scalikejdbc._

  def selectById(id: RoleId)(implicit s: ScalikeJDBC[Query]): IO[Option[Role]] =
    s.first(sql"select * from role where id = ${id.raw}".map(rs =>
      Role(id, UserId(rs.int("user_id")), rs.boolean("is_admin"))
    ))

  def insert(userId: UserId, isAdmin: Boolean)(implicit s: ScalikeJDBC[Query]): IO[RoleId] =
    s.generateKey(sql"insert into role (user_id, is_admin) values (${userId.raw}, ${if (isAdmin) 1 else 0})").map(RoleId)

}
