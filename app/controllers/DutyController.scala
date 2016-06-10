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

case class MobiRequest(
	OrderID: Int,
	Amount: Int,
	FIO: String,
	Address: String,
	PayerINN: String,
	Region: String,
	KBK: String,
	TaxName: String,
	URL: String,
	NotifyURL: String,
	OKTМO: String,
	PayeeName: String,
	PayeeBIC: String,
	PayeePersonalAcc: String,
	PayeeINN: String,
	PayeeKPP: String,
	HASH: String = ""
	)

class DutyController @Inject() (
  val messagesApi: MessagesApi,
  fillsDAO:FillsDAO,
  ws: WSClient,
  fillAttributesDAO: FillAttributesDAO,
  val env: Environment[User, CookieAuthenticator],
  socialProviderRegistry: SocialProviderRegistry)
  extends Silhouette[User, CookieAuthenticator] {



implicit val MobiRequestWrites = Json.writes[MobiRequest]
implicit val MobiRequestFormat = Json.format[MobiRequest]
/*
{
    "OrderID": 123456,
    "Amount": 800,
    "FIO": "Тестовый пользователь",
    "Address": "117186, Нахимовский пр­кт, дом 22, кв 33",
    "PayerINN": "771234567890",
    "Region": 77,
    "KBK": "18210807010 011000110",
    "TaxName": "Государственная пошлина за регистрацию ЮЛ",
    "URL": "http://gett.clerksy.ru/gett/gett-payment",
    "NotifyURL": "https://www.oplatagosuslug.ru/",
    "OKTМO": "40911000",
    "PayeeName": "УФК по г. Москве (МИ ФНС России o46 по г. Москве)",
    "PayeeBIC": "44583001",
    "PayeePersonalAcc": "40101810800000010041",
    "PayeeINN": "7733506810",
    "PayeeKPP": "773301001",
    "HASH": "ZmMxNWJiZjY3MmI4OGIwMGVmODZkMTQ4OWZlY2IzNTY2OWNjZjllMQ=="
}

{
    "OrderID": 123456789,
    "Amount": 400000,
    "FIO": "Иванов Иван Иванович",
    "Address": "г. Москва, ул. Ленина, д.1",
    "PayerINN": "500100732259",
    "Region": "77",
    "KBK": "18210807010011000110",
    "TaxName": "Государственная пошлина за регистрацию ЮЛ",
    "URL": "https://www.oplatagosuslug.ru/",
    "NotifyURL": "https://www.oplatagosuslug.ru/",
    "OKTMO": "40911000",
    "PayeeName": "УФК по г. Санкт-Петербургу (Межрайонная ИФНС России №11 по Санкт- Петербургу)",
    "PayeeBIC": "044030001",
    "PayeePersonalAcc": "40101810200000010001",
    "PayeeINN": "7842000011",
    "PayeeKPP": "784201001",
    "HASH": "NWUwNjMzM2ViMTc4YzhiMjkxNjYxOTFmZjQ5MmEwNjRjZDlmOWMyNA=="
}
 */

def index = SecuredAction.async { implicit request =>
	val req = MobiRequest(OrderID = 123456789,
Amount = 400000,
FIO = "Иванов Иван Иванович",
Address = "г. Москва, ул. Ленина, д.1",
PayerINN = "500100732259",
Region = "77",
KBK = "18210807010011000110",
TaxName = "Государственная пошлина за регистрацию ЮЛ",
URL = "http://gett.clerksy.ru/gett/gett-payment",
NotifyURL = "https://www.oplatagosuslug.ru/",
OKTМO = "40911000",
PayeeName = "УФК по г. Санкт-Петербургу (Межрайонная ИФНС России №11 по Санкт- Петербургу)",
PayeeBIC = "044030001",
PayeePersonalAcc = "40101810200000010001",
PayeeINN = "7842000011",
PayeeKPP = "784201001",
HASH = ""
)


val hashFirst = s"${req.OrderID}${req.Amount}${req.FIO}${req.Address}${req.PayerINN}${req.Region}${req.KBK}${req.TaxName}${req.URL}${req.NotifyURL}${req.OKTМO}${req.PayeeName}${req.PayeeBIC}${req.PayeePersonalAcc}${req.PayeeINN}${req.PayeeKPP}iODA0OD"
val md = java.security.MessageDigest.getInstance("SHA-1")
val hashSha = md.digest(hashFirst.getBytes("UTF-8")).map("%02x".format(_)).mkString
val hash = java.util.Base64.getUrlEncoder.encodeToString(hashSha.getBytes("UTF-8"))
	Future.successful(Ok( Json.toJson(req.copy(HASH=hash)) ))


}




}