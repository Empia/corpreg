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

import scala.concurrent.Future
import info.folone.scala.poi._

import scala.concurrent.ExecutionContext.Implicits.global
case class RowCellView(rowNum: Int, cellNum: Int, data: String)


object Documents {
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet; 
import org.apache.poi.ss.usermodel._;
import java.io.FileInputStream;
import java.io._;
import java.util.Iterator;
//itext libraries to create PDF table from XLSX

  def newFile(filename:String) = {
    testXlsx = new java.io.File( "." ).getCanonicalPath + filename
    documents = List(impure.load(testXlsx))
  }
  var testXlsx = new java.io.File( "." ).getCanonicalPath + "/2100111.xlsx"

  var documents = List(impure.load(testXlsx))
  
def writePdf() = {
  import com.itextpdf.text._;
  import com.itextpdf.text.pdf._;
 //First we read the XLSX in binary format into FileInputStream
                val input_document:FileInputStream = new FileInputStream(new File(testXlsx));
                val my_xls_workbook = new XSSFWorkbook(input_document)
                val my_worksheet = my_xls_workbook.getSheetAt(0)
                val rowIterator = my_worksheet.iterator()
                
                val iText_xls_2_pdf = new Document()
                PdfWriter.getInstance(iText_xls_2_pdf, new FileOutputStream("PDFOutput.pdf"))
                iText_xls_2_pdf.open()
                val my_table = new PdfPTable(2)
                var table_cell: PdfPCell = null
                while (rowIterator.hasNext) {
                  val row = rowIterator.next()
                  val cellIterator = row.cellIterator()
                  while (cellIterator.hasNext) {
                    val cell = cellIterator.next()
                    cell.getCellType match {
                      case Cell.CELL_TYPE_STRING => 
                        table_cell = new PdfPCell(new Phrase(cell.getStringCellValue))
                        my_table.addCell(table_cell)
                      case _ => 
                        table_cell = new PdfPCell(new Phrase(cell.getStringCellValue))
                        my_table.addCell(table_cell)
                    }
                  }
                }
                //Finally add the table to PDF document
                iText_xls_2_pdf.add(my_table);                       
                iText_xls_2_pdf.close();                
                //we created our pdf file..
                input_document.close(); //close xlsx

}
}


case class ArgFields(
                    arg1:String = "",
                    arg2:String = "",
                    arg3:String = "",
                    arg4:String = "",
                    arg5:String = "",
                    arg6:String = "",
                    arg7:String = "",
                    arg8:String = "",
                    arg9:String = ""                    
)

/**
 * The basic application controller.
 *
 * @param messagesApi The Play messages API.
 * @param env The Silhouette environment.
 * @param socialProviderRegistry The social provider registry.
 */
class ApplicationController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, CookieAuthenticator],
  socialProviderRegistry: SocialProviderRegistry)
  extends Silhouette[User, CookieAuthenticator] {


val ArgFieldsForm = Form(mapping("arg1" -> text,
  "arg2" -> text,
  "arg3" -> text,
  "arg4" -> text,
  "arg5" -> text,
  "arg6" -> text,
  "arg7" -> text,
  "arg8" -> text,
  "arg9" -> text
)(ArgFields.apply)(ArgFields.unapply))


def passport = SecuredAction.async { implicit request => 

  Future.successful(Ok(views.html.passport(request.identity )))
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

  /**
   * Handles the index action.
   *
   * @return The result to display.
   */
  def index = SecuredAction.async { implicit request =>
    /*
    val test_result = Documents.documents.head.sheets.find(sheet => sheet.name == "page1").get
    val rows =  test_result.rows 
    val cells:List[RowCellView] = rows.toSeq.sortBy(_.index).map(row => 

      row.cells.toSeq.sortBy(_.index).map { cell =>

         cell match {
            case cell:StringCell => RowCellView(row.index, cell.index, cell.data)
            case _ => RowCellView(row.index, cell.index, "")
         }
         
      }
      ).flatten.toList
    */
    //val stringCells:scala.collection.immutable.Set[StringCell] = cells.filter(cell => 
    //                                      cell.isInstanceOf[StringCell]).map(_.asInstanceOf[StringCell])
    //val stringCellContent:List[String] = stringCells.toSeq.sortBy(_.index).map(cell => s"Колонка ${cell.index}: ${cell.data}").toList
//    val email = request.identity.email.getOrElse("test@empia.io")

//    com.journaldev.di.test.MainClass1.main2(Array(
//        "ТЕСТ","ТЕСТ","ТЕСТ","ТЕСТ","ТЕСТ","ТЕСТ","ТЕСТ","ТЕСТ", "ТЕСТ", email
//      ))
    Future.successful(Ok(views.html.home(request.identity, "", List(),ArgFieldsForm )))
  }

  def fillByAnchor() = SecuredAction.async { implicit request =>
    //val test_result = Documents.documents.head.sheets.head

/*
<h3>Фамилия</h3>
@cells.filter(cell => cell.rowNum == 13).map { cell =>  
        <span>@cell.data</span>    
} 
</div>
<div class="anchor-detail">
<h3>Имя</h3>
@cells.filter(cell => cell.rowNum == 15).map { cell =>
        <span>@cell.data</span>    
} 
</div>
<div class="anchor-detail">
<h3>Отчество</h3>
@cells.filter(cell => cell.rowNum == 17).map { cell =>
        <span>@cell.data</span>    
} 
*/

  ArgFieldsForm.bindFromRequest.fold(
        formWithErrors => {
          println(formWithErrors)
         Future.successful( Redirect(routes.ApplicationController.index) )

         },
        entity => {
            val email = request.identity.email.getOrElse("test@empia.io")
            com.journaldev.di.test.MainClass1.main2(Array(
                entity.arg1.toUpperCase(),
                entity.arg2.toUpperCase(),
                entity.arg3.toUpperCase(),
                entity.arg4.toUpperCase(),
                entity.arg5.toUpperCase(),
                entity.arg6.toUpperCase(),
                entity.arg7.toUpperCase(),
                entity.arg8.toUpperCase(),
                entity.arg9.toUpperCase(), email
              ))


            Future.successful(Ok(views.html.home(request.identity, "", List(), ArgFieldsForm.fill(entity) )))
        })

  }

  /**
   * Handles the Sign In action.
   *
   * @return The result to display.
   */
  def signIn = UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(user) => Future.successful(Redirect(routes.ApplicationController.index()))
      case None => Future.successful(Ok(views.html.signIn(SignInForm.form, socialProviderRegistry)))
    }
  }

  /**
   * Handles the Sign Up action.
   *
   * @return The result to display.
   */
  def signUp = UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(user) => Future.successful(Redirect(routes.ApplicationController.index()))
      case None => Future.successful(Ok(views.html.signUp(SignUpForm.form)))
    }
  }

  /**
   * Handles the Sign Out action.
   *
   * @return The result to display.
   */
  def signOut = SecuredAction.async { implicit request =>
    val result = Redirect(routes.ApplicationController.index())
    env.eventBus.publish(LogoutEvent(request.identity, request, request2Messages))

    env.authenticatorService.discard(request.authenticator, result)
  }
}
