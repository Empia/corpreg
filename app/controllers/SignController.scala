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


class SignController @Inject() (
  val messagesApi: MessagesApi,
  fillsDAO:FillsDAO,
  fillAttributesDAO: FillAttributesDAO,    
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
	  Ok(views.html.sign(request.identity, signRequested ))
//	  Redirect(routes.UserFillingController.index)
  }	  
}

def requestSign = SecuredAction.async { implicit request => 
	val phone = request.identity.email.getOrElse("")
	val fill = await(fillsDAO.getByPhone(phone)).get
	val id = fill.id.get
  fillsDAO.signRequested(id).map { r2 =>
	  Ok(views.html.sign(request.identity, true ))
//	  Redirect(routes.UserFillingController.index)
  }	  
}

def retriveSms = SecuredAction.async { implicit request => 
	val phone = request.identity.email.getOrElse("")
	val fill = await(fillsDAO.getByPhone(phone)).get
	val id = fill.id.get
    Future(
    	Ok(views.html.sign(request.identity, true ))
    )
}
def finalizing = SecuredAction.async { implicit request => 
	val phone = request.identity.email.getOrElse("")
	val fill = await(fillsDAO.getByPhone(phone)).get
	val id = fill.id.get
    Future(
    	Ok(views.html.sign(request.identity, true ))
    )
}






}