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
import com.mohiva.play.silhouette.impl.authenticators._
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
  clersky.WSDLTest.test2("").map { r =>
   Ok(r)
}
}

def index2(guid: String) = Action.async { implicit request =>
  clersky.WSDLTest.test3(ws, guid).map { r =>
   Ok(r)
  }
}

def index3(guid: String, code:String) = Action.async { implicit request =>
  clersky.WSDLTest.test4(ws, guid, code).map { r =>
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


// get status of initial request
def checkStatus(guid: String) = Action.async { implicit request =>
  clersky.WSDLTest.getStatus(ws, guid).map { r =>
   Ok(r)
  }
}

// get status of initial request
def checkStatusByPhone(phone: String) = Action.async { implicit request =>
  val fill = await(fillsDAO.getByPhone(phone)).get
  val id = fill.id.get
  val attrs = await(fillAttributesDAO.findByFill(id))
  val packetId = retriveFromAttrSeq(attrs, attribute="packetId")

  clersky.WSDLTest.getStatus(ws, packetId).map { r =>
   Ok(r)
  }

}
def sendSmsByPhone(phone: String) = Action.async {
  implicit request =>
  val fill = await(fillsDAO.getByPhone(phone)).get
  val id = fill.id.get
  val attrs = await(fillAttributesDAO.findByFill(id))
  val abnGuid = retriveFromAttrSeq(attrs, attribute="abnGuid")
  // a0459ad6-89d5-4d1a-9694-319bd225f469
  //val (beg, rest1) = packetId.splitAt(8)
  //val (rest2, rest3) = rest1.splitAt(4)
  //val (rest4,rest5) = rest3.splitAt(4)
  //val (rest6,rest7) = rest5.splitAt(4)
  //val (rest8,rest9) = rest7.splitAt(12)
  //val packetIdTransformed = beg+"-"+rest2+"-"+rest4+"-"+rest6+"-"+rest8

  clersky.WSDLTest.test3(ws, abnGuid).map { r =>
   Ok(r)
  }
}

def getSmsByPhone(phone: String, code: String) = Action.async {
  implicit request =>
  val fill = await(fillsDAO.getByPhone(phone)).get
  val id = fill.id.get
  val attrs = await(fillAttributesDAO.findByFill(id))
  val abnGuid = retriveFromAttrSeq(attrs, attribute="abnGuid")

  clersky.WSDLTest.test4(ws, abnGuid, code).flatMap { r =>
  println(r)
      val sessionKeyXml = scala.xml.XML.loadString(r)
      val sessionKey = (sessionKeyXml \\ "Sessionkey").text 
      val attr = FillAttributeDTO(id=None,
            fill_id=id,
            attribute="sessionKey",
            value=sessionKey)
      fillAttributesDAO.findOrCreate(id, attr).map { ohhhh => 
           Ok(sessionKey.toString)
     }
  }
}


def sendFiles(phone: String) = Action.async { implicit request =>
  val fill = await(fillsDAO.getByPhone(phone)).get
  val id = fill.id.get
  val attrs = await(fillAttributesDAO.findByFill(id))
  val sessionKey = retriveFromAttrSeq(attrs, attribute="sessionKey")
  val firstName = retriveFromAttrSeq(attrs, attribute="firstname")
  val lastName = retriveFromAttrSeq(attrs, attribute="lastName")
  val patronymic = retriveFromAttrSeq(attrs, attribute="middleName")

  clersky.WSDLTest.sendFiles(ws, sessionKey, firstName+lastName+patronymic).map { r =>
      Ok(r)
  }

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

  val firstName = retriveFromAttrSeq(attrs, attribute="firstname")
  val lastName = retriveFromAttrSeq(attrs, attribute="lastName")
  val patronymic = retriveFromAttrSeq(attrs, attribute="middleName")
  val inn = retriveFromAttrSeq(attrs, attribute="inn")
  val snils = retriveFromAttrSeq(attrs, attribute="snils")
  val passport = retriveFromAttrSeq(attrs, attribute="passport")
  val passportIssuedBy = retriveFromAttrSeq(attrs, attribute="passportIssuedBy")
  val passportDate = retriveFromAttrSeq(attrs, attribute="passportIssuedDate")
  val eMail = retriveFromAttrSeq(attrs, attribute="eMail")
  val postalAddress = retriveFromAttrSeq(attrs, attribute="postalAddress")
  val locationAddress = retriveFromAttrSeq(attrs, attribute="locationAddress")

  val subject = retriveFromAttrSeq(attrs, attribute="subject")
  val area = retriveFromAttrSeq(attrs, attribute="area")
  val city = retriveFromAttrSeq(attrs, attribute="city")
  val settlement = retriveFromAttrSeq(attrs, attribute="settlement")
  val street = retriveFromAttrSeq(attrs, attribute="street")
  val house = retriveFromAttrSeq(attrs, attribute="house")
  val corpus = retriveFromAttrSeq(attrs, attribute="corpus")
  val flat = retriveFromAttrSeq(attrs, attribute="flat")

  Future.successful(
    Ok(clersky.WSDLTest.saveDoc(phone,
      abnGuid = abnGuid,
      eMail,
      inn,
      shortName=s"ИП $lastName",
      postalAddress,
      locationAddress,
      snils,
      firstName,
      lastName,
      patronymic,
      passportType="1",
      passportSerial=passport.split(" ").lift(0).getOrElse("")+ " "+passport.split(" ").lift(1).getOrElse(""),
      passportNumber=passport.split(" ").lift(2).getOrElse(""),
      passportDate,
      passportIssuedBy,
      subject,
      area,
      city,
    settlement,
    street,
  house,
  corpus,
  flat,

  ws

    ))
  )
}


}
