package com.evaluni.txn_example.domain

import com.evaluni.txn_example.infra.EntityIOHandler
import com.evaluni.txn_example.infra.MockStores
import com.evaluni.txn_example.infra.SampleDatabaseEntityStore
import com.evaluni.txn_example.util.MixInBlockingDatabaseExecutionContext
import org.scalatest.FlatSpec
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class UserRepositorySpec extends FlatSpec {

  val store = new SampleDatabaseEntityStore with MixInBlockingDatabaseExecutionContext {
    override val entityIOHandler: EntityIOHandler = UserHandler
  }

  import store._

  MockStores.mainStoreDB.autoCommit {
    implicit session =>
      UserRepositorySpec.ddl.execute.apply()
  }

  "UserRepository" should "work well" in {

    val w = for {
      id <- UserRepository.create("findall", age = 99)
      user <- UserRepository.find(id)
    } yield user.map(e => e.name + ":" + e.age) getOrElse ""

    assert(Await.result(store.invoke(w), Duration(300, "seconds")) == "findall:99")
  }

}

object UserRepositorySpec {

  import scalikejdbc._

  val ddl =
    sql"""
CREATE TABLE user (
  id         serial       NOT NULL PRIMARY KEY ,
  name       varchar(32)  NOT NULL,
  age        int          NOT NULL,
  created_at timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP
)
"""
}
