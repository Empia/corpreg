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


class FNSCodesController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, CookieAuthenticator],
  fillsDAO:FillsDAO,
  ws: WSClient,
  codes: FNSCodeDAO,
  codes2: FNSCode2DAO,
  fillAttributesDAO: FillAttributesDAO,
  socialProviderRegistry: SocialProviderRegistry)
  extends Silhouette[User, CookieAuthenticator] {


def index(glob: String) = Action.async { implicit request =>
  import play.api.libs.json.{JsNull,Json,JsString,JsValue}


    codes2.getByGlobal(glob).flatMap { seq =>
      val opt = seq.find(s => s.c.isDefined)
        opt match {
          case Some(fns) => {
            codes.getByGlobal2(fns.c.getOrElse[String]("")).map { c2 =>
              val fns = c2.last
              val json: JsValue = Json.obj(
                "name" -> fns.title.getOrElse[String](""),
                "code" -> fns.id6.getOrElse[String]("")
              )
              Ok(json)
            }
          }
          case _ => Future.successful( Ok("Not found") )
        }
    }


ws.url("https://service.nalog.ru/addrno-proc.json").withHeaders("headerKey" -> "headerValue").post(Map(
  //"objectAddr" -> Seq("value"),
  //"objectAddr_zip" -> Seq("634061"),
  //"objectAddr_ifns" -> Seq("7017"),
  //"objectAddr_okatom" -> Seq("69401000000"),
  "ifns" -> Seq(glob),
  //"oktmmf" -> Seq("69701000"),
  "lk" -> Seq("false"),
  "c" -> Seq("next"),
  "step" -> Seq("1")

)).map {
  response =>
    Ok(response.body.toString)
}
}


def fnsOktmo(glob: String) = Action.async { implicit request =>
  import play.api.libs.json.{JsNull,Json,JsString,JsValue}


  ws.url("https://suggestions.dadata.ru/suggestions/api/4_1/rs/suggest/party").withHeaders(
    "Content-Type" -> "application/json",
    "Accept" -> "application/json",
    "headerKey" -> "headerValue",
    "Authorization" -> "Token 2aba8de760c817b3e11ac7726435d42a124d5f62",
    "X-Secret" -> "5fe3c7bcbe3363365af9657e800674680bde36c4"
  ).post(s"""{ "query": "${glob}" } """

  ).flatMap {
    response =>
    val address = ((response.json \ "suggestions" )(0) \ "data" \ "address" \ "value").as[String]

  ws.url("https://suggestions.dadata.ru/suggestions/api/4_1/rs/suggest/address").withHeaders(
    "Content-Type" -> "application/json",
    "Accept" -> "application/json",
    "headerKey" -> "headerValue",
    "Authorization" -> "Token 2aba8de760c817b3e11ac7726435d42a124d5f62",
    "X-Secret" -> "5fe3c7bcbe3363365af9657e800674680bde36c4"
  ).post(s"""{ "query": "${address}" } """

  ).map { c =>

      println(c.body)
      Ok( ((response.json \ "suggestions" )(0) \ "data" \ "address" \ "data" \ "oktmo").as[String] ) 
    }
  }
}

def passport(num: String) = Action.async { implicit request =>
  import play.api.libs.json.{JsNull,Json,JsString,JsValue}


  ws.url("https://dadata.ru/api/v2/clean/passport").withHeaders(
    "headerKey" -> "headerValue",
    "Authorization" -> "Token 2aba8de760c817b3e11ac7726435d42a124d5f62",
    "X-Secret" -> "5fe3c7bcbe3363365af9657e800674680bde36c4"
  ).post(s"""["${num}"]"""

  ).map {
    response =>
      Ok(response.body.toString)
  }
}

}
