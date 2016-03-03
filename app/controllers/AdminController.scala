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


import javax.inject.Inject

import com.mohiva.play.silhouette.api.{ Environment, LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import forms._
import models.User
import play.api.i18n.MessagesApi
import models._
import models.daos._






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


}
