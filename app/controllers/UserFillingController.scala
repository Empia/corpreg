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

class UserFillingController @Inject() (
  val messagesApi: MessagesApi,
  fillsDAO:FillsDAO,
  ws: WSClient,
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
     *///
      //if (fill.filled && !fill.filledCorrect) {
        // Личные данные
        // Адрес регистрации
        if (fill.addressFilled == false) {
        Future.successful(Redirect(routes.UserFillingController.passport))
        // Налоговый режим
        } else {
          if (fill.addressFilled) {
            Future.successful(Redirect(routes.UserFillingController.fillNalog))
          } else 
          if (fill.nalogFilled) {
            Future.successful(Redirect(routes.AdminController.writeFillFilesUser))
          } else 
          if (fill.userFilesUploaded) {
            Future.successful(Redirect(routes.UserFillingController.fillConfirmation))
          } else 
          if (fill.signCreationConfirm) {
            Future.successful(Redirect(routes.UserFillingController.fillConfirmation))
          } else {
            Future.successful(Redirect(routes.UserFillingController.passport))
          } 
        }




        //  identityConfirmRequest
        //  identityConfirmApproved

      ////} else
      //if (fill.filled && fill.filledCorrect && !fill.signMarked) {
    	  //Future.successful(Redirect(routes.SignController.index))
      ////} else
      //if (fill.filled && fill.filledCorrect && fill.signMarked && !fill.signCompleted) {
      	//Future.successful(Redirect(routes.UserFillingController.fillSign))
      ////} else
      //if (fill.filled && fill.filledCorrect && fill.signMarked && fill.signCompleted && (fill.smsCode != "0000")) {
      	//Future.successful(Redirect(routes.UserFillingController.fillSendFns))
      //} else {
    	  //Future.successful(Redirect(routes.UserFillingController.passport))
      //}
    }
  case _ => Future(Ok("Вас нету в системе. Свяжитесь с нами "))
}

}

def await[T](a: Awaitable[T])(implicit ec: ExecutionContext) = Await.result(a, Duration.Inf)
def awaitAndPrint[T](a: Awaitable[T])(implicit ec: ExecutionContext) = println(await(a))


/*
setAttraddressFilled
setAttrnalogFilled
setAttruserFilesUploaded
setAttrsignCreationConfirm
setAttridentityConfirmRequest
setAttridentityConfirmApproved
*/


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
      locationAddress= retriveFromAttrSeq(attrs, attribute="locationAddress"),
      fnsreg = retriveFromAttrSeq(attrs, attribute="fnsreg"),
      oktmo = retriveFromAttrSeq(attrs, attribute="oktmo"),

      gender = retriveFromAttrSeq(attrs, attribute="gender"),
      AddressData(
        subject = retriveFromAttrSeq(attrs, attribute="subject"),
        area = retriveFromAttrSeq(attrs, attribute="area"),
        city = retriveFromAttrSeq(attrs, attribute="city"),
        settlement = retriveFromAttrSeq(attrs, attribute="settlement"),
        street = retriveFromAttrSeq(attrs, attribute="street"),
        house = retriveFromAttrSeq(attrs, attribute="house"),
        corpus = retriveFromAttrSeq(attrs, attribute="corpus"),
        flat = retriveFromAttrSeq(attrs, attribute="flat"))
    ))

	Ok(views.html.passport(request.identity,id, form ))
}

}


def fillAddress = SecuredAction.async { implicit request =>
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
        locationAddress= retriveFromAttrSeq(attrs, attribute="locationAddress"),
        fnsreg = retriveFromAttrSeq(attrs, attribute="fnsreg"),
        oktmo = retriveFromAttrSeq(attrs, attribute="oktmo"),

        gender = retriveFromAttrSeq(attrs, attribute="gender"),
        AddressData(
          subject = retriveFromAttrSeq(attrs, attribute="subject"),
          area = retriveFromAttrSeq(attrs, attribute="area"),
          city = retriveFromAttrSeq(attrs, attribute="city"),
          settlement = retriveFromAttrSeq(attrs, attribute="settlement"),
          street = retriveFromAttrSeq(attrs, attribute="street"),
          house = retriveFromAttrSeq(attrs, attribute="house"),
          corpus = retriveFromAttrSeq(attrs, attribute="corpus"),
          flat = retriveFromAttrSeq(attrs, attribute="flat"))
      ))

  	Ok(views.html.fillUserAddress(request.identity,id, form ))
  }
}




def fillNalog = SecuredAction.async { implicit request =>
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
        locationAddress= retriveFromAttrSeq(attrs, attribute="locationAddress"),
        fnsreg = retriveFromAttrSeq(attrs, attribute="fnsreg"),
        oktmo = retriveFromAttrSeq(attrs, attribute="oktmo"),

        gender = retriveFromAttrSeq(attrs, attribute="gender"),
        AddressData(
          subject = retriveFromAttrSeq(attrs, attribute="subject"),
          area = retriveFromAttrSeq(attrs, attribute="area"),
          city = retriveFromAttrSeq(attrs, attribute="city"),
          settlement = retriveFromAttrSeq(attrs, attribute="settlement"),
          street = retriveFromAttrSeq(attrs, attribute="street"),
          house = retriveFromAttrSeq(attrs, attribute="house"),
          corpus = retriveFromAttrSeq(attrs, attribute="corpus"),
          flat = retriveFromAttrSeq(attrs, attribute="flat"))
      ))

  	Ok(views.html.fillNalog(request.identity,id, form ))
  }
}





val files = List("P21001",
                "USN",
                "PASSPORT",
"PASSPORT2_SCAN",
"INN_SCAN",
"SNILS_SCAN",

                "POSHLINA")

def fillConfirmation = SecuredAction.async { implicit request =>
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
        locationAddress= retriveFromAttrSeq(attrs, attribute="locationAddress"),
        fnsreg = retriveFromAttrSeq(attrs, attribute="fnsreg"),
        oktmo = retriveFromAttrSeq(attrs, attribute="oktmo"),

        gender = retriveFromAttrSeq(attrs, attribute="gender"),
        AddressData(
          subject = retriveFromAttrSeq(attrs, attribute="subject"),
          area = retriveFromAttrSeq(attrs, attribute="area"),
          city = retriveFromAttrSeq(attrs, attribute="city"),
          settlement = retriveFromAttrSeq(attrs, attribute="settlement"),
          street = retriveFromAttrSeq(attrs, attribute="street"),
          house = retriveFromAttrSeq(attrs, attribute="house"),
          corpus = retriveFromAttrSeq(attrs, attribute="corpus"),
          flat = retriveFromAttrSeq(attrs, attribute="flat"))
      ))

      val filesCn:List[FileValue] = files.map { fileId =>
        FileValue(fileId, retriveFromAttrSeq(attrs, attribute=fileId))
      }

  	Ok(views.html.fillConfirmation(request.identity,id, form, attrs, filesCn, phone ))
  }
}


def fillDuty = SecuredAction.async { implicit request =>
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
        locationAddress= retriveFromAttrSeq(attrs, attribute="locationAddress"),
        fnsreg = retriveFromAttrSeq(attrs, attribute="fnsreg"),
        oktmo = retriveFromAttrSeq(attrs, attribute="oktmo"),

        gender = retriveFromAttrSeq(attrs, attribute="gender"),
        AddressData(
          subject = retriveFromAttrSeq(attrs, attribute="subject"),
          area = retriveFromAttrSeq(attrs, attribute="area"),
          city = retriveFromAttrSeq(attrs, attribute="city"),
          settlement = retriveFromAttrSeq(attrs, attribute="settlement"),
          street = retriveFromAttrSeq(attrs, attribute="street"),
          house = retriveFromAttrSeq(attrs, attribute="house"),
          corpus = retriveFromAttrSeq(attrs, attribute="corpus"),
          flat = retriveFromAttrSeq(attrs, attribute="flat"))
      ))

      val filesCn:List[FileValue] = files.map { fileId =>
        FileValue(fileId, retriveFromAttrSeq(attrs, attribute=fileId))
      }

val r = await(DutyProcess.checkPaymentProcess(phone,
  fillsDAO,
  fillAttributesDAO) )

  val reqOpt = (r.json \ "Result" \ "CheckURL").asOpt[String]
  val url = reqOpt match {
    case Some(r) => r.replace("\\", "") 
    case _ => "" 
    
  }
  val payed = reqOpt.isDefined

    Ok(views.html.internal_forms.fillDuty(request.identity,id, attrs, phone, payed, url ))
  }
}
def fillUserIdent = SecuredAction.async { implicit request =>
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
        locationAddress= retriveFromAttrSeq(attrs, attribute="locationAddress"),
        fnsreg = retriveFromAttrSeq(attrs, attribute="fnsreg"),
      oktmo = retriveFromAttrSeq(attrs, attribute="oktmo"),
          
        gender = retriveFromAttrSeq(attrs, attribute="gender"),
        AddressData(
          subject = retriveFromAttrSeq(attrs, attribute="subject"),
          area = retriveFromAttrSeq(attrs, attribute="area"),
          city = retriveFromAttrSeq(attrs, attribute="city"),
          settlement = retriveFromAttrSeq(attrs, attribute="settlement"),
          street = retriveFromAttrSeq(attrs, attribute="street"),
          house = retriveFromAttrSeq(attrs, attribute="house"),
          corpus = retriveFromAttrSeq(attrs, attribute="corpus"),
          flat = retriveFromAttrSeq(attrs, attribute="flat"))
      ))

      val filesCn:List[FileValue] = files.map { fileId =>
        FileValue(fileId, retriveFromAttrSeq(attrs, attribute=fileId))
      }
      val identityConfirmRequest = fill.identityConfirmRequest
      val identityConfirmApproved = fill.identityConfirmApproved
  
      identityConfirmApproved match {
        case false => Ok(views.html.internal_forms.fillUserIdent(request.identity,
                              id, attrs, phone, identityConfirmRequest, identityConfirmApproved ))
        case true => Redirect(routes.UserFillingController.fillSign)
      }
  }
}

// Запрос на подтверждение личности
def fillUserIdentReq = SecuredAction.async { implicit request =>
  val phone = request.identity.email.getOrElse("")
  val fill = await(fillsDAO.getByPhone(phone)).get
  val id = fill.id.get

  fillsDAO.setAttridentityConfirmRequest(id).map { l =>
    Redirect(routes.UserFillingController.fillUserIdent)
  }
}
// Подтверждение личности
def fillUseruserIdentCf(fillId: Long) = SecuredAction.async { implicit request =>
  //val phone = request.identity.email.getOrElse("")
  //val fill = await(fillsDAO.getByPhone(phone)).get
  val id = fillId

  fillsDAO.setAttridentityConfirmApproved(id).map { l =>
    Redirect(routes.AdminController.index)
  }
}




def fillSign = SecuredAction.async { implicit request =>
	val phone = request.identity.email.getOrElse("")
	val fill = await(fillsDAO.getByPhone(phone)).get
	val id = fill.id.get

 	val attrsF = fillAttributesDAO.findByFill(id)
	attrsF.flatMap { attrs =>

    val signRequested = fill.signRequested
    val packetId = retriveFromAttrSeq(attrs, attribute="redirectUrl")
    // send and get link
    // send https://1registr.ru/ipapi/s1/

    // POST['dt']: ['1','2','3','4'], # 1 - квитанция, 2 - паспорт, 3 - прописка, 4 – снилс, 5 – 21001, 
    // 6 - усн


    // https://1registr.ru/ipapi/e1/
    // redirect uri
    // https://sign.me/signapi/multijson/f7dd68da-4f2d-11e6-bda2-18a9055c0762"

    packetId match {
      case "" => Future.successful( Redirect(routes.UserFillingController.fillSign) )
      case _ => Future.successful( Redirect(packetId) )
    }
/*
    clersky.WSDLTest.getStatus(ws, packetId).flatMap { r =>
      // check length of response
      if (r.length > 400) {
        fillsDAO.signMarketByCode(id).map { rrr =>
          if (!fill.signCompleted) {
            Redirect(routes.UserFillingController.fillSendFns)
          } else {
            Ok(views.html.internal_forms.fillSign(request.identity,id, attrs, phone, signRequested ))
          }        
        }
    } else {
        Future.successful( 
          Ok(views.html.internal_forms.fillSign(request.identity,id, attrs, phone, signRequested ))
        )
      }   
    }
*/



  }
}

def fillSendFns = SecuredAction.async { implicit request =>
	val phone = request.identity.email.getOrElse("")
	val fill = await(fillsDAO.getByPhone(phone)).get
	val id = fill.id.get

 	val attrsF = fillAttributesDAO.findByFill(id)
	attrsF.map { attrs =>


    val smsCode = fill.smsCode
    var smsCodeRequestGranted = false
    val codeSigned = smsCode match {
    	case "0000" => true
    	case _ => false
    }
    if (smsCode != "" && smsCode != "0000") {
    	smsCodeRequestGranted = true
    }
    val finalizing = (fill.signCompleted && (fill.smsCode != "0000") )

  	Ok(views.html.internal_forms.fillFns(request.identity,id, attrs, phone,
      codeSigned,
      forms.FillForm.form,
      smsCodeRequestGranted,
      finalizing
       ))
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
