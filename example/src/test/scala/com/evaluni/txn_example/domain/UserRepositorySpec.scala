package com.evaluni.txn_example.domain

import com.evaluni.txn_example.infra.MockStores
import com.evaluni.txn_example.infra.RDBEntityStores
import org.scalatest.FlatSpec
import scala.util.Success

class UserRepositorySpec extends FlatSpec {

  val store = new RDBEntityStores(UserHandler)
  import store._

  MockStores.mainStoreDB.autoCommit { implicit session =>
    UserRepositorySpec.ddl.execute.apply()
  }

  "UserRepository" should "work well" in {

    val w = for {
      id   <- UserRepository.create("findall", age=99)
      user <- UserRepository.find(id)
    } yield user.map(e => e.name + ":" + e.age) getOrElse ""

    assert(store.invoke(w) == Success("findall:99"))
  }

}

object UserRepositorySpec {

  import scalikejdbc._

  val ddl = sql"""
CREATE TABLE user (
  id         serial       NOT NULL PRIMARY KEY ,
  name       varchar(32)  NOT NULL,
  age        int          NOT NULL,
  created_at timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP
)
"""
}
