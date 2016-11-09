package com.evaluni.txn_example.domain

import com.evaluni.txn_example.domain.UserRepository.Create
import com.evaluni.txn_example.domain.UserRepository.Find
import com.evaluni.txn_example.domain.store.EntityOp
import com.evaluni.txn_example.infra.EntityIOHandler
import com.evaluni.txn_example.infra.RDB._
import com.evaluni.txn_example.infra.dao.UserTable

object UserHandler extends EntityIOHandler {

  override def handle[A](msg: EntityOp[A]): Option[IO[A]] = Some(msg).collect {
    case Find(id) =>
      UserTable.selectById(id)

    case Create(name, age) =>
      UserTable.insert(name, age)
  }

}
