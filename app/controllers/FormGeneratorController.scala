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

class FormGeneratorController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, CookieAuthenticator],
  fillsDAO:FillsDAO,
  fillAttributesDAO: FillAttributesDAO,
  socialProviderRegistry: SocialProviderRegistry,
  mailerClient: MailerClient,
  positions: FormPositionsDAO,

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




  import scala.sys.process._
  def testAction() = SecuredAction { implicit request =>
    val phone = request.identity.email.getOrElse("email")
    val positions_dto = await(positions.getAll())
    var positionsList = positions_dto.toList


    val fields = new com.journaldev.di.test.FormFields()
    fields.addPositions(AllPositions(positionsList))


    com.journaldev.di.test.MainClass1.main2(Array(
            "ФАМИЛИЯФАМИЛИЯФАМИЛИЯФАМИЛИЯ",
            "ИМЯИМЯИМЯИМЯИМЯИМЯ",
            "ОТЧЕСТВООТЧЕСТВООТЧЕСТВООТЧЕСТВО",

            "ТЕСТ","ТЕСТ","ТЕСТ","ТЕСТ","ТЕСТ", "ТЕСТ", phone
          ),
          fields
        )

    Seq("convert", s"public/*Page.png", s"public/${phone}.tif").!!
    Seq("convert", s"public/*Usn.png", s"public/${phone}_usn.tif").!!

    Ok("test")
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
        snils = retriveFromAttrSeq(attrs, attribute="snils"),
        eMail= retriveFromAttrSeq(attrs, attribute="eMail"),
        postalAddress= retriveFromAttrSeq(attrs, attribute="postalAddress"),
        locationAddress= retriveFromAttrSeq(attrs, attribute="locationAddress"),
        fnsreg = retriveFromAttrSeq(attrs, attribute="fnsreg"),
        AddressData(
        subject = retriveFromAttrSeq(attrs, attribute="subject"),
        area = retriveFromAttrSeq(attrs, attribute="area"),
        city = retriveFromAttrSeq(attrs, attribute="city"),
        settlement = retriveFromAttrSeq(attrs, attribute="settlement"),
        street = retriveFromAttrSeq(attrs, attribute="street"),
        house = retriveFromAttrSeq(attrs, attribute="house"),
        corpus = retriveFromAttrSeq(attrs, attribute="corpus"),
        flat = retriveFromAttrSeq(attrs, attribute="flat"))
  )
  )

  	  Ok(views.html.fillFormGeneratorData(request.identity, id, form ))
  	}
  }

  val files = List("P21001",
                  "USN",
                  "PASSPORT",
                  "POSHLINA")


  def saveFill(id: Long) = SecuredAction.async { implicit request =>
  	  val fillingsF = fillsDAO.getAll
  	  val fillings = await(fillingsF)
      val current_fill = fillings.find(fill => fill.id.get == id).get

      val phone = current_fill.phone

      forms.PrimaryFillForm.form.bindFromRequest.fold(
        form => {
        	println("error")
        	println(form)
        	Future.successful(Redirect(routes.AdminController.index))
        },
        data => {
        	println(data)

    val positions_dto = await(positions.getAll())
    var positionsList = positions_dto.toList

    val fields = new com.journaldev.di.test.FormFields()
    fields.addPositions(AllPositions(positionsList))

//    val fields = new com.journaldev.di.test.FormFields()
    val email = request.identity.email.getOrElse("6666")



    fields.put("lastName", data.lastName.toUpperCase() )
    fields.put("firstName", data.firstname.toUpperCase() )
    fields.put("middleName", data.middleName.toUpperCase() )
    fields.put("lastName_latin", "" )
    fields.put("firstName_latin", "" )
    fields.put("middleName_latin", "" )
    fields.put("inn", data.inn)
    fields.put("gender", "1")
    fields.put("dob", data.dob)
    fields.put("pob", data.placeOfBorn.toUpperCase() )
    fields.put("pob2", "" )
    fields.put("grajdanstvo", "1")
    fields.put("zip", data.postalAddress )
    fields.put("subject", "" ) // #
    fields.put("area", "" ) // #
    fields.put("area_title", "" ) // #
    fields.put("area_title_big", "" ) // #
    fields.put("city", "") // #
    fields.put("city_title", "") // #
    fields.put("nasel", "" ) // #
    fields.put("nasel_title", "" ) // #
    fields.put("nasel_title_big", "" ) // #
    fields.put("street", "") // #
    fields.put("street_title", "") // #
    fields.put("street_title_big", "") // #
    fields.put("house", "" ) // #
    fields.put("house_num", "" ) // #
    fields.put("corpus", "" ) // #
    fields.put("corpus_num", "" ) // #
    fields.put("appartment", "" ) // #
    fields.put("appartment_num", "" ) // #
    fields.put("vid_document", "1")
    fields.put("series", data.passport)
    fields.put("issue_date", data.passportIssuedDate)
    fields.put("issuer", data.passportIssuedBy)
    fields.put("issuer2", "" )
    fields.put("issuer3", "" )
    fields.put("issuer_code1", data.kodPodrazdelenia )
    fields.put("issuer_code2", data.kodPodrazdelenia )



    fields.put("vid_doc", "" )
    fields.put("page3_doc_number", "" )
    fields.put("page3_doc_number1", "" )
    fields.put("page3_doc_number2", "" )
    fields.put("page3_doc_number3", "" )
    fields.put("page3_doc_number4", "" )
    fields.put("page3_doc_number5", "" )


    fields.put("activity_type", "") // #


    fields.put("fio", data.lastName.toUpperCase()+" "+data.firstname.toUpperCase()+" "+data.middleName.toUpperCase())
    fields.put("vidat", "1")
    fields.put("phone", phone)
    fields.put("email", data.eMail.toUpperCase() )


    fields.put("inn", data.inn)
    fields.put("kpp", "")
    fields.put("nalogovaya", data.fnsreg)
    fields.put("priznak", "1")
    fields.put("fio1", data.lastName.toUpperCase()+" "+data.firstname.toUpperCase()+" "+data.middleName.toUpperCase())
    fields.put("fio2", "")
    fields.put("fio3", "")
    fields.put("fio4", "")
    fields.put("perehodit1", "1")
    fields.put("perehodit2", "1")
    fields.put("tax_type", "1")
    fields.put("fio1", data.lastName.toUpperCase())
    fields.put("fio2", data.firstname.toUpperCase())
    fields.put("fio3", data.middleName.toUpperCase())
    fields.put("usn_date", "10.04.2016")























    com.journaldev.di.test.MainClass1.main2(Array(
        "entity.arg1.toUpperCase()",
        "entity.arg2.toUpperCase()",
        "entity.arg3.toUpperCase()",
        "entity.arg4.toUpperCase()",
        "entity.arg5.toUpperCase()",
        "entity.arg6.toUpperCase()",
        "entity.arg7.toUpperCase()",
        "entity.arg8.toUpperCase()",
        "entity.arg9.toUpperCase()", phone
      ),fields)

      import sys.process._

      Seq("convert", s"public/*Page.png", s"public/${phone}.tif").!!
      Seq("convert", s"public/*Usn.png", s"public/${phone}_usn.tif").!!

      Seq(s"mkdir", "-p", s"./public/files/doc_$phone").lineStream
      Seq(s"mkdir", "-p", s"./public/files/doc_$phone/P21001").lineStream
      Seq(s"mkdir", "-p", s"./public/files/doc_$phone/USN").lineStream

      Seq(s"mv", s"./public/${phone}.tif", s"./public/files/doc_$phone/P21001").lineStream
      Seq(s"mv", s"./public/${phone}_usn.tif", s"./public/files/doc_$phone/USN").lineStream

      val attr = FillAttributeDTO(id=None,
      	fill_id=id,
      	attribute=s"P21001",
      	value=s"${phone}.tif")
        val attr2 = FillAttributeDTO(id=None,
        	fill_id=id,
        	attribute=s"USN",
        	value=s"${phone}_usn.tif")
      	val createdAttrs = List(
          fillAttributesDAO.findOrCreate(id, attr),
          fillAttributesDAO.findOrCreate(id, attr2))



      val attrF = Future.sequence(createdAttrs)


  	for {
                attr <- attrF
                r2 <- fillsDAO.areFilled(id)
              } yield {
  	   Redirect(routes.AdminController.writeFillFiles(id) )

  	}


        })

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
