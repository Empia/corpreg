package models

import java.util.UUID

import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }
import com.mohiva.play.silhouette.api.{LoginInfo, Identity}
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import play.api.libs.json._
/**
 * The user object.
 *
 * @param userID The unique ID of the user.
 * @param loginInfo The linked login info.
 * @param firstName Maybe the first name of the authenticated user.
 * @param lastName Maybe the last name of the authenticated user.
 * @param fullName Maybe the full name of the authenticated user.
 * @param email Maybe the email of the authenticated provider.
 * @param avatarURL Maybe the avatar URL of the authenticated provider.
 */
case class User(
  userID: Option[Long],
  firstName: Option[String],
  lastName: Option[String],
  fullName: Option[String],
  email: Option[String],
  avatarURL: Option[String],
  providerID: String = "",
  providerKey: String = "",
  roles: Set[String] = Set("USER")) extends Identity {
  
  def loginInfo = new LoginInfo(providerID, providerKey)
  def toTuple() = (userID, firstName, lastName, fullName, email, avatarURL, providerID, providerKey)
  def addRole(role: String) = copy(roles = roles + role)
}



object User {
  implicit val userFormat = Json.format[User]

  def withoutRoles(t: (Option[Long], Option[String], Option[String], Option[String], Option[String], Option[String], String, String)) =
    User(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, Set())

  def toTuple(u: User) = Some((
  	u.userID, 
  	u.firstName, 
  	u.lastName, 
  	u.fullName, 
  	u.email, 
  	u.avatarURL,
  	u.providerID, 
  	u.providerKey))
}
