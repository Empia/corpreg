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
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Awaitable, Await, Future}
import scala.util.Try
import javax.inject.Inject
import scala.concurrent.Future

import play.api.mvc._
import play.api.libs.ws._

object EgrulLookupModule {

val url = "https://ru.rus.company"


implicit val NameObjectFormat = Json.format[NameObject]
implicit val NameObjectWrites = Json.writes[NameObject]
implicit val ShareObjectFormat = Json.format[ShareObject]
implicit val ShareObjectWrites = Json.writes[ShareObject]
implicit val EgrulObjectNameFormat = Json.format[EgrulObjectName]
implicit val EgrulObjectNameWrites = Json.writes[EgrulObjectName]

implicit val FundPFTObjectFormat = Json.format[FundPFTObject]
implicit val FundPFTObjectWrites = Json.writes[FundPFTObject]
implicit val FundFSSObjectFormat = Json.format[FundFSSObject]
implicit val FundFSSObjectWrites = Json.writes[FundFSSObject]

implicit val CapitalObjectFormat = Json.format[CapitalObject]
implicit val CapitalObjectWrites = Json.writes[CapitalObject]
implicit val CEOObjectFormat = Json.format[CEOObject]
implicit val CEOObjectWrites = Json.writes[CEOObject]
implicit val OwnerObjectFormat = Json.format[OwnerObject]
implicit val OwnerObjectWrites = Json.writes[OwnerObject]

implicit val OwnerCompanyFormat = Json.format[OwnerCompany]
implicit val OwnerCompanyWrites = Json.writes[OwnerCompany]
implicit val OkvedMainObjectFormat = Json.format[OkvedMainObject]
implicit val OkvedMainObjectWrites = Json.writes[OkvedMainObject]
implicit val OkvedObjectFormat = Json.format[OkvedObject]
implicit val OkvedObjectWrites = Json.writes[OkvedObject]
implicit val AddressObjectFormat = Json.format[AddressObject]
implicit val AddressObjectWrites = Json.writes[AddressObject]
implicit val FundObjectFormat = Json.format[FundObject]
implicit val FundObjectWrites = Json.writes[FundObject]


implicit val EgrulObjectFormat = Json.format[EgrulObject]
implicit val EgrulObjectWrites = Json.writes[EgrulObject]

implicit val EgrulContainerFormat = Json.format[EgrulContainer]
implicit val EgrulContainerWrites = Json.writes[EgrulContainer]

}