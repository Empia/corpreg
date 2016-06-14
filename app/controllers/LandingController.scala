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
import play.api.mvc._
import play.api.libs.ws._
import play.api.mvc._
import play.api.libs.ws._
import scala.concurrent.ExecutionContext.Implicits.global

import com.ning.http.client.AsyncHttpClientConfigBean
import play.api.libs.ws.WSResponse
import play.api.libs.ws.ning.NingWSClient
import scala.concurrent.Future

import com.ning.http.client.AsyncHttpClientConfig
import play.api.libs.ws.ning._
import play.api.libs.ws._





class LandingController @Inject() (
  val messagesApi: MessagesApi,
  fillsDAO:FillsDAO,
  fillAttributesDAO: FillAttributesDAO,
  val env: Environment[User, CookieAuthenticator],
  socialProviderRegistry: SocialProviderRegistry)
  extends Silhouette[User, CookieAuthenticator] {

def index = Action.async { implicit request =>
	Future.successful(    Ok(views.html.landing.gett()) )

}

}