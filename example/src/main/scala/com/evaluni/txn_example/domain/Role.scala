package com.evaluni.txn_example.domain

case class Role(id: RoleId, userId: UserId, isAdmin: Boolean)

case class RoleId(raw: Long)
