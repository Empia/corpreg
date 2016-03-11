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

class UserFillingController @Inject() (
  val messagesApi: MessagesApi,
  fillsDAO:FillsDAO,
  fillAttributesDAO: FillAttributesDAO,
  val env: Environment[User, CookieAuthenticator],
  socialProviderRegistry: SocialProviderRegistry)
  extends Silhouette[User, CookieAuthenticator] {


def index = SecuredAction.async { implicit request =>
  // passport stage
	val phone = request.identity.email.getOrElse("")
	await(fillsDAO.getByPhone(phone)) match {
		case Some(fill) => {

	val id = fill.id.get
	val signRequested = fill.signRequested
/*
	filled:Boolean = false,
	filledCorrect: Boolean = false,
	signRequested: Boolean = false,
	signMarked:Boolean = false,
	smsCode: String = "",
	signCompleted:Boolean = false)
 */
  if (fill.filled && !fill.filledCorrect) {
      Future.successful(Redirect(routes.UserFillingController.passport))
  } else
  if (fill.filled && fill.filledCorrect && !fill.signMarked) {
	  Future.successful(Redirect(routes.SignController.index))
  } else
  if (fill.filled && fill.filledCorrect && fill.signMarked && !fill.signCompleted) {
  	Future.successful(Redirect(routes.SignController.retriveSms))
  } else
  if (fill.filled && fill.filledCorrect && fill.signMarked && fill.signCompleted && (fill.smsCode != "0000")) {
  	Future.successful(Redirect(routes.SignController.finalizing))
  } else {
	  Future.successful(Redirect(routes.UserFillingController.passport))
  }

  }
  case _ => Future(Ok("Вас нету в системе. Свяжитесь с нами "))
}

}

def await[T](a: Awaitable[T])(implicit ec: ExecutionContext) = Await.result(a, Duration.Inf)
def awaitAndPrint[T](a: Awaitable[T])(implicit ec: ExecutionContext) = println(await(a))


def passport = SecuredAction.async { implicit request =>
	val phone = request.identity.email.getOrElse("")
	val fill = await(fillsDAO.getByPhone(phone)).get
	val id = fill.id.get

 	val attrsF = fillAttributesDAO.findByFill(id)
	attrsF.map { attrs =>



val form = forms.PrimaryFillForm.form.fill(
forms.PrimaryFillForm.PrimaryFillData(
      lastName = retriveFromAttrSeq(attrs, attribute="lastName"),
      firstname =  retriveFromAttrSeq(attrs, attribute="firstname"),
      middleName =  retriveFromAttrSeq(attrs, attribute="middleName"),
      dob =  retriveFromAttrSeq(attrs, attribute="dob"),
      placeOfBorn =  retriveFromAttrSeq(attrs, attribute="placeOfBorn"),
      passport =  retriveFromAttrSeq(attrs, attribute="passport"),
      passportIssuedDate =  retriveFromAttrSeq(attrs, attribute="passportIssuedDate"),
      kodPodrazdelenia = retriveFromAttrSeq(attrs, attribute="kodPodrazdelenia"),
      passportIssuedBy =  retriveFromAttrSeq(attrs, attribute="passportIssuedBy"),
      inn = retriveFromAttrSeq(attrs, attribute="inn"),
      snils = retriveFromAttrSeq(attrs, attribute="snils"),
      eMail= retriveFromAttrSeq(attrs, attribute="eMail"),
      postalAddress= retriveFromAttrSeq(attrs, attribute="postalAddress"),
      locationAddress= retriveFromAttrSeq(attrs, attribute="locationAddress")

    ))

	Ok(views.html.passport(request.identity,id, form ))
}

}
def address = SecuredAction.async { implicit request =>
  Future.successful(Ok(views.html.address(request.identity )))
}
def okved = SecuredAction.async { implicit request =>
  Future.successful(Ok(views.html.okved(request.identity )))
}
def taxesIP = SecuredAction.async { implicit request =>
  Future.successful(Ok(views.html.taxesIP(request.identity )))
}
def documentsIP = SecuredAction.async { implicit request =>
  Future.successful(Ok(views.html.documentsIP(request.identity )))
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
