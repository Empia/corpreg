package models.daos

import com.mohiva.play.silhouette.api.LoginInfo
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape.proveShapeOf
import models._


case class FNSCodeDTO (
                      id:String,
                      reg:Option[String],
                      id2:Option[String],
                      id3:Option[String],
                      id4:Option[String],
                      id5:Option[String],
                      id6:Option[String],
                      title:Option[String],
                      id7:Option[String],
                      date1:Option[String],
                      date2:Option[String],
                      date3:Option[String],
                      worktime:Option[String],
                      c:Option[String],
                      d:Option[String],
                      e:Option[String],
                      f:Option[String],
                      h:Option[String],
                      t:Option[String],
                      y:Option[String],
                      i:Option[String] )

case class FNSCode2DTO (
                      id:String,
                      reg:Option[String],
                      id2:Option[String],
                      id3:Option[String],
                      id4:Option[String],
                      id5:Option[String],
                      id6:Option[String],
                      title:Option[String],
                      id7:Option[String],
                      date1:Option[String],
                      date2:Option[String],
                      date3:Option[String],
                      worktime:Option[String],
                      c:Option[String],
                      d:Option[String],
                      e:Option[String],
                      f:Option[String],
                      h:Option[String],
                      t:Option[String],
                      y:Option[String],
                      i:Option[String] )

trait DBTableDefinitions {

  protected val driver: JdbcProfile
  import driver.api._


  class FNSCodes2(tag: Tag) extends Table[FNSCode2DTO](tag, "codes6") {
    def id = column[String]("id")
    def reg = column[Option[String]]("reg")
    def id2 = column[Option[String]]("id2")
    def id3 = column[Option[String]]("id3")
    def id4 = column[Option[String]]("id4")
    def id5 = column[Option[String]]("id5")
    def id6 = column[Option[String]]("id6")
    def title = column[Option[String]]("title")
    def id7 = column[Option[String]]("id7")
    def date1 = column[Option[String]]("date1")
    def date2 = column[Option[String]]("date2")
    def date3 = column[Option[String]]("date3")
    def worktime = column[Option[String]]("worktime")
    def c = column[Option[String]]("c")
    def d = column[Option[String]]("d")
    def e = column[Option[String]]("e")
    def f = column[Option[String]]("f")
    def h = column[Option[String]]("h")
    def t = column[Option[String]]("t")
    def y = column[Option[String]]("y")
    def i = column[Option[String]]("i")

    def * = (
  id,
  reg,
  id2,
  id3,
  id4,
  id5,
  id6,
  title,
  id7,
  date1,
  date2,
  date3,
  worktime,
  c,
  d,
  e,
  f,
  h,
  t,
  y,
  i) <> (FNSCode2DTO.tupled, FNSCode2DTO.unapply)
  }

class FNSCodes(tag: Tag) extends Table[FNSCodeDTO](tag, "codes5") {
  def id = column[String]("id")
  def reg = column[Option[String]]("reg")
  def id2 = column[Option[String]]("id2")
  def id3 = column[Option[String]]("id3")
  def id4 = column[Option[String]]("id4")
  def id5 = column[Option[String]]("id5")
  def id6 = column[Option[String]]("id6")
  def title = column[Option[String]]("title")
  def id7 = column[Option[String]]("id7")
  def date1 = column[Option[String]]("date1")
  def date2 = column[Option[String]]("date2")
  def date3 = column[Option[String]]("date3")
  def worktime = column[Option[String]]("worktime")
  def c = column[Option[String]]("c")
  def d = column[Option[String]]("d")
  def e = column[Option[String]]("e")
  def f = column[Option[String]]("f")
  def h = column[Option[String]]("h")
  def t = column[Option[String]]("t")
  def y = column[Option[String]]("y")
  def i = column[Option[String]]("i")

  def * = (
id,
reg,
id2,
id3,
id4,
id5,
id6,
title,
id7,
date1,
date2,
date3,
worktime,
c,
d,
e,
f,
h,
t,
y,
i) <> (FNSCodeDTO.tupled, FNSCodeDTO.unapply)
}


class FillAttributes(tag: Tag) extends Table[FillAttributeDTO](tag, "fill_attributes") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def fill_id = column[Long]("fill_id")
    def attribute = column[String]("attribute")
    def value = column[String]("value")
    def * = (id.?, fill_id, attribute, value) <> (FillAttributeDTO.tupled, FillAttributeDTO.unapply)
}

class Fills(tag: Tag) extends Table[FillDTO](tag, "fill") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def phone = column[String]("phone")
    def registered = column[Boolean]("registered")
    def filled = column[Boolean]("filled")
    def filledCorrect = column[Boolean]("filledCorrect")
    def signRequested = column[Boolean]("signRequested")
    def signMarked = column[Boolean]("signMarked")
    def smsCode = column[String]("smsCode")
    def signCompleted = column[Boolean]("signCompleted")
    def * = (id.?, phone, registered,
      filled,
      filledCorrect,
      signRequested,
      signMarked,
      smsCode,
      signCompleted) <> (FillDTO.tupled, FillDTO.unapply)
}










  case class DBUser (
    userID: String,
    firstName: Option[String],
    lastName: Option[String],
    fullName: Option[String],
    email: Option[String],
    avatarURL: Option[String]
  )

  class Users(tag: Tag) extends Table[DBUser](tag, "user") {
    def id = column[String]("userID", O.PrimaryKey)
    def firstName = column[Option[String]]("firstName")
    def lastName = column[Option[String]]("lastName")
    def fullName = column[Option[String]]("fullName")
    def email = column[Option[String]]("email")
    def avatarURL = column[Option[String]]("avatarURL")
    def * = (id, firstName, lastName, fullName, email, avatarURL) <> (DBUser.tupled, DBUser.unapply)
  }

  case class DBLoginInfo (
    id: Option[Long],
    providerID: String,
    providerKey: String
  )

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

  class UserLoginInfos(tag: Tag) extends Table[DBUserLoginInfo](tag, "userlogininfo") {
    def userID = column[String]("userID")
    def loginInfoId = column[Long]("loginInfoId")
    def * = (userID, loginInfoId) <> (DBUserLoginInfo.tupled, DBUserLoginInfo.unapply)
  }

  case class DBPasswordInfo (
    hasher: String,
    password: String,
    salt: Option[String],
    loginInfoId: Long
  )

  class PasswordInfos(tag: Tag) extends Table[DBPasswordInfo](tag, "passwordinfo") {
    def hasher = column[String]("hasher")
    def password = column[String]("password")
    def salt = column[Option[String]]("salt")
    def loginInfoId = column[Long]("loginInfoId")
    def * = (hasher, password, salt, loginInfoId) <> (DBPasswordInfo.tupled, DBPasswordInfo.unapply)
  }

  case class DBOAuth1Info (
    id: Option[Long],
    token: String,
    secret: String,
    loginInfoId: Long
  )

  class OAuth1Infos(tag: Tag) extends Table[DBOAuth1Info](tag, "oauth1info") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def token = column[String]("token")
    def secret = column[String]("secret")
    def loginInfoId = column[Long]("loginInfoId")
    def * = (id.?, token, secret, loginInfoId) <> (DBOAuth1Info.tupled, DBOAuth1Info.unapply)
  }

  case class DBOAuth2Info (
    id: Option[Long],
    accessToken: String,
    tokenType: Option[String],
    expiresIn: Option[Int],
    refreshToken: Option[String],
    loginInfoId: Long
  )

  class OAuth2Infos(tag: Tag) extends Table[DBOAuth2Info](tag, "oauth2info") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def accessToken = column[String]("accesstoken")
    def tokenType = column[Option[String]]("tokentype")
    def expiresIn = column[Option[Int]]("expiresin")
    def refreshToken = column[Option[String]]("refreshtoken")
    def loginInfoId = column[Long]("logininfoid")
    def * = (id.?, accessToken, tokenType, expiresIn, refreshToken, loginInfoId) <> (DBOAuth2Info.tupled, DBOAuth2Info.unapply)
  }

  case class DBOpenIDInfo (
    id: String,
    loginInfoId: Long
  )

  class OpenIDInfos(tag: Tag) extends Table[DBOpenIDInfo](tag, "openidinfo") {
    def id = column[String]("id", O.PrimaryKey)
    def loginInfoId = column[Long]("logininfoid")
    def * = (id, loginInfoId) <> (DBOpenIDInfo.tupled, DBOpenIDInfo.unapply)
  }

  case class DBOpenIDAttribute (
    id: String,
    key: String,
    value: String
  )

  class OpenIDAttributes(tag: Tag) extends Table[DBOpenIDAttribute](tag, "openidattributes") {
    def id = column[String]("id")
    def key = column[String]("key")
    def value = column[String]("value")
    def * = (id, key, value) <> (DBOpenIDAttribute.tupled, DBOpenIDAttribute.unapply)
  }

  // table query definitions
  val slickUsers = TableQuery[Users]
  val slickLoginInfos = TableQuery[LoginInfos]
  val slickUserLoginInfos = TableQuery[UserLoginInfos]
  val slickPasswordInfos = TableQuery[PasswordInfos]
  val slickOAuth1Infos = TableQuery[OAuth1Infos]
  val slickOAuth2Infos = TableQuery[OAuth2Infos]
  val slickOpenIDInfos = TableQuery[OpenIDInfos]
  val slickOpenIDAttributes = TableQuery[OpenIDAttributes]



  val fills = TableQuery[Fills]
  val fill_attributes = TableQuery[FillAttributes]


  val codes5 = TableQuery[FNSCodes]
  val codes6 = TableQuery[FNSCodes2]

  // queries used in multiple places
  def loginInfoQuery(loginInfo: LoginInfo) =
    slickLoginInfos.filter(dbLoginInfo => dbLoginInfo.providerID === loginInfo.providerID && dbLoginInfo.providerKey === loginInfo.providerKey)
}
