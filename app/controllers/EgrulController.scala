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


class EgrulController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, CookieAuthenticator],
  fillsDAO:FillsDAO,
  ws: WSClient,
  codes: FNSCodeDAO,
  codes2: FNSCode2DAO,
  fillAttributesDAO: FillAttributesDAO,
  socialProviderRegistry: SocialProviderRegistry)
  extends Silhouette[User, CookieAuthenticator] {

def await[T](a: Awaitable[T])(implicit ec: ExecutionContext) = Await.result(a, Duration.Inf)


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

def find() = Action.async { implicit request =>
  import play.api.libs.json.{JsNull,Json,JsString,JsValue}

  val paramType = request.queryString.get("inn") match {
    case Some(v) => ("инн", v.head)
    case _ => request.queryString.get("ogrn") match { 
      case Some(vv) => ("огрн", vv.head)
      case _ => ("null", "")
  } 
  }
  if (paramType._1 == "null") {
    Future.successful(Ok(Json.toJson(Map("status" -> "error", "info" -> "?ogrn=NUM OR ?inn=NUM must be present in URL"))))
  } else {
  println("paramType: "+paramType)
//интеграция/компании/?огрн=1117746123456
//интеграция/компании/?инн=7701123456

ws.url(
  s"${url}/интеграция/компании/?${paramType._1}=${paramType._2}")
.get().flatMap { response =>
//      println(response.body)
      //address region url
      //address street url
      //mainOkved1 url
      //okved1.map[url]
      //mainOkved2 url
      //val obj = Json.prettyPrint(response.json.as[JsObject] - "url" )
      //val z = obj.toString replaceAll (""""(url)" : "((\\"|[^"])*)"""", "")
      //println(response.json)

      if (!(response.json.as[JsArray].asOpt[Seq[JsObject]].isDefined || response.json.as[JsArray].head.asOpt[JsObject].isDefined) ) {
        Future.successful(Ok(Json.toJson(Map("status" -> "error", "info" -> "?ogrn=NUM OR ?inn=NUM must be correct in URL"))))
      } else {

      // Введи нормально номер 

      println((response.json.as[JsArray].head.as[JsObject] \ "id").get)












      val name: JsLookupResult = (response.json.as[JsArray].head.as[JsObject] \ "id")
      val internalId = name.get

val secondReq:String = s"${url}/интеграция/компании/${internalId}/"
println("|secondReq|: "+secondReq)

ws.url(secondReq)
.get().flatMap { response2 =>
  /*
  name = name.full_opf
  shortName = name.short_opf
  inn = inn
  kpp = kpp
  ogrn = ogrn
  ogrnDate = ogrn_date
  authorizedCapital.value = capital.value 
  authorizedCapital.type.name = capital.type

mainOkved2.code = okved_main.code
mainOkved2.name. = okved_main.name
okved2.code = okved.code
okved2.name = okved.name
address.fullHouseAddress = address.full
pfrRegistration.number = pfr.reg_number
pfrRegistration.pfr.name = pfr.name
pfrRegistration.pfr.code = pfr.code
fssRegistration.number = fss.reg_number
fssRegistration.fss.name = fss.name
fssRegistration.fss.code = fss.code

  */
  // берем только одну строчку
  val objFullAdr = Json.obj( "full" -> ((response2.json.as[JsObject] - "url") \ "address" \ "fullHouseAddress").get) 


ws.url(s"${url}/интеграция/компании/${internalId}/сотрудники/")
.get().flatMap { response3 =>
/*
person.fullName = ceo.name.full_name 
person.firstName = ceo.name.first_name
person.middleName = ceo.name.middle_name
person.surName = ceo.name.last_name
person.inn = ceo.inn
postName = ceo.position
*/



ws.url(s"${url}/интеграция/компании/${internalId}/учредители/")
.get().map { response4 =>
//  https://ru.rus.company/интеграция/компании/123456/учредители/
/*
personOwner.fullName = owner.name.full_name 
personOwner.firstName = owner.name.first_name
personOwner.middleName = owner.name.middle_name
personOwner.surName = owner.name.last_name
personOwner.inn = owner.inn
price = owner.share.value
part = owner.share.portion
*/

  val personsObj = Json.toJson(response3.json.as[Seq[JsObject]].map(c => c - "url")) 
  val ownersObj = Json.toJson(response4.json.as[Seq[JsObject]].map(c => c - "url"))

  val obj = Json.prettyPrint(((response2.json.as[JsObject] - "url") - "id" - "lastUpdateDate" - "address") + 
    ("address" -> objFullAdr) + ("persons" -> personsObj) + ("owners" -> ownersObj) )

  println("response3: "+response3.json)
  println("response4: "+response4.json)
   /*
    name match {
      case JsDefined(v) => Ok( obj.toString replaceAll (""""(url)" : "((\\"|[^"])*)"""", "\"checksum\": \"1\"") ) 
      case undefined: JsUndefined => Ok("z")
    }
  */
  Ok(Json.toJson(
    EgrulContainer(EgrulObject()
  )))


}
}
}
  }
  }
  }
}

private def withoutValue(v: JsValue) = v match {
  case JsNull => true
  case JsString("") => true
  case _ => false
}


}



case class EgrulContainer(egrul: EgrulObject)
case class EgrulObject(
name: EgrulObjectName=EgrulObjectName(), 
inn:String="",
kpp:String="",
ogrn:String="",
ogrn_date : String="",//"2002-11-12T00:00:00.000", - надо отрезать время, а то палево
//okpo: null, - будем брать из статитсики потом, пока не используем
capital: CapitalObject=CapitalObject(),
ceo:CEOObject=CEOObject(),
owner:OwnerObject=OwnerObject(),
okved_main: OkvedMainObject=OkvedMainObject(),
okved: OkvedObject=OkvedObject(),
address: AddressObject=AddressObject(),
fund: FundObject=FundObject()
)
case class CapitalObject(
value: String="",
typeVal: String=""
)

case class CEOObject(
name: NameObject=NameObject(),
inn:String="",
position: String=""
)

case class NameObject(
full_name:String = "",
first_name:String = "",
middle_name:String = "",
last_name:String = ""
)
case class OwnerObject(
name: NameObject=NameObject(),
inn:String="",
share:ShareObject=ShareObject()
) 
case class ShareObject(
value:String="",
portion:String=""
)
case class OwnerCompany(
name: EgrulObjectName = EgrulObjectName(),
inn: String="",
share: ShareObject=ShareObject()
)
case class OkvedMainObject(
code:String="",
name:String=""
)
case class OkvedObject(
code:String="",
name:String="")
case class AddressObject(full: String="")
case class FundObject(pfr: FundPFTObject=FundPFTObject(), fss: FundFSSObject=FundFSSObject()) 
case class FundPFTObject( 
reg_number:String="",
name:String="",
code:String=""
)
case class FundFSSObject(
reg_number:String="",
name:String="",
code:String=""
)
case class EgrulObjectName(
full_opf: String="",
short_opf: String=""
)