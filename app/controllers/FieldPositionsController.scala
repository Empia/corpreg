package controllers

import play.api.Play.current
import play.api.libs.mailer._

import play.api._
import play.api.mvc._
import play.twirl.api.Html

import play.api.http.MimeTypes
import play.api.libs.json._
import play.api.cache._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats
import play.api.data.format.Formatter
import play.api.data.FormError


import javax.inject.Inject

import com.mohiva.play.silhouette.api.{ Environment, LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import forms._
import models.User
import play.api.i18n.MessagesApi
import models._
import models.daos._





import java.io.File
import org.apache.commons.mail.EmailAttachment





import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers._
import forms.SignUpForm
import models.User
import models.services.UserService
import play.api.i18n.{ MessagesApi, Messages }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.Action
import scala.concurrent.Future

import play.api.libs.json._


class FieldPositionsController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, CookieAuthenticator],
  fillsDAO:FillsDAO,
  fillAttributesDAO: FillAttributesDAO,
  socialProviderRegistry: SocialProviderRegistry,
  mailerClient: MailerClient,
  positions: FormPositionsDAO,

  userService: UserService,
  authInfoRepository: AuthInfoRepository,
  avatarService: AvatarService,
  passwordHasher: PasswordHasher)

  extends Silhouette[User, CookieAuthenticator] {

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Awaitable, Await, Future}
import scala.util.Try

implicit val FormPositionWrites = Json.writes[FormPosition]
implicit val FormPositionFormat = Json.format[FormPosition]

  def await[T](a: Awaitable[T])(implicit ec: ExecutionContext) = Await.result(a, Duration.Inf)
  def awaitAndPrint[T](a: Awaitable[T])(implicit ec: ExecutionContext) = println(await(a))


  def showPage(page: String) = SecuredAction.async { implicit request =>
/*
      positions.createPosition(FormPosition(pos_x=408,pos_y=774, id="lastName", page="firstPage"))
      positions.createPosition(FormPosition(pos_x=408,pos_y=887, id="firstName", page="firstPage"))
      positions.createPosition(FormPosition(pos_x=408,pos_y=1010, id="middleName", page="firstPage"))
      positions.createPosition(FormPosition(pos_x=408,pos_y=1241, id="lastName_latin", page="firstPage"))
      positions.createPosition(FormPosition(pos_x=408,pos_y=1358, id="firstName_latin", page="firstPage"))
      positions.createPosition(FormPosition(pos_x=408,pos_y=1478, id="middleName_latin", page="firstPage"))
      positions.createPosition(FormPosition(pos_x=655,pos_y=1620, id="inn", page="firstPage"))
      positions.createPosition(FormPosition(pos_x=360,pos_y=1745, id="gender", page="firstPage"))
      positions.createPosition(FormPosition(pos_x=510,pos_y=1990, id="dob", page="firstPage"))
      positions.createPosition(FormPosition(pos_x=80,pos_y=2170, id="pob", page="firstPage"))
      positions.createPosition(FormPosition(pos_x=80,pos_y=2270, id="pob2", page="firstPage"))
      positions.createPosition(FormPosition(pos_x=480,pos_y=2524, id="grajdanstvo", page="firstPage"))

      positions.createPosition(FormPosition(pos_x=545, pos_y=615,  id="zip", page="secondPage"));
      positions.createPosition(FormPosition(pos_x=1710, pos_y=615,  id="subject", page="secondPage"));
      positions.createPosition(FormPosition(pos_x=72, pos_y=799,  id="area", page="secondPage"));
      positions.createPosition(FormPosition(pos_x=778, pos_y=799, id="area_title", page="secondPage"));
      positions.createPosition(FormPosition(pos_x=72, pos_y=909,  id="area_title_big", page="secondPage"));
      positions.createPosition(FormPosition(pos_x=72, pos_y=1083,  id="city", page="secondPage"));
      positions.createPosition(FormPosition(pos_x=778, pos_y=1083,  id="city_title", page="secondPage")); // CITY TITLE
      positions.createPosition(FormPosition(pos_x=72, pos_y=1255,  id="nasel", page="secondPage")); // NASEL PUNKT
      positions.createPosition(FormPosition(pos_x=778, pos_y=1255,  id="nasel_title", page="secondPage")); // NASEL PUNKT
      positions.createPosition(FormPosition(pos_x=72, pos_y=1371,  id="nasel_title_big", page="secondPage")); // NASEL PUNKT BIG
      positions.createPosition(FormPosition(pos_x=72, pos_y=1543,  id="street", page="secondPage")); // STREET
      positions.createPosition(FormPosition(pos_x=776, pos_y=1543,  id="street_title", page="secondPage")); // STREET
      positions.createPosition(FormPosition(pos_x=72, pos_y=1655,  id="street_title_big", page="secondPage")); // STREET
      positions.createPosition(FormPosition(pos_x=72, pos_y=1825,  id="house", page="secondPage")); // HOUSE
      positions.createPosition(FormPosition(pos_x=728, pos_y=1825,  id="house_num", page="secondPage")); // HOUSE NUMBER
      positions.createPosition(FormPosition(pos_x=1286, pos_y=1825,  id="corpus", page="secondPage")); // CORPUS
      positions.createPosition(FormPosition(pos_x=1948, pos_y=1817,  id="corpus_num", page="secondPage")); // CORPUS NUMBER
      positions.createPosition(FormPosition(pos_x=728, pos_y=1939,  id="appartment", page="secondPage")); // APPARTMENT
      positions.createPosition(FormPosition(pos_x=1948, pos_y=1939,  id="appartment_num", page="secondPage")); // APPARTMENT NUMBER
      positions.createPosition(FormPosition(pos_x=486, pos_y=2235,  id="vid_document", page="secondPage")); // VID DOCUMENTS
      positions.createPosition(FormPosition(pos_x=672, pos_y=2414,  id="series", page="secondPage")); // SERIES NUMBER
      positions.createPosition(FormPosition(pos_x=426, pos_y=2525,  id="issue_date", page="secondPage")); // DATA VIDACHI
      positions.createPosition(FormPosition(pos_x=422, pos_y=2652,  id="issuer", page="secondPage")); // KEM VIDAN
      positions.createPosition(FormPosition(pos_x=72, pos_y=2767,  id="issuer2", page="secondPage")); // KEM VIDAN 2
      positions.createPosition(FormPosition(pos_x=72, pos_y=2887,  id="issuer3", page="secondPage")); // KEM VIDAN 3
      positions.createPosition(FormPosition(pos_x=536, pos_y=3029,  id="issuer_code1", page="secondPage")); // KOD PODR 1
      positions.createPosition(FormPosition(pos_x=778, pos_y=3029,  id="issuer_code2", page="secondPage")); // KOD PODR 2
/////////////////////////////////////
positions.createPosition(FormPosition(pos_x=260, pos_y=738,  id="vid_doc",page="thirdPage")); // VID
positions.createPosition(FormPosition(pos_x=557, pos_y=878,  id="page3_doc_number",page="thirdPage")); // NOMER DOCUMENTA
positions.createPosition(FormPosition(pos_x=445, pos_y=1033,  id="page3_doc_number1",page="thirdPage")) // NOMER DOCUMENTA
positions.createPosition(FormPosition(pos_x=432, pos_y=1166,  id="page3_doc_number2",page="thirdPage")) // NOMER DOCUMENTA
positions.createPosition(FormPosition(pos_x=90, pos_y=1278,  id="page3_doc_number3",page="thirdPage")) // NOMER DOCUMENTA
positions.createPosition(FormPosition(pos_x=90, pos_y=1386,  id="page3_doc_number4",page="thirdPage")) // NOMER DOCUMENTA
positions.createPosition(FormPosition(pos_x=500, pos_y=1556,  id="page3_doc_number5",page="thirdPage")) // NOMER DOCUMENTA
////////////////////////////////////
positions.createPosition(FormPosition(pos_x=924, pos_y=779,  id="activity_type", page="FourthPage")); // VID DEYATELNOSTY
////////////////////////////////////
positions.createPosition(FormPosition(pos_x=264, pos_y=513,  id="fio", page="fifthPage")); // FIO
positions.createPosition(FormPosition(pos_x=256, pos_y=1011,  id="vidat", page="fifthPage")); // VIDAT
positions.createPosition(FormPosition(pos_x=872, pos_y=1223, id="phone", page="fifthPage")); // PHONE
positions.createPosition(FormPosition(pos_x=290, pos_y=1345, id="email", page="fifthPage")); // EMAIL
//////////////////////////////////////
positions.createPosition(FormPosition(pos_x=758, pos_y=105, id="inn", page="firstPageUsn")); // INN
positions.createPosition(FormPosition(pos_x=758, pos_y=200, id="kpp", page="firstPageUsn")); // KPP
positions.createPosition(FormPosition(pos_x=834, pos_y=475, id="nalogovaya", page="firstPageUsn")); // NALOGOVAYA
positions.createPosition(FormPosition(pos_x=1870, pos_y=479, id="priznak", page="firstPageUsn")); // PRIZNAK
positions.createPosition(FormPosition(pos_x=75, pos_y=661, id="fio1", page="firstPageUsn")); // FIO 1
positions.createPosition(FormPosition(pos_x=75, pos_y=763, id="fio2", page="firstPageUsn")); // FIO 2
positions.createPosition(FormPosition(pos_x=75, pos_y=861, id="fio3", page="firstPageUsn")); // FIO 3
positions.createPosition(FormPosition(pos_x=75, pos_y=961, id="fio4", page="firstPageUsn")); // FIO 3
positions.createPosition(FormPosition(pos_x=1050, pos_y=1115, id="perehodit1", page="firstPageUsn")); // PEREHODIT
positions.createPosition(FormPosition(pos_x=935, pos_y=1335, id="perehodit2", page="firstPageUsn")); // PEREHODIT
positions.createPosition(FormPosition(pos_x=286, pos_y=1975, id="tax_type", page="firstPageUsn")); // TYPE NALOGOPLATILSHIKA
positions.createPosition(FormPosition(pos_x=82, pos_y=2061, id="fio1", page="firstPageUsn")); // FIO 1
positions.createPosition(FormPosition(pos_x=82, pos_y=2159, id="fio2", page="firstPageUsn")); // FIO 2
positions.createPosition(FormPosition(pos_x=82, pos_y=2257, id="fio3", page="firstPageUsn")); // FIO 3
positions.createPosition(FormPosition(pos_x=657, pos_y=2625, id="usn_date", page="firstPageUsn")); // DATE
*/

  	   Future.successful(Ok(views.html.position_page(page) )
     )
 }
 def getPositions(page: String) = SecuredAction.async { implicit request =>
      positions.getPosition(page).map { positions =>
        val distinct_positions = positions
          Ok( Json.toJson(distinct_positions) )
      }

}

def updatePosition(page: String)= SecuredAction.async { implicit request =>
  println("request.body " + request.body)
  val pos = request.body.asJson.get.as[FormPosition]

    positions.updatePosition(pos).map { positions =>
        Ok( "Good" )
    }


}

}
