package com.evaluni.txn_example.service

import com.evaluni.txn_example.domain.UserRepositorySpec
import com.evaluni.txn_example.domain.store.EntityStore
import com.evaluni.txn_example.infra.MixInDefaultEntityIOHandler
import com.evaluni.txn_example.infra.MockStores
import com.evaluni.txn_example.infra.SampleDatabaseEntityStore
import com.evaluni.txn_example.util.MixInBlockingDatabaseExecutionContext
import com.evaluni.txn_example.util.MixInBlockingExecutionContext
import org.scalatest.FlatSpec
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.Duration

class UserServiceSpec extends FlatSpec with MixInBlockingExecutionContext {

  val sut = new UserService with MixInBlockingDatabaseExecutionContext {
    override lazy val databaseEntityStore: EntityStore[Future] = new SampleDatabaseEntityStore with MixInBlockingDatabaseExecutionContext with MixInDefaultEntityIOHandler
  }

  MockStores.mainStoreDB.autoCommit {
    implicit session =>
      UserRepositorySpec.ddl.execute.apply()
  }

  "UserService#createUser, getUser" should "work well" in {
    Await.result(sut.createUser(name = "hoge", age = 2525, isAdmin = true), Duration(300, "seconds")) match {
      case Left(e) =>
        fail("createUser is failed")
      case Right(userId) =>
        assert(Await.result(sut.getUser(userId), Duration(300, "seconds")).map(e => e.name + ":" + e.age).getOrElse("") == "hoge:2525")
    }
  }

}
