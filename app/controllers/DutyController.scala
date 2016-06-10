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
	OKTMO: String,
	PayeeName: String,
	PayeeBIC: String,
	PayeePersonalAcc: String,
	PayeeINN: String,
	PayeeKPP: String,
	HASH: String = ""
	)
case class MobiRequestResult(
	  Error: String,
      Result: MobiRequestResultObj 
)
case class MobiRequestResultObj(
    ttl: Int,
    url: String
  )

case class IfnsDetails(
ifnsPhone: String,
ifnsKpp: String,
ifnsComment: String,
ifnsAddr: String,
ifnsCode: String,
ifnsName: String,
ifnsInn: String
)

case class PayeeDetails(
    payeeName: String,
    payeeAcc:String,
    bankBik:String,
    payeeKpp:String,
    payeeInn:String,
    bankName:String)

class DutyController @Inject() (
  val messagesApi: MessagesApi,
  fillsDAO:FillsDAO,
  fillAttributesDAO: FillAttributesDAO,
  val env: Environment[User, CookieAuthenticator],
  socialProviderRegistry: SocialProviderRegistry)
  extends Silhouette[User, CookieAuthenticator] {

implicit val MobiRequestResultObjFormat = Json.format[MobiRequestResultObj]
implicit val MobiRequestResultObjWrites = Json.writes[MobiRequestResultObj]

implicit val MobiRequestResultWrites = Json.writes[MobiRequestResult]
implicit val MobiRequestResultFormat = Json.format[MobiRequestResult]


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

implicit val PayeeDetailsWrites = Json.writes[PayeeDetails]
implicit val PayeeDetailsFormat = Json.format[PayeeDetails]


implicit val IfnsDetailsWrites = Json.writes[IfnsDetails]
implicit val IfnsDetailsFormat = Json.format[IfnsDetails]


implicit val MobiRequestWrites = Json.writes[MobiRequest]
implicit val MobiRequestFormat = Json.format[MobiRequest]


/*
{
  "ifnsDetails": {
    "ifnsPhone": "8-800-222-22-22",
    "ifnsKpp": "771401001",
    "ifnsComment": "Код ОКПО: 17654504 Понедельник-четверг с 9-00 до 18-00, пятница с 9-00 до 16-45",
    "ifnsAddr": ",125284,Москва г,,,,Хорошевское ш,12А,,",
    "ifnsCode": "7700",
    "ifnsName": "Управление Федеральной налоговой службы по г.Москве",
    "ifnsInn": "7710474590"
  },
  "form": {
    "lk": "false",
    "step": "1",
    "ifns": "7700"
  },
  "payeeDetails": {
    "payeeName": "Управление Федерального казначейства по г. Москве (Управление Федеральной налоговой службы по г.Москве)",
    "payeeAcc": "40101810800000010041",
    "bankBik": "044583001",
    "payeeKpp": "771401001",
    "payeeInn": "7710474590",
    "bankName": "Отделение 1 Москва"
  },
  "nextStep": 2,
  "step": 1
}
*/

val config = new AsyncHttpClientConfigBean()
  config.setAcceptAnyCertificate(true)
  config.setFollowRedirect(true)
  val ws = NingWSClient(config) 
val r = scala.util.Random


def index(phone: String) = Action.async { implicit request =>

  val abnGuid = uuid
  val fill = await(fillsDAO.getByPhone(phone)).get
  val id = fill.id.get
  val attrs = await(fillAttributesDAO.findByFill(id))

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


val orderId = r.nextInt(10000000)
println("orderId " + orderId)

val fullName = firstName+" "+lastName+" "+patronymic
//val address = subject+" "+city+" "+settlement+" "+street+" "+house+" "+corpus+" "+flat
val region = area


val fnsResF: Future[WSResponse] = ws.url("https://service.nalog.ru/addrno-proc.json")
.post((Map(
		"ifns" -> Seq( retriveFromAttrSeq(attrs, attribute="fnsreg")),
		"lk" ->  Seq("false"),
		"c" ->  Seq("next"),
		"step" ->  Seq("1"),
		"npKind" ->  Seq("fl"),
		"objectAddr" ->  Seq(""),
		"oktmmf" ->  Seq("")
		)))
val fnsRes = await(fnsResF)

println("fnsRes: " + fnsRes.json)

val fnsData = (fnsRes.json \ "ifnsDetails").as[IfnsDetails] 
val payee = (fnsRes.json \ "payeeDetails").as[PayeeDetails]

println("t"+ retriveFromAttrSeq(attrs, attribute="subject"))

val req = MobiRequest(OrderID = orderId,
					Amount = 800,
					FIO = lastName + " " +firstName+" "+patronymic,
					Address = "117186, Нахимовский пр­кт, дом 22, кв 33",//subject+" "+city+" "+settlement+" "+street+" "+house+" "+corpus+" "+flat,
					PayerINN = inn,
					Region = "77",//retriveFromAttrSeq(attrs, attribute="subject"),
					KBK = "18210807010011000110",
					TaxName = "Государственная пошлина за регистрацию ФЛ в качестве ИП",
					URL = "http://clerksy.ru/gett-fill-duty",
					NotifyURL = "https://www.oplatagosuslug.ru/",
					OKTMO = "45373000",//retriveFromAttrSeq(attrs, attribute="oktmo"),
					PayeeName = payee.payeeName,
					PayeeBIC = payee.bankBik,
					PayeePersonalAcc = payee.payeeAcc,
					PayeeINN = payee.payeeInn,
					PayeeKPP = payee.payeeKpp,
					HASH = ""
					)


val hashFirst = s"${req.OrderID}${req.Amount}${req.FIO}${req.Address}${req.PayerINN}${req.Region}${req.KBK}${req.TaxName}${req.URL}${req.NotifyURL}${req.OKTMO}${req.PayeeName}${req.PayeeBIC}${req.PayeePersonalAcc}${req.PayeeINN}${req.PayeeKPP}B0P3OHFA"
val md = java.security.MessageDigest.getInstance("SHA-1")
val hashSha = md.digest(hashFirst.getBytes("UTF-8")).map("%02x".format(_)).mkString
val hash = java.util.Base64.getUrlEncoder.encodeToString(hashSha.getBytes("UTF-8"))

val data = Json.toJson(req.copy(HASH=hash))



val futureResponse: Future[WSResponse] = ws.url("https://demopay.oplatagosuslug.ru/tax/pay/").withHeaders("Content-Type" -> "application/json",
	"Authorization" -> "8TKM8IFG").post(data)
	futureResponse.flatMap { r => 
		println("index "+r.json)
		val url = r.json.as[MobiRequestResult].Result.url 
		    fillAttributesDAO.findOrCreate(id,
		      FillAttributeDTO(None,
		                        fill_id = id,
		                        attribute = "poshlinaOrder",
		                        value = req.OrderID.toString)).map { r3 =>
				Redirect( url )
		    }

	}


}



def check(phone: String) = Action.async { implicit request =>
  val abnGuid = uuid
  val fill = await(fillsDAO.getByPhone(phone)).get
  val id = fill.id.get
  val attrs = await(fillAttributesDAO.findByFill(id))

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


//println("orderId " + orderId)


val fnsResF: Future[WSResponse] = ws.url("https://service.nalog.ru/addrno-proc.json")
.post((Map(
		"ifns" -> Seq( retriveFromAttrSeq(attrs, attribute="fnsreg")),
		"lk" ->  Seq("false"),
		"c" ->  Seq("next"),
		"step" ->  Seq("1"),
		"npKind" ->  Seq("fl"),
		"objectAddr" ->  Seq(""),
		"oktmmf" ->  Seq("")
		)))
val fnsRes = await(fnsResF)

println("fnsRes: " + fnsRes.json)


val fnsData = (fnsRes.json \ "ifnsDetails").as[IfnsDetails] 
val payee = (fnsRes.json \ "payeeDetails").as[PayeeDetails]

val req = MobiRequest(OrderID = poshlinaFormatter(
		retriveFromAttrSeq(attrs, attribute="poshlinaOrder")),
					Amount = 800,
					FIO = lastName + " " +firstName+" "+patronymic,
					Address = subject+" "+city+" "+settlement+" "+street+" "+house+" "+corpus+" "+flat,
					PayerINN = inn,
					Region = retriveFromAttrSeq(attrs, attribute="subject"),
					KBK = "18210807010011000110",
					TaxName = "Государственная пошлина за регистрацию ФЛ в качестве ИП",
					URL = "http://clerksy.ru/gett-fill-duty",
					NotifyURL = "https://www.oplatagosuslug.ru/",
					OKTMO = "45373000",//retriveFromAttrSeq(attrs, attribute="oktmo"),
					PayeeName = payee.payeeName,
					PayeeBIC = payee.bankBik,
					PayeePersonalAcc = payee.payeeAcc,
					PayeeINN = payee.payeeInn,
					PayeeKPP = payee.payeeKpp,
					HASH = ""
)




val hashFirst = s"${req.OrderID}${req.Amount}${req.FIO}${req.Address}${req.PayerINN}${req.Region}${req.KBK}${req.TaxName}${req.URL}${req.NotifyURL}${req.OKTMO}${req.PayeeName}${req.PayeeBIC}${req.PayeePersonalAcc}${req.PayeeINN}${req.PayeeKPP}B0P3OHFA"
val md = java.security.MessageDigest.getInstance("SHA-1")
val hashSha = md.digest(hashFirst.getBytes("UTF-8")).map("%02x".format(_)).mkString
val hash = java.util.Base64.getUrlEncoder.encodeToString(hashSha.getBytes("UTF-8"))

val data = Json.toJson(req.copy(HASH=hash))



val futureResponse: Future[WSResponse] = ws.url("https://demopay.oplatagosuslug.ru/tax/check/").withHeaders("Content-Type" -> "application/json",
	"Authorization" -> "8TKM8IFG").post(data)
	futureResponse.map { r => 
		Ok( r.body)
	}


}


def poshlinaFormatter(s: String):Int = {
	s match {
		case "" => 0
		case _ => s.toInt 
	}
}

def uuid = java.util.UUID.randomUUID.toString

def await[T](a: Awaitable[T])(implicit ec: ExecutionContext) = Await.result(a, Duration.Inf)

}