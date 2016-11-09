package com.evaluni.txn_example.domain

import com.evaluni.txn_example.domain.RoleRepository.Create
import com.evaluni.txn_example.domain.RoleRepository.Find
import com.evaluni.txn_example.domain.store.EntityOp
import com.evaluni.txn_example.infra.EntityIOHandler
import com.evaluni.txn_example.infra.RDB._
import com.evaluni.txn_example.infra.dao.RoleTable

object RoleHandler extends EntityIOHandler {

  override def handle[A](msg: EntityOp[A]): Option[IO[A]] = Some(msg).collect {
    case Find(id) =>
      RoleTable.selectById(id)

    case Create(name, age) =>
      RoleTable.insert(name, age)
  }

}
