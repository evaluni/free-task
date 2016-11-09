package com.evaluni.txn_example.infra

import scalikejdbc.ConnectionPool
import scalikejdbc.NamedDB

object MockStores {
  Class.forName("org.h2.Driver")

  ConnectionPool.add(SampleDatabase.mainStore
    , "jdbc:h2:mem:example"
    , "user"
    , "pass"
  )

  lazy val mainStoreDB: NamedDB = NamedDB(SampleDatabase.mainStore)

}
