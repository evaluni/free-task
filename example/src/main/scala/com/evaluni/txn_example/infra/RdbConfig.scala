package com.evaluni.txn_example.infra

case class RdbConfig(mainStore: JdbcEndpoint)

case class JdbcEndpoint(
  driverClass: String,
  url: String,
  username: String,
  password: String
)
