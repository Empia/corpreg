package models.daos

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import models.daos.PasswordInfoDAO._
import play.api.libs.concurrent.Execution.Implicits._

import scala.collection.mutable
import scala.concurrent.Future

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
 * The DAO to store the password information.
 */
class PasswordInfoDAO @Inject()(dbConfigProvider: DatabaseConfigProvider) extends DelegableAuthInfoDAO[PasswordInfo] {

  private val users = TableQuery[Users]
  private val dbRoles = TableQuery[Roles]
  private val allQuery = users.sortBy(u => (u.lastName.asc, u.firstName.asc)).map(_.preview)
  private val pwInfos = TableQuery[PasswordInfos]
  private val loginInfos = TableQuery[LoginInfos]
  private val userLoginInfos = TableQuery[UserLoginInfos]

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig.driver.api._

  val db = dbConfig.db
  /**
   * Finds the auth info which is linked with the specified login info.
   *
   * @param loginInfo The linked login info.
   * @return The retrieved auth info or None if no auth info could be retrieved for the given login info.
   */
  protected def passwordInfoQuery(loginInfo: LoginInfo) = for {
    dbLoginInfo <- loginInfoQuery(loginInfo)
    dbPasswordInfo <- pwInfos if dbPasswordInfo.loginInfoId === dbLoginInfo.id
  } yield dbPasswordInfo

  def loginInfoQuery(loginInfo: LoginInfo) = 
    loginInfos.filter(dbLoginInfo => dbLoginInfo.providerID === loginInfo.providerID && dbLoginInfo.providerKey === loginInfo.providerKey)


  protected def passwordInfoSubQuery(loginInfo: LoginInfo) =
    pwInfos.filter(_.loginInfoId in loginInfoQuery(loginInfo).map(_.id))

  protected def addAction(loginInfo: LoginInfo, authInfo: PasswordInfo) =
    loginInfoQuery(loginInfo).result.head.flatMap { dbLoginInfo =>
      pwInfos +=
        DBPasswordInfo(authInfo.hasher, authInfo.password, authInfo.salt, dbLoginInfo.id.get)
    }.transactionally
    
  protected def updateAction(loginInfo: LoginInfo, authInfo: PasswordInfo) =
    passwordInfoSubQuery(loginInfo).
      map(dbPasswordInfo => (dbPasswordInfo.hasher, dbPasswordInfo.password, dbPasswordInfo.salt)).
      update((authInfo.hasher, authInfo.password, authInfo.salt))






  def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = {
//    findBy(_.id === userID.get)
//    Future.successful(data.get(loginInfo))
   db.run(passwordInfoQuery(loginInfo).result.headOption).map { dbPasswordInfoOption =>
      dbPasswordInfoOption.map(dbPasswordInfo => 
        PasswordInfo(dbPasswordInfo.hasher, dbPasswordInfo.password, dbPasswordInfo.salt))
    }

  }

  /**
   * Adds new auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be added.
   * @param authInfo The auth info to add.
   * @return The added auth info.
   */
  def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    //data += (loginInfo -> authInfo)
    //Future.successful(authInfo)
   db.run(addAction(loginInfo, authInfo)).map(_ => authInfo)

  }

  /**
   * Updates the auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be updated.
   * @param authInfo The auth info to update.
   * @return The updated auth info.
   */
  def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    //data += (loginInfo -> authInfo)
    //Future.successful(authInfo)
   db.run(updateAction(loginInfo, authInfo)).map(_ => authInfo)
  }

  /**
   * Saves the auth info for the given login info.
   *
   * This method either adds the auth info if it doesn't exists or it updates the auth info
   * if it already exists.
   *
   * @param loginInfo The login info for which the auth info should be saved.
   * @param authInfo The auth info to save.
   * @return The saved auth info.
   */
  def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    //find(loginInfo).flatMap {
    //  case Some(_) => update(loginInfo, authInfo)
    //  case None => add(loginInfo, authInfo)
    //}
    val query = loginInfoQuery(loginInfo).joinLeft(pwInfos).on(_.id === _.loginInfoId)
    val action = query.result.head.flatMap {
      case (dbLoginInfo, Some(dbPasswordInfo)) => updateAction(loginInfo, authInfo)
      case (dbLoginInfo, None) => addAction(loginInfo, authInfo)
    }
    db.run(action).map(_ => authInfo)
  }
  /**
   * Removes the auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be removed.
   * @return A future to wait for the process to be completed.
   */
  def remove(loginInfo: LoginInfo): Future[Unit] = {
    //data -= loginInfo
    //Future.successful(())
    db.run(passwordInfoSubQuery(loginInfo).delete).map(_ => ())
  }
}

/**
 * The companion object.
 */
object PasswordInfoDAO {

  /**
   * The data store for the password info.
   */
  var data: mutable.HashMap[LoginInfo, PasswordInfo] = mutable.HashMap()
}
