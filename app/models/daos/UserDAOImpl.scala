package models.daos

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import models.User
import models.daos.UserDAOImpl._
import slick.driver.H2Driver.api._
import slick.driver.JdbcProfile

import scala.collection.mutable
import scala.concurrent.Future
import models.tables._
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import play.api.libs.concurrent.Execution.Implicits._
import javax.inject.Inject

/**
 * Give access to the user object.
 */
class UserDAOImpl @Inject()(dbConfigProvider: DatabaseConfigProvider) extends UserDAO {//with HasDatabaseConfig[JdbcProfile] {

  private val users = TableQuery[Users]
  private val dbRoles = TableQuery[Roles]
  private val allQuery = users.sortBy(u => (u.lastName.asc, u.firstName.asc)).map(_.preview)
  private val pwInfos = TableQuery[PasswordInfos]
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig.driver.api._

  val db = dbConfig.db
  private def findBy(criterion: (Users => slick.lifted.Rep[Boolean])) = db.run(
    for {
      user <- users.filter(criterion).result.head
      dbRoles <- dbRoles.filter(_.userID === user.userID).map(_.role).result
    } yield Some(user.copy(roles = dbRoles.toSet))
  ).recover { case e => None }
  def find(userID: Option[Long] = Some(-1)):scala.concurrent.Future[Option[models.User]] = findBy(_.id === userID.get)
  def find(loginInfo: LoginInfo): Future[Option[User]] =
    findBy(u => u.providerID === loginInfo.providerID && u.providerKey === loginInfo.providerKey)

  private def updateRolesQuery(userId: Option[Long], roles: Set[String]) = for {
    d <- dbRoles.filter(_.userID === userId).delete
    a <- dbRoles ++= roles.map(DBRole(None, userId.get, _))
  } yield roles

  def save(user: User): Future[User] = {
    val existingUserFuture = user.userID match {
      case None => Future.successful(None)
      case Some(id) => find(Some(id))
    }
    existingUserFuture.flatMap {
      case None => db.run(
        for {
          u <- (users returning users.map(_.id)
            into ((user, id) => user.copy(userID = Some(id)))) += user
          r <- updateRolesQuery(u.userID, user.roles)
        } yield u
      )
      case Some(_) => db.run(
        for {
          updatedUser <- users.filter(_.id === user.userID).update(user)
          upatedRoles <- updateRolesQuery(user.userID, user.roles)
        } yield user
      )
    }
  }

  //def findByEmail(email: String) = findBy(_.email === email)




  def count = db.run(users.length.result)
  def all: Future[Seq[UserPreview]] =
    db.run(allQuery.result)

  def all(page: Int, pageSize: Int): Future[Seq[UserPreview]] =
    db.run(allQuery.drop(page * pageSize).take(pageSize).result)

  def delete(id: Option[Long]): Future[Unit] = {
    val delQuery = for {
      roleDelete <- dbRoles.filter(_.userID === id).delete
      pwInfoDelete <- pwInfos.filter(_.userID === id).delete
      userDelete <- users.filter(_.id === id).delete
    } yield (roleDelete, pwInfoDelete, userDelete)
    db.run(delQuery).map(_ => {})
  }




  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  //def find(loginInfo: LoginInfo) = Future.successful(
  //  users.find { case (id, user) => user.loginInfo == loginInfo }.map(_._2)
  //)

  /**
   * Finds a user by its user ID.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  //def find(userID: Option[Long]) = Future.successful(users.get(userID))

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  //def find(loginInfo: LoginInfo): Future[Option[User]] = Future.successful(None)
  //def find(userID: Option[Long]): Future[Option[User]] = Future.successful(None)

  //def save(user: User) = {
  //  UserDAOImpl.users += (user.userID -> user)
  //  Future.successful(user)
  //}
}

/**
 * The companion object.
 */
object UserDAOImpl {

  /**
   * The list of users.
   */
  val users: mutable.HashMap[Option[Long], User] = mutable.HashMap()
}
