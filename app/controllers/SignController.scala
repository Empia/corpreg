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
import models.User
import play.api.i18n.MessagesApi

import scala.concurrent.Future
import info.folone.scala.poi._

import scala.concurrent.ExecutionContext.Implicits.global

import play.api.Play.current
import play.api.libs.mailer._
import java.io.File
import org.apache.commons.mail.EmailAttachment
import play.api.libs.json._
import play.api.mvc._
import play.api.libs.ws._

class SignController @Inject() (
  val messagesApi: MessagesApi,
  fillsDAO:FillsDAO,
  fillAttributesDAO: FillAttributesDAO,
  mailerClient: MailerClient,
  ws: WSClient,

  val env: Environment[User, CookieAuthenticator],
  socialProviderRegistry: SocialProviderRegistry)
  extends Silhouette[User, CookieAuthenticator] {

def await[T](a: Awaitable[T])(implicit ec: ExecutionContext) = Await.result(a, Duration.Inf)
def awaitAndPrint[T](a: Awaitable[T])(implicit ec: ExecutionContext) = println(await(a))


implicit val fillDTOWrites = Json.writes[models.FillDTO]
implicit val fillDTOFormat = Json.format[models.FillDTO]


def states = SecuredAction.async { implicit request =>
  val phone = request.identity.email.getOrElse("")
	val fill = await(fillsDAO.getByPhone(phone)).get
	val id = fill.id.get
  Future.successful(Ok(
    Json.toJson(fill))
  )

}


def index = SecuredAction.async { implicit request =>
	val phone = request.identity.email.getOrElse("")
	val fill = await(fillsDAO.getByPhone(phone)).get
	val id = fill.id.get
	val signRequested = fill.signRequested
  fillsDAO.correctFilling(id).map { r2 =>
  	// Mail
  	if (!fill.filledCorrect){
	Mailer.sendFullEmail(mailerClient, phone, request.identity.fullName,
		action = "Подтвердил правильность анкетных данных")
	}
	  Ok(views.html.sign(request.identity, signRequested ))
//	  Redirect(routes.UserFillingController.index)
  }
}








def uuid = java.util.UUID.randomUUID.toString

def requestSign = SecuredAction.async { implicit request =>

	val phone = request.identity.email.getOrElse("")

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

val packetId: String = clersky.WSDLTest.saveDoc(phone,
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

    )
  

  val cleanPacketId = packetId replaceAll ("-", "")

  fillsDAO.signRequested(id).flatMap { r2 =>
  	if (!fill.signRequested){
	Mailer.sendFullEmail(mailerClient, phone, request.identity.fullName,
		action = "Запросил выпуск электронной подписи")
	}

    fillAttributesDAO.findOrCreate(id,
      FillAttributeDTO(None,
                        fill_id = id,
                        attribute = "packetId",
                        value = cleanPacketId)).map { r3 =>
    Redirect(routes.UserFillingController.fillSign)
//    Redirect(routes.UserFillingController.index)
    }



  }
}







def retriveSms = SecuredAction.async { implicit request =>
	val phone = request.identity.email.getOrElse("")
	val fill = await(fillsDAO.getByPhone(phone)).get
	val id = fill.id.get
 	val attrs = await(fillAttributesDAO.findByFill(id))

    val firstName = retriveFromAttrSeq(attrs, attribute="firstName")
    val lastName = retriveFromAttrSeq(attrs, attribute="lastName")
 	val inn = retriveFromAttrSeq(attrs, attribute="inn")
    val snils = retriveFromAttrSeq(attrs, attribute="snils")
    val smsCode = fill.smsCode
    var smsCodeRequestGranted = false
    val codeSigned = smsCode match {
    	case "0000" => true
    	case _ => false
    }
    if (smsCode != "" && smsCode != "0000") {
    	smsCodeRequestGranted = true
    }

    Future(
    	Ok(views.html.signReady(request.identity, codeSigned,
				firstName,
				lastName,
				inn,
				snils,
				forms.FillForm.form,
				smsCodeRequestGranted
    		))
    )
}

def sendDocs() = SecuredAction.async { implicit request =>
	val phone = request.identity.email.getOrElse("")
	val fill = await(fillsDAO.getByPhone(phone)).get
	val id = fill.id.get
	fillsDAO.smsCode(id, "0000").map { r =>
	  	if (fill.smsCode != "0000"){
			Mailer.sendFullEmail(mailerClient, phone, request.identity.fullName,
			action = "Решил подписать и отправить документы в налоговую")
		}
	    	Redirect(routes.UserFillingController.fillSendFns)
	}
}

def sendSms() = SecuredAction.async { implicit request =>
	val phone = request.identity.email.getOrElse("")
	val fill = await(fillsDAO.getByPhone(phone)).get
	val id = fill.id.get

	forms.FillForm.form.bindFromRequest.fold(
	  form => {
	  	println("error")
	  	println(form)
	  	Future.successful(Redirect(routes.UserFillingController.index)	)
	  },
	  data => {
	  		fillsDAO.smsCode(id, data.phone).flatMap { r =>
      	  if (fill.smsCode == "0000"){
      			Mailer.sendFullEmail(mailerClient, phone, request.identity.fullName,
      			action = s"Получил кодик и решил передать его на подписание. Сам кодик $data.phone")
      		}
          val attrs = await(fillAttributesDAO.findByFill(id))
          val abnGuid = retriveFromAttrSeq(attrs, attribute="abnGuid")

          clersky.WSDLTest.test4(ws, abnGuid, data.phone).flatMap { oH =>
            val sessionKeyXml = scala.xml.XML.loadString(oH)
            val sessionKey = (sessionKeyXml \\ "Sessionkey").text 
              val attr = FillAttributeDTO(id=None,
                  fill_id=id,
                  attribute="sessionKey",
                  value=sessionKey)
              fillAttributesDAO.findOrCreate(id, attr).flatMap { ohhhh => 
                  val firstName = retriveFromAttrSeq(attrs, attribute="firstname")
                  val lastName = retriveFromAttrSeq(attrs, attribute="lastName")
                  val patronymic = retriveFromAttrSeq(attrs, attribute="middleName")

                  clersky.WSDLTest.sendFiles(ws, sessionKey, firstName+lastName+patronymic, phone).flatMap { rczxcz =>
                    fillsDAO.signComplete(id).map { r2 =>
                      Redirect(routes.UserFillingController.fillSendFns) 
                    } 
                  }

               
              }            
          }          


			}
	  })
}
def finalizing = SecuredAction.async { implicit request =>
	val phone = request.identity.email.getOrElse("")
	val fill = await(fillsDAO.getByPhone(phone)).get
	val id = fill.id.get
    Future(
    	Ok(views.html.finalizing(request.identity ))
    )
}









private def retriveAttribute(c: Option[FillAttributeDTO]):String = {
	c match {
		case Some(attr) => attr.value
		case _ => ""
	}
}
private def retriveFromAttrSeq(attrs: Seq[FillAttributeDTO], attribute:String):String = {
	retriveAttribute(attrs.find(attr => attr.attribute == attribute))
}





}
