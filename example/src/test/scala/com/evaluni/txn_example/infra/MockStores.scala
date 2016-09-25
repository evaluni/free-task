package com.evaluni.txn_example.infra

import scalikejdbc.ConnectionPool
import scalikejdbc.NamedDB

object MockStores {

  val config = RdbConfig(
    mainStore = JdbcEndpoint(
      driverClass = "org.h2.Driver",
      "jdbc:h2:mem:example",
      "user",
      "pass"
    )
  )

  Class.forName(config.mainStore.driverClass)

  ConnectionPool.add(Rdb.mainStore
    , config.mainStore.url
    , config.mainStore.username
    , config.mainStore.password
  )

  lazy val mainStoreDB: NamedDB = NamedDB(Rdb.mainStore)

}
