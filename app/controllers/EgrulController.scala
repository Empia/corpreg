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

      if (!response.json.as[JsArray].as[Seq[JsObject]].headOption.isDefined) {
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
    name match {
      case JsDefined(v) => Ok( obj.toString replaceAll (""""(url)" : "((\\"|[^"])*)"""", "\"checksum\": \"1\"") ) 
      case undefined: JsUndefined => Ok("z")
    }
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

