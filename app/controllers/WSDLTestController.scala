package controllers
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

import models._
import models.daos._
import scala.concurrent._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Awaitable, Await, Future}
import scala.util.Try

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{ Environment, LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import forms._
import models._
import play.api.i18n.MessagesApi

import scala.concurrent.Future
import info.folone.scala.poi._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Awaitable, Await, Future}
import scala.util.Try
import javax.inject.Inject
import scala.concurrent.Future

import play.api.mvc._
import play.api.libs.ws._


class WSDLTestController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, CookieAuthenticator],
  fillsDAO:FillsDAO,
  ws: WSClient,
  fillAttributesDAO: FillAttributesDAO,
  socialProviderRegistry: SocialProviderRegistry)
  extends Silhouette[User, CookieAuthenticator] {


def index = Action.async { implicit request =>
  clersky.WSDLTest.test()
  clersky.WSDLTest.test2().map { r =>
   Ok(r)
}
}

def index2 = Action.async { implicit request =>
  clersky.WSDLTest.test3(ws, "40fa0d16-3b54-4004-b9d3-3997c4c0cc91").map { r =>
   Ok(r)
  }
}

def index3 = Action.async { implicit request =>
  clersky.WSDLTest.test4(ws, "40fa0d16-3b54-4004-b9d3-3997c4c0cc91", "cccc").map { r =>
   Ok(r)
  }
}



def uuid = java.util.UUID.randomUUID.toString

  def await[T](a: Awaitable[T])(implicit ec: ExecutionContext) = Await.result(a, Duration.Inf)
  def awaitAndPrint[T](a: Awaitable[T])(implicit ec: ExecutionContext) = println(await(a))
  private def retriveAttribute(c: Option[FillAttributeDTO]):String = {
  	c match {
  		case Some(attr) => attr.value
  		case _ => ""
  	}
  }
  private def retriveFromAttrSeq(attrs: Seq[FillAttributeDTO], attribute:String):String = {
  	retriveAttribute(attrs.find(attr => attr.attribute == attribute))
  }


def saveDoc(phone:String) = Action.async { implicit request =>
  val abnGuid = uuid
    val fill = await(fillsDAO.getByPhone(phone)).get
    val id = fill.id.get
    val attrs = await(fillAttributesDAO.findByFill(id))

val attr = FillAttributeDTO(id=None,
  	fill_id=id,
  	attribute="abnGuid",
  	value=abnGuid)
    fillAttributesDAO.findOrCreate(id, attr)

  val firstName = retriveFromAttrSeq(attrs, attribute="firstName")
  val lastName = retriveFromAttrSeq(attrs, attribute="lastName")
  val patronymic = retriveFromAttrSeq(attrs, attribute="middleName")
  val inn = retriveFromAttrSeq(attrs, attribute="inn")
  val snils = retriveFromAttrSeq(attrs, attribute="snils")
  val passport = retriveFromAttrSeq(attrs, attribute="passport")
  val passportDate = retriveFromAttrSeq(attrs, attribute="passportIssuedBy")
  val eMail = retriveFromAttrSeq(attrs, attribute="eMail")
  val postalAddress = retriveFromAttrSeq(attrs, attribute="postalAddress")
  val locationAddress = retriveFromAttrSeq(attrs, attribute="locationAddress")
  Future.successful(
    Ok(clersky.WSDLTest.saveDoc(phone,
      abnGuid = abnGuid,
      eMail,
      inn,
      shortName=s"ИП $firstName",
      postalAddress,
      locationAddress,
      snils,
      firstName,
      lastName,
      patronymic,
      passportType="1",
      passportSerial=passport,
      passportNumber=passport,
      passportDate
    ))
  )
}

}
