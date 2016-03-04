package controllers

import play.api.Play.current
import play.api.libs.mailer._

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


import javax.inject.Inject

import com.mohiva.play.silhouette.api.{ Environment, LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import forms._
import models.User
import play.api.i18n.MessagesApi
import models._
import models.daos._





import java.io.File
import org.apache.commons.mail.EmailAttachment





import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers._
import forms.SignUpForm
import models.User
import models.services.UserService
import play.api.i18n.{ MessagesApi, Messages }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.Action

import scala.concurrent.Future





import scala.concurrent.Future

class AdminController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, CookieAuthenticator],
  fillsDAO:FillsDAO,
  fillAttributesDAO: FillAttributesDAO,
  socialProviderRegistry: SocialProviderRegistry,
  mailerClient: MailerClient,

  userService: UserService,
  authInfoRepository: AuthInfoRepository,
  avatarService: AvatarService,
  passwordHasher: PasswordHasher)

  extends Silhouette[User, CookieAuthenticator] {	

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Awaitable, Await, Future}
import scala.util.Try


  def await[T](a: Awaitable[T])(implicit ec: ExecutionContext) = Await.result(a, Duration.Inf)
  def awaitAndPrint[T](a: Awaitable[T])(implicit ec: ExecutionContext) = println(await(a))


def index = SecuredAction.async { implicit request => 
	  val fillingsF = fillsDAO.getAll	
	  val fillings = await(fillingsF)
	  Future.successful(Ok(views.html.admin(request.identity, forms.FillForm.form, fillings )))
}

def create_filling() = SecuredAction.async { implicit request => 
    forms.FillForm.form.bindFromRequest.fold(
      form => { 
  		  val fillingsF = fillsDAO.getAll	
		  val fillings = await(fillingsF)
      	Future.successful(BadRequest(views.html.admin(request.identity, forms.FillForm.form, fillings ) ))
      },
      data => {
		  fillsDAO.create(FillDTO(id = None, phone = data.phone)).map { r =>
		  		  val fillingsF = fillsDAO.getAll	
				  val fillings = await(fillingsF)
				  Ok(views.html.admin(request.identity, forms.FillForm.form, fillings ))
		  }			  
      }
    )
}


val smsComponent = new SMSComponent

def registerFill(id: Long) = SecuredAction.async { implicit request =>
	  val fillingsF = fillsDAO.getAll	
	  val fillings = await(fillingsF)
	  val current_fill = fillings.find(fill => fill.id.get == id).get
      val attrs = await(fillAttributesDAO.findByFill(id))

	  val firstName = retriveFromAttrSeq(attrs, attribute="firstname")
	  val lastName = retriveFromAttrSeq(attrs, attribute="lastName")

        val loginInfo = LoginInfo(CredentialsProvider.ID, current_fill.phone)
        userService.retrieve(loginInfo).flatMap {
          case Some(user) =>
            Future.successful(Redirect(routes.ApplicationController.signUp()).flashing("error" -> Messages("user.exists")))
          case None =>
            val password = smsComponent.generatePassword
            println("generated password: "+password)
            smsComponent.sendSms(current_fill.phone, text = s"Ваш пароль для входа $password")
            val authInfo = passwordHasher.hash(password)
            val user = User(
              userID = UUID.randomUUID(),
              loginInfo = loginInfo,
              firstName = Some(firstName),
              lastName = Some(lastName),
              fullName = Some(firstName + " " + lastName),
              email = Some(current_fill.phone),
              avatarURL = None
            )
            for {
              avatar <- avatarService.retrieveURL(current_fill.phone)
              user <- userService.save(user.copy(avatarURL = avatar))
              authInfo <- authInfoRepository.add(loginInfo, authInfo)
              authenticator <- env.authenticatorService.create(loginInfo)
              r2 <- fillsDAO.registerUser(id)
              //value <- env.authenticatorService.init(authenticator)
              //result <- env.authenticatorService.embed(value, Redirect(routes.UserFillingController.index()))
            } yield {
              //env.eventBus.publish(SignUpEvent(user, request, request2Messages))
              //env.eventBus.publish(LoginEvent(user, request, request2Messages))
			  Redirect(routes.AdminController.index)
            }
        }

}



def isFillAreCorrect(id: Long) = SecuredAction.async { implicit request => 
  val fillingsF = fillsDAO.getAll	
  val fillings = await(fillingsF)
  Future.successful(Ok(views.html.admin(request.identity, forms.FillForm.form, fillings )))
}


def signRequested(id: Long) = SecuredAction.async { implicit request => 
  val fillingsF = fillsDAO.getAll	
  val fillings = await(fillingsF)
  fillsDAO.signRequested(id).map { r2 =>
	  Redirect(routes.UserFillingController.index)
  }	  
}

def signReady(id: Long) = SecuredAction.async { implicit request => 
  val fillingsF = fillsDAO.getAll	
  val fillings = await(fillingsF)
  fillsDAO.signMarketByCode(id).map { r2 =>
	  Redirect(routes.AdminController.index)
  }	  
}

def smsCode(id: Long) = SecuredAction.async { implicit request => 
  val fillingsF = fillsDAO.getAll	
  val fillings = await(fillingsF)
	forms.FillForm.form.bindFromRequest.fold(
	  form => {
	  	println("error")
	  	println(form)
	  	Future.successful(Redirect(routes.UserFillingController.index))
	  },
	  data => {
	  	println(data)  
	  fillsDAO.smsCode(id, data.phone).flatMap { r2 =>
		  fillsDAO.signMarketByCode(id).map { r2 =>
			  Redirect(routes.UserFillingController.index)
		  }	 
	  }	  
	  })
}



def fillAreCorrect(id: Long) = SecuredAction.async { implicit request => 
  val fillingsF = fillsDAO.getAll	
  val fillings = await(fillingsF)
  fillsDAO.correctFilling(id).map { r2 =>
	  Redirect(routes.UserFillingController.index)
  }
  // ПОЧТА	  
}



def removeFill(id: Long) = SecuredAction.async { implicit request =>
	  val fillingsF = fillsDAO.getAll	
	  val fillings = await(fillingsF)
  fillsDAO.delete(id).map { r2 =>
	Redirect(routes.AdminController.index)
  }	  
}
def closeFill(id: Long) = SecuredAction.async { implicit request =>
	  val fillingsF = fillsDAO.getAll	
	  val fillings = await(fillingsF)
  	fillsDAO.signComplete(id).map { r2 =>
	  Redirect(routes.AdminController.index)
	}	  
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


def writeFill(id: Long) = SecuredAction.async { implicit request =>
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
      snils = retriveFromAttrSeq(attrs, attribute="snils")
)
)

	  Ok(views.html.fillData(request.identity, id, form ))
	}
}



def saveFill(id: Long) = SecuredAction.async { implicit request =>
	  val fillingsF = fillsDAO.getAll	
	  val fillings = await(fillingsF)
    forms.PrimaryFillForm.form.bindFromRequest.fold(
      form => {
      	println("error")
      	println(form)
      	Future.successful(Redirect(routes.AdminController.index))
      },
      data => {
      	println(data)

/*
PrimaryFillData(
      lastName:String,
      firstname:String,
      middleName:String,
      dob:String,
      placeOfBorn:String,
      passport:String,
      passportIssuedDate:String,
      passportIssuedBy:String,
      inn:String,
      snils:String
)*/
val fillAttributes = List(    
FillAttributeDTO(id=None,
	fill_id=id,
	attribute="lastName",
	value=data.lastName),
FillAttributeDTO(id=None,
	fill_id=id,
	attribute="firstname",
	value=data.firstname),
FillAttributeDTO(id=None,
	fill_id=id,
	attribute="middleName",
	value=data.middleName),
FillAttributeDTO(id=None,
	fill_id=id,
	attribute="dob",
	value=data.dob),
FillAttributeDTO(id=None,
	fill_id=id,
	attribute="placeOfBorn",
	value=data.placeOfBorn),
FillAttributeDTO(id=None,
	fill_id=id,
	attribute="passport",
	value=data.passport),
FillAttributeDTO(id=None,
	fill_id=id,
	attribute="passportIssuedDate",
	value=data.passportIssuedDate),
FillAttributeDTO(id=None,
	fill_id=id,
	attribute="passportIssuedBy",
	value=data.passportIssuedBy),
FillAttributeDTO(id=None,
	fill_id=id,
	attribute="kodPodrazdelenia",
	value=data.kodPodrazdelenia),


FillAttributeDTO(id=None,
	fill_id=id,
	attribute="inn",
	value=data.inn),
FillAttributeDTO(id=None,
	fill_id=id,
	attribute="snils",
	value=data.snils))



val attrF = Future.sequence(fillAttributes.map { attr =>
	fillAttributesDAO.findOrCreate(id, attr)
})

	for {
              attr <- attrF
              r2 <- fillsDAO.areFilled(id)
            } yield {
	   Redirect(routes.AdminController.index)

	}


      })

}

def testMail = SecuredAction.async { implicit request =>
	Mailer.sendEmail(mailerClient)
  val fillingsF = fillsDAO.getAll	
	  val fillings = await(fillingsF)
	  Future.successful(Ok(views.html.admin(request.identity, forms.FillForm.form, fillings )))

}

}

object Mailer {
def sendEmail(mailerClient: MailerClient) {
  val cid = "1234"
  val email = Email(
    "Кто тяжело работает тот?",
    "Clersky FROM <mailbot@clerksy.ru>",
    Seq("Stan TO <e.timofeev@clerksy.ru>","Stan TO <iamjacke@gmail.com>"),
    // adds attachment
    attachments = Seq(),
    // sends text, HTML or both...
    bodyText = Some("Кто тяжело работает тот?"),
    bodyHtml = Some(s"""<html><body><p>ТОТ <b>Тяжело отдыхает</b> message with cid <img src="cid:$cid"></p></body></html>""")
  )
  mailerClient.send(email)
}
}