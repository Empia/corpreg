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


class SignController @Inject() (
  val messagesApi: MessagesApi,
  fillsDAO:FillsDAO,
  fillAttributesDAO: FillAttributesDAO,    
  mailerClient: MailerClient,

  val env: Environment[User, CookieAuthenticator],
  socialProviderRegistry: SocialProviderRegistry)
  extends Silhouette[User, CookieAuthenticator] {	

def await[T](a: Awaitable[T])(implicit ec: ExecutionContext) = Await.result(a, Duration.Inf)
def awaitAndPrint[T](a: Awaitable[T])(implicit ec: ExecutionContext) = println(await(a))


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

def requestSign = SecuredAction.async { implicit request => 
	val phone = request.identity.email.getOrElse("")
	val fill = await(fillsDAO.getByPhone(phone)).get
	val id = fill.id.get
  fillsDAO.signRequested(id).map { r2 =>
  	if (!fill.signRequested){
	Mailer.sendFullEmail(mailerClient, phone, request.identity.fullName, 
		action = "Запросил выпуск электронной подписи")
	}  

	  Ok(views.html.sign(request.identity, true ))
//	  Redirect(routes.UserFillingController.index)
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
	    	Redirect(routes.SignController.retriveSms)			    
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
	  		fillsDAO.smsCode(id, data.phone).map { r =>	
	  	if (fill.smsCode == "0000"){
			Mailer.sendFullEmail(mailerClient, phone, request.identity.fullName, 
			action = s"Получил кодик и решил передать его на подписание. Сам кодик $data.phone")
		}  	

		    	Redirect(routes.UserFillingController.index)			    
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