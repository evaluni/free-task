package com.evaluni.txn_example.infra.config

import scalikejdbc.ConnectionPool
import scalikejdbc.ConnectionPoolContext
import scalikejdbc.ConnectionPoolSettings
import scalikejdbc.MultipleConnectionPoolContext

case class SampleDatabaseConfig(
  databaseName: String,
  driverClassName: String,
  masterJdbcUrl: String,
  masterJdbcUser: String,
  masterJdbcPassword: String,
  slaveJdbcUrl: String,
  slaveJdbcUser: String,
  slaveJdbcPassword: String,
  connectionPoolConfig: Option[ConnectionPoolSettings] = None
) {
  def connectionPoolContext: ConnectionPoolContext = MultipleConnectionPoolContext((databaseName, initialize()))

  def initialize(): ConnectionPool = {
    // initialize JDBC driver & connection pool
    Class.forName(driverClassName)
    connectionPoolConfig match {
      case Some(js) =>
        ConnectionPool.add(databaseName, masterJdbcUrl, masterJdbcUser, masterJdbcPassword, js)
      case None =>
        ConnectionPool.add(databaseName, masterJdbcUrl, masterJdbcUser, masterJdbcPassword)
    }
    ConnectionPool.get(databaseName)
  }
}

trait UsesDatabaseConnectionPoolContext {
  val connectionPoolContext: ConnectionPoolContext
}

trait MixInDatabaseConnectionPoolContext extends UsesDatabaseConnectionPoolContext with UsesSampleDatabaseConfig {
  override lazy val connectionPoolContext: ConnectionPoolContext = sampleDatabaseConfig.connectionPoolContext
}

trait UsesSampleDatabaseConfig {
  val sampleDatabaseConfig: SampleDatabaseConfig
}
