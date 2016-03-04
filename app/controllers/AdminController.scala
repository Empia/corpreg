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

def registered_user = SecuredAction.async { implicit request => 
	  val fillingsF = fillsDAO.getAll	
	  val fillings = await(fillingsF)

/* def smsSignUp = Action.async { implicit request =>
    SignUpForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.signUp(form))),
      data => {
        val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
        userService.retrieve(loginInfo).flatMap {
          case Some(user) =>
            Future.successful(Redirect(routes.ApplicationController.signUp()).flashing("error" -> Messages("user.exists")))
          case None =>
            val password = smsComponent.generatePassword
            smsComponent.sendSms(data.email, text = s"Ваш пароль для входа $password")
            val authInfo = passwordHasher.hash(password)
            val user = User(
              userID = UUID.randomUUID(),
              loginInfo = loginInfo,
              firstName = Some(data.firstName),
              lastName = Some(data.lastName),
              fullName = Some(data.firstName + " " + data.lastName),
              email = Some(data.email),
              avatarURL = None
            )
            for {
              avatar <- avatarService.retrieveURL(data.email)
              user <- userService.save(user.copy(avatarURL = avatar))
              authInfo <- authInfoRepository.add(loginInfo, authInfo)
              authenticator <- env.authenticatorService.create(loginInfo)
              value <- env.authenticatorService.init(authenticator)
              result <- env.authenticatorService.embed(value, Redirect(routes.ApplicationController.index()))
            } yield {
              env.eventBus.publish(SignUpEvent(user, request, request2Messages))
              env.eventBus.publish(LoginEvent(user, request, request2Messages))
              result
            }
        }
      }
    )
  }
 */
	  Future.successful(Ok(views.html.admin(request.identity, forms.FillForm.form, fillings )))
}



def isFillAreCorrect = SecuredAction.async { implicit request => 
  val fillingsF = fillsDAO.getAll	
  val fillings = await(fillingsF)
  Future.successful(Ok(views.html.admin(request.identity, forms.FillForm.form, fillings )))
}

def fillAreCorrect = SecuredAction.async { implicit request => 
  val fillingsF = fillsDAO.getAll	
  val fillings = await(fillingsF)
  Future.successful(Ok(views.html.admin(request.identity, forms.FillForm.form, fillings )))
}



def removeFill(id: Long) = SecuredAction.async { implicit request =>
	  val fillingsF = fillsDAO.getAll	
	  val fillings = await(fillingsF)
	  Future.successful(Ok(views.html.admin(request.identity, forms.FillForm.form, fillings )))

}
def closeFill(id: Long) = SecuredAction.async { implicit request =>
	  val fillingsF = fillsDAO.getAll	
	  val fillings = await(fillingsF)
	  Future.successful(Ok(views.html.admin(request.identity, forms.FillForm.form, fillings )))

}
def registerFill(id: Long) = SecuredAction.async { implicit request =>
	  val fillingsF = fillsDAO.getAll	
	  val fillings = await(fillingsF)
	  Future.successful(Ok(views.html.admin(request.identity, forms.FillForm.form, fillings )))

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
      	Future.successful(Ok(views.html.fillData(request.identity, id, forms.PrimaryFillForm.form )))
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



Future.sequence(fillAttributes.map { attr =>
	fillAttributesDAO.findOrCreate(id, attr)
}).map { r =>
   Ok(views.html.admin(request.identity, forms.FillForm.form, fillings ))
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
    Seq("Stan TO <iamjacke@gmail.com>"),
    // adds attachment
    attachments = Seq(),
    // sends text, HTML or both...
    bodyText = Some("Кто тяжело работает тот?"),
    bodyHtml = Some(s"""<html><body><p>An <b>Тяжело отдыхает</b> message with cid <img src="cid:$cid"></p></body></html>""")
  )
  mailerClient.send(email)
}
}