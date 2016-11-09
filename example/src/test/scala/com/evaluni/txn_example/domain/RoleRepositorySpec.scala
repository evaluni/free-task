package com.evaluni.txn_example.domain

import com.evaluni.txn_example.infra.MockStores
import com.evaluni.txn_example.infra.RDBEntityStores
import org.scalatest.FlatSpec
import scala.util.Success

class RoleRepositorySpec extends FlatSpec {

  val store = new RDBEntityStores(RoleHandler)
  import store._

  MockStores.mainStoreDB.autoCommit { implicit session =>
    RoleRepositorySpec.ddl.execute.apply()
  }

  "RoleRepository" should "work well" in {

    val w1 = for {
      id   <- RoleRepository.create(UserId(1), isAdmin = true)
      role <- RoleRepository.find(id)
    } yield role.map(e => e.userId.raw + ":" + e.isAdmin) getOrElse ""

    assert(store.invoke(w1) == Success("1:true"))

    val w2 = for {
      id   <- RoleRepository.create(UserId(2), isAdmin = false)
      role <- RoleRepository.find(id)
    } yield role.map(e => e.userId.raw + ":" + e.isAdmin) getOrElse ""

    assert(store.invoke(w2) == Success("2:false"))
  }

}

object RoleRepositorySpec {

  import scalikejdbc._

  val ddl = sql"""
CREATE TABLE role (
  id         serial       NOT NULL PRIMARY KEY ,
  user_id    int          NOT NULL,
  is_admin   int          NOT NULL,
  created_at timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP
)
"""
}
