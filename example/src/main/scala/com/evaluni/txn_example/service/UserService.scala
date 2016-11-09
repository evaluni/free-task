package com.evaluni.txn_example.service

import com.evaluni.freetask.TxnT
import com.evaluni.txn_example.domain.RoleRepository
import com.evaluni.txn_example.domain.UserId
import com.evaluni.txn_example.domain.UserRepository
import com.evaluni.txn_example.domain.store._
import com.evaluni.txn_example.infra.UsesSampleDatabaseEntityStore
import com.evaluni.txn_example.infra.config.UsesDatabaseExecutionContext
import scala.concurrent.Future

trait UserService extends UsesSampleDatabaseEntityStore with UsesDatabaseExecutionContext {

  import databaseEntityStore._

  def getUser(userId: UserId) = invoke(UserRepository.find(userId))

  def createUser(
    name: String,
    age: Int,
    isAdmin: Boolean
  ): Future[Either[CreateUserError, UserId]] = invoke {
    UserRepository.findByName(name).flatMap {
      case Some(_) =>
        Txn(Left(CreateUserError.DuplicatedName))
      case None =>
        UserRepository.create(name, age).flatMap { userId =>
          RoleRepository.create(userId, isAdmin).map(_ => Right(userId))
        }
    }
  }
}

abstract sealed class CreateUserError(val value: String)
object CreateUserError {
  case object DuplicatedName extends CreateUserError("DuplicatedName")
  case class Unknown(override val value: String) extends CreateUserError(value)
  val values: Seq[CreateUserError] = Seq(DuplicatedName)
  def apply(c: String): CreateUserError = values.find(_.value == c).getOrElse(Unknown(c))
}
