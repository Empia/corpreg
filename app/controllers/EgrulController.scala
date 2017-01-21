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
.get().flatMap { responseComp =>

ws.url(
  s"${url}/интеграция/ип/?${paramType._1}=${paramType._2}")
.get().flatMap { responseIp =>

      //println(response.body)
      //address region url
      //address street url
      //mainOkved1 url
      //okved1.map[url]
      //mainOkved2 url
      //val obj = Json.prettyPrint(response.json.as[JsObject] - "url" )
      //val z = obj.toString replaceAll (""""(url)" : "((\\"|[^"])*)"""", "")
      //println(response.json)

val initialResponseComp = responseComp.json.as[Seq[JsObject]]
val initialResponseIp = responseIp.json.as[Seq[JsObject]]
val initialOrgType = initialResponseComp.length match {
  case x  if x > 0 => "компании"
  case _ => "ип"  
}
println(initialResponseComp)
println(initialResponseComp.length > 0)
println(initialResponseIp)

      if (!(initialResponseComp.length > 0 || initialResponseIp.length > 0)) {
        Future.successful(Ok(Json.toJson(Map("status" -> "error", "info" -> "?ogrn=NUM OR ?inn=NUM must be correct in URL"))))
      } else {

      // Введи нормально номер 












      val nameVV: JsLookupResult = initialOrgType match {
        case "компании" => (responseComp.json.as[JsArray].head.as[JsObject] \ "id")
        case "ип" => (responseIp.json.as[JsArray].head.as[JsObject] \ "id")
      }
      val internalId = nameVV.get

val secondReq:String = initialOrgType match {
        case "компании" => s"${url}/интеграция/${initialOrgType}/${internalId}/"
        case "ип" => s"${url}/интеграция/ип/?${paramType._1}=${paramType._2}"
      }
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
  val objFullAdr = initialOrgType match {
    case "ип" => Json.obj("full" -> "")
    case _ => Json.obj( "full" -> ((response2.json.as[JsObject] - "url") \ "address" \ "fullHouseAddress").get) 
  }


val thirdReq:String = initialOrgType match {
        case "компании" => s"${url}/интеграция/${initialOrgType}/${internalId}/сотрудники/"
        case "ип" => s"${url}/интеграция/ип/?${paramType._1}=${paramType._2}"
}

ws.url(thirdReq)
.get().flatMap { response3 =>
/*
person.fullName = ceo.name.full_name 
person.firstName = ceo.name.first_name
person.middleName = ceo.name.middle_name
person.surName = ceo.name.last_name
person.inn = ceo.inn
postName = ceo.position
*/

     def getLookup(l: JsLookupResult):Option[JsObject] = {
        l match {
          case JsDefined(v) => v.asOpt[JsObject]
          case undefined: JsUndefined => None
        }
      }
      def getLookupString(o: Option[JsObject]):String = {
        o match {
          case Some(os) => os.as[String]
          case _ => ""
        }
      }

val fourthReq:String = initialOrgType match {
        case "компании" => s"${url}/интеграция/${initialOrgType}/${internalId}/учредители/"
        case "ип" => s"${url}/интеграция/ип/?${paramType._1}=${paramType._2}"
}
ws.url(fourthReq)
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

  //val obj = Json.prettyPrint(((response2.json.as[JsObject] - "url") - "id" - "lastUpdateDate" - "address") + 
  //  ("address" -> objFullAdr) + ("persons" -> personsObj) + ("owners" -> ownersObj) )

  println("response 2"+Json.prettyPrint(response2.json))
  println("response3: "+Json.prettyPrint(response3.json))
  println("response4: "+Json.prettyPrint(response4.json))
      val personResp = initialOrgType match {
        case "компании" => response2.json.as[JsObject]
        case "ип" => getLookup(response2.json.as[Seq[JsObject]].head \ "person").get
      }

      val innF: JsLookupResult = (personResp \ "inn")
      val innV = innF.get.as[String]
      val kppF: JsLookupResult = (personResp \ "kpp")
      val kpp = getLookupString(getLookup(kppF))
      val orgnF: JsLookupResult = (personResp \ "ogrn")
      val orgnV = getLookupString(getLookup(orgnF))    
      val orgnDateF: JsLookupResult = (personResp \ "ogrnDate")
      val orgnDate = getLookupString(getLookup(orgnDateF))

      val nameF =  (personResp \ "name")
      val nameV = getLookupString(getLookup(nameF))
      val shortNameF = (personResp \ "shortName")
      val shortNameV = getLookupString(getLookup(shortNameF))

      val capValF = (personResp \ "authorizedCapital" \ "type" \ "name")
      val capTypeNameF = getLookupString(getLookup(capValF))    
      val capValV = (personResp \ "authorizedCapital" \ "value")
      val capTypeValV = getLookupString(getLookup(capValV)) 

      val personsValF = response3.json//(response3.json.as[JsObject] \ "persons")
      val personValF = personsValF.as[JsArray].head.as[JsObject] \ "person"
      val personValV = personValF.get
      val personValVfull_name = personValF \ "fullName"
      val personValVfirst_name = personValF \ "firstName"
      val personValVmiddle_name = personValF \ "middleName"
      val personValVlast_name = personValF \ "surName"
      val personValVinn = personValF \ "inn"
      val personPositionF = personsValF.as[JsArray].head.as[JsObject] \ "postName"

      val adressValF = initialOrgType match {
        case "компании" => (response2.json.as[JsObject] \ "address" \ "fullHouseAddress")
        case "ип" => (response2.json.as[Seq[JsObject]].head \ "address" \ "fullHouseAddress")
      }
      val adressVal = getLookupString(getLookup(adressValF))

      val ownersValF = response4.json//(response4.json.as[JsObject] \ "owners")
      ////////////////////////////////////////////////////////////////////////////////////////////
      ////////////////////////////////////////////////////////////////////////////////////////////
      ////////////////////////////////////////////////////////////////////////////////////////////
 
      val ownerValF:Option[JsObject] = ownersValF.as[Seq[JsObject]].headOption match {
        case Some(x) => getLookup(x \ "companyOwner")
        case _ => None
      }


      //println("ownerValF"+Json.prettyPrint(ownersValF.as[JsArray].head.as[JsObject]))
      val ownerNameValF:Option[JsObject] = ownerValF match {
        case Some(x) => getLookup(x.as[JsObject] \ "name")
        case _ => None
        }
      val ownershortNameValF:Option[JsObject] = ownerValF match {
        case Some(x) =>  getLookup(x.as[JsObject] \ "shortName")
        case _ => None
      }
      val ownerInnValF:Option[JsObject] = ownerValF match {
        case Some(x) => getLookup(x.as[JsObject] \ "inn")
        case _ => None
        }
      val priceValF:Option[JsObject] = ownersValF.as[Seq[JsObject]].headOption match {
        case Some(x) => getLookup(x \ "price")
        case _ => None
      }      
      ////////////////////////////////////////////////////////////////////////////////////////////
      val primaryResponse = initialOrgType match {
        case "компании" => response2.json.as[JsObject]
        case "ип" => response2.json.as[Seq[JsObject]].head
      }

      val okvedMainValF:Option[JsObject] =  getLookup(primaryResponse \ "mainOkved2")
      val okvedMainCodeValF:Option[JsObject] = okvedMainValF match {
        case Some(x) => getLookup(x.as[JsObject] \ "code")
        case _ => None
      }
      val okvedMainNameValF = okvedMainValF match {
        case Some(x) => getLookup(x.as[JsObject] \ "name")
        case _ => None
      }
      val okvedValF = getLookup(primaryResponse \ "mainOkved2")
      val okvedCodeValF:Option[JsObject] = okvedValF match {
        case Some(x) => getLookup(x.as[JsObject] \ "code")
        case _ => None
      }
      val okvedNameValF:Option[JsObject] = okvedValF match {
        case Some(x) => getLookup(x \ "name")
        case _ => None
      }
   

    val pfrRegistrationValF = getLookup(primaryResponse \ "pfrRegistration") match {
      case Some(o) => o
      case _ => Json.obj("c" -> "c")
    }
    val pfrRegistrationRegNumberF = getLookup(pfrRegistrationValF \ "number")
    val pfrRegistrationNameF = getLookup(pfrRegistrationValF \ "pfr" \ "name")
    val pfrRegistrationCodeF = getLookup(pfrRegistrationValF \ "pfr" \ "code")
    val fssRegistrationValF = getLookup(primaryResponse \ "fssRegistration") match {
      case Some(o) => o
      case _ => Json.obj("c" -> "c")
    }
    val fssRegistrationRegNumberF = getLookup(fssRegistrationValF \ "number")
    val fssRegistrationNameF = getLookup(fssRegistrationValF \ "fss" \ "name")
    val fssRegistrationCodeF = getLookup(fssRegistrationValF \ "fss" \ "code")

    val pfrRegistrationRegNumber = getLookupString(pfrRegistrationRegNumberF)
    val pfrRegistrationName = getLookupString(pfrRegistrationNameF)
    val pfrRegistrationCode = getLookupString(pfrRegistrationCodeF)    
    val fssRegistrationRegNumber = getLookupString(fssRegistrationRegNumberF)
    val fssRegistrationName = getLookupString(fssRegistrationNameF)
    val fssRegistrationCode = getLookupString(fssRegistrationCodeF)

   /*
    Ok()
  */
  val json = Json.toJson(
      EgrulContainer(EgrulObject(
          name = EgrulObjectName( full_opf = nameV, short_opf = shortNameV ),
          inn=innV,
          kpp=kpp,
          ogrn=orgnV, 
          ogrn_date=orgnDate,
          capital = CapitalObject(
            capTypeNameF, capTypeValV
          ),
          ceo = CEOObject(
                          name= NameObject(full_name = personValVfull_name.get.as[String],
                                            first_name = personValVfirst_name.get.as[String],
                                            middle_name = getLookupString(getLookup(personValVmiddle_name)),
                                            last_name = personValVlast_name.get.as[String]),
                          inn=personValVinn.get.as[String],
                          position= getLookupString(getLookup(personPositionF))
          ),
          owner = OwnerObject(
            name= NameObject(full_name = getLookupString(ownerNameValF), 
                                  first_name = getLookupString(ownershortNameValF)),
            inn=getLookupString(ownerInnValF),
            share=ShareObject(value=getLookupString(priceValF),
                              portion="")
          ),
          okved_main = OkvedMainObject(
code=getLookupString(okvedMainCodeValF),
name=getLookupString(okvedMainNameValF)
          ),
          okved = OkvedObject(
  getLookupString(okvedCodeValF),
  getLookupString(okvedNameValF)),

          address = AddressObject(
            full = adressVal
          ),
          fund = FundObject(
                            FundPFTObject( 
                            reg_number=pfrRegistrationRegNumber,
                            name=pfrRegistrationName,
                            code=pfrRegistrationCode
                            ),
                            FundFSSObject(
                            reg_number=fssRegistrationRegNumber,
                            name=fssRegistrationName,
                            code=fssRegistrationCode
                            )
          )
    )
    ))
  println(Json.prettyPrint(json))

/*
    nameVV match {
      case JsDefined(v) => Ok( obj.toString replaceAll (""""(url)" : "((\\"|[^"])*)"""", "\"checksum\": \"1\"") ) 
      case undefined: JsUndefined => Ok("z")
    }
*/
Ok(Json.prettyPrint(json))

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