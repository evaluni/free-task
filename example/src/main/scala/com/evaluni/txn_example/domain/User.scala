package com.evaluni.txn_example.domain

case class User(id: UserId, name: String, age: Int)

case class UserId(raw: Long)
