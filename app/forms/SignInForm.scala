package forms

import play.api.data.Form
import play.api.data.Forms._


object FillForm {
  val form = Form(
    mapping(
      "phone" ->text)(FillData.apply)(FillData.unapply)
  )
  case class FillData(
    phone:String
  )
}
case class AddressData(
  subject:String, area:String,
city:String,
settlement:String,
street:String,
house:String,
corpus:String,
flat:String
) {

}
object PrimaryFillForm {
  val form = Form(
    mapping(
         "lastName" -> text,
         "firstname" -> text,
         "middleName" -> text,
         "dob" -> text,
         "placeOfBorn" -> text,
         "passport" -> text,
         "passportIssuedDate" -> text,
         "kodPodrazdelenia" -> text,
         "passportIssuedBy" -> text,
         "inn" -> text,
         "snils" -> text,
         "eMail" -> text,
         "postalAddress" -> text,
         "locationAddress" -> text,
         "fnsreg" -> text,
         "addressInfo" -> mapping(
           "subject" -> default(text, ""),
           "area" -> default(text, ""),
           "city" -> default(text, ""),
           "settlement" -> default(text, ""),
           "street" -> default(text, ""),
           "house" -> default(text, ""),
           "corpus" -> default(text, ""),
           "flat" -> default(text, "")
         )(AddressData.apply)(AddressData.unapply)

)(PrimaryFillData.apply)(PrimaryFillData.unapply)
  )
  case class PrimaryFillData(
      lastName:String,
      firstname:String,
      middleName:String,
      dob:String,
      placeOfBorn:String,
      passport:String,
      passportIssuedDate:String,
      kodPodrazdelenia: String,
      passportIssuedBy:String,
      inn:String,
      snils:String,
      eMail:String,
      postalAddress:String,
      locationAddress:String,
      fnsreg:String,
      addressInfo: AddressData
  )
}

/**
 * The form which handles the submission of the credentials.
 */
object SignInForm {

  /**
   * A play framework form.
   */
  val form = Form(
    mapping(
      "email" -> text,
      "password" -> nonEmptyText,
      "rememberMe" -> boolean
    )(Data.apply)(Data.unapply)
  )

  /**
   * The form data.
   *
   * @param email The email of the user.
   * @param password The password of the user.
   * @param rememberMe Indicates if the user should stay logged in on the next visit.
   */
  case class Data(
    email: String,
    password: String,
    rememberMe: Boolean)
}
