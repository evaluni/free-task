package com.evaluni.txn_example.domain

import com.evaluni.txn_example.domain.store.EntityOp
import com.evaluni.txn_example.domain.store.MainStore
import com.evaluni.txn_example.domain.store.Txn

object UserRepository {

  case class Find(id: UserId) extends EntityOp[Option[User]]
  def find(id: UserId): Txn[MainStore.R, Option[User]] = Find(id)

  case class FindByName(name: String) extends EntityOp[Option[User]]
  def findByName(name: String): Txn[MainStore.R, Option[User]] = FindByName(name)

  case class Create(name: String, age: Int) extends EntityOp[UserId]
  def create(name: String, age: Int): Txn[MainStore.W, UserId] = Create(name, age)

}
