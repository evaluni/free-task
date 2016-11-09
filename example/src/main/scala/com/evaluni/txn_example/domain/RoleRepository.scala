package com.evaluni.txn_example.domain

import com.evaluni.txn_example.domain.store.EntityOp
import com.evaluni.txn_example.domain.store.MainStore
import com.evaluni.txn_example.domain.store.Txn

object RoleRepository {

  case class Find(id: RoleId) extends EntityOp[Option[Role]]
  def find(id: RoleId): Txn[MainStore.R, Option[Role]] = Find(id)

  case class Create(userId: UserId, isAdmin: Boolean) extends EntityOp[RoleId]
  def create(userId: UserId, isAdmin: Boolean): Txn[MainStore.W, RoleId] = Create(userId, isAdmin)

}
