package models.tables


import javax.xml.ws.BindingProvider

import com.mohiva.play.silhouette.api.util.PasswordInfo
import slick.driver.H2Driver.api._
import slick.lifted.TableQuery
import scala.language.implicitConversions
import models._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class UserPreview(id: Option[Long],
                       firstName: Option[String],
                       lastName: Option[String],
                       email: Option[String])
object UserPreview {
  implicit val userPreviewFormat = Json.format[UserPreview]
}
/*
  userID: Option[Int],
  loginInfo: LoginInfo,
  firstName: Option[String],
  lastName: Option[String],
  fullName: Option[String],
  email: Option[String],
  avatarURL: Option[String],
  providerID: String = "",
  providerKey: String = "",  
  */

class Users(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def firstName = column[Option[String]]("FIRSTNAME")
  def lastName = column[Option[String]]("LASTNAME")
  def fullname = column[Option[String]]("FULLNAME")
  def email = column[Option[String]]("EMAIL")
  def avatarURL = column[Option[String]]("avatarURL")
  def providerID = column[String]("PROVIDER_ID")
  def providerKey = column[String]("PROVIDER_KEY")
  def * = (id.?, firstName, lastName, fullname, email, avatarURL, providerID, providerKey) <>(User.withoutRoles, User.toTuple)
  def preview = (id.?, firstName, lastName, email) <>((UserPreview.apply _).tupled,UserPreview.unapply)
}
// Source type: (slick.lifted.Rep[Option[Int]], slick.lifted.Rep[Option[String]], slick.lifted.Rep[Option[String]], slick.lifted.Rep[Option[String]], slick.lifted.Rep[String], slick.lifted.Rep[Option[String]], slick.lifted.Rep[String], slick.lifted.Rep[String]) 
// Unpacked type: (Option[Int], Option[String], Option[String], Option[String], Option[String], Option[String], String, String)

case class DBRole(id: Option[Long], userId: Long, role: String)

class Roles(tag: Tag) extends Table[DBRole](tag, "roles") {
  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def userID = column[Long]("USER_ID")
  def role = column[String]("ROLE")
  def * = (id.?, userID, role) <>(DBRole.tupled, DBRole.unapply)
}

object Users {
  val users = TableQuery[Users]
}
case class DBPasswordInfo(
                           hasher: String,
                           password: String,
                           salt: Option[String],
                           userID: Long)
class PasswordInfos(tag: Tag) extends Table[DBPasswordInfo](tag, "passwordinfo") {
    def hasher = column[String]("hasher")
    def password = column[String]("password")
    def salt = column[Option[String]]("salt")
    def loginInfoId = column[Long]("loginInfoId") 
  def * = (hasher, password, salt, loginInfoId) <>((DBPasswordInfo.apply _).tupled, DBPasswordInfo.unapply)
}
object DBPasswordInfo {
  def passwordInfo2db(userID: Long, pwInfo: PasswordInfo) = DBPasswordInfo(pwInfo.hasher,pwInfo.password,pwInfo.salt,userID)
  implicit def db2PasswordInfo(pwInfo: DBPasswordInfo): PasswordInfo = new PasswordInfo(pwInfo.hasher,pwInfo.password,pwInfo.salt)
  implicit def dbTableElement2PasswordInfo(pwInfo: PasswordInfos#TableElementType): PasswordInfo = new PasswordInfo(pwInfo.hasher,pwInfo.password,pwInfo.salt)
}



class LoginInfos(tag: Tag) extends Table[DBLoginInfo](tag, "logininfo") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def providerID = column[String]("providerID")
  def providerKey = column[String]("providerKey")
  def * = (id.?, providerID, providerKey) <> (DBLoginInfo.tupled, DBLoginInfo.unapply)
}

case class DBUserLoginInfo (
  userID: String,
  loginInfoId: Long
)

case class DBLoginInfo (
    id: Option[Long],
    providerID: String,
    providerKey: String
)


class UserLoginInfos(tag: Tag) extends Table[DBUserLoginInfo](tag, "userlogininfo") {
  def userID = column[String]("userID")
  def loginInfoId = column[Long]("loginInfoId")
  def * = (userID, loginInfoId) <> (DBUserLoginInfo.tupled, DBUserLoginInfo.unapply)
}