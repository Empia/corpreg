package clersky

import com.sandinh.soap._
import com.sandinh.xml.{Xml, XmlReader, XmlWriter}
import com.sandinh.soap.DefaultImplicits._
import scala.xml.NodeSeq
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/** @see [[http://www.webservicex.net/CurrencyConvertor.asmx?op=ConversionRate]] */
 object CurrencyConvertor {
   case class Param(file: String)
   case class Result(ConversionRateResult: String)

   implicit object ParamXmlW extends XmlWriter[Param] {
     def write(t: Param, base: NodeSeq): NodeSeq =
       <SendPacket xmlns="http://regservice.keydisk.ru/">
         <packet>
         { t.file }
         </packet>       </SendPacket>
  }



   implicit object ResultXmlR extends XmlReader[Result] {
     def read(x: NodeSeq): Option[Result] = {
       println(x)
       for {
         r <- Xml.fromXml[String](x \ "SendPacketResponse")
         //rate <- Xml.fromXml[String](r \ "result")
       } yield Result(r)
     }
   }

   object WS11 extends SoapWS11[Param, Result](
     "http://www.webservicex.net/CurrencyConvertor.asmx",
     "http://www.webserviceX.NET/ConversionRate"
   )

   object WS12 extends SoapWS12[Param, Result]("http://regservice.keydisk.ru/regservice.asmx")
 }

 object GetCurrencyByCountry {
   case class Param(CountryName: String)
   case class Result(GetCurrencyByCountryResult: String)

   implicit object ParamXmlW extends XmlWriter[Param] {
     def write(t: Param, base: NodeSeq): NodeSeq =
       <GetCurrencyByCountry xmlns="http://www.webserviceX.NET">
         <CountryName>{ t.CountryName }</CountryName>
       </GetCurrencyByCountry>
   }

   implicit object ResultXmlR extends XmlReader[Result] {
     def read(x: NodeSeq): Option[Result] =
       for {
         r <- (x \ "GetCurrencyByCountryResponse").headOption
         s <- Xml.fromXml[String](r \ "GetCurrencyByCountryResult")
       } yield Result(s)
   }

   object WS11 extends SoapWS11[Param, Result](
     "http://www.webservicex.net/country.asmx",
     "http://www.webserviceX.NET/GetCurrencyByCountry"
   )

   object WS12 extends SoapWS12[Param, Result]("http://www.webservicex.net/country.asmx")
 }

 object WSDLTest {

 val timeOut = 2.minutes

 //  def xmlFromEscaped(s: String) = {
 //    import scala.xml.XML
 //    val x = XML.loadString(s"<foo>$s</foo>")
 //    XML.loadString(x.text)
 //  }

// "WS" should {
//   "callable CurrencyConvertor" >> new WithApplication {
import scala.xml._
import scala.concurrent.Future

def saveDoc(phone: String):String = {
  val portfolio =
  <portfolio>
    <stocks>
      <stock>AAPL</stock>
      <stock>AMZN</stock>
      <stock>GOOG</stock>
    </stocks>
    <reits>
      <reit>Super REIT 1</reit>
    </reits>
  </portfolio>
  val doc =
    <regRequest version="3.1" dateTime="08.03.2016 11:29:13" id="0f6ecc4dfb9d46608934bd7f130fa39f">
    <type>1</type>
    <abnType>1</abnType>
    <officeGuid>40FA0D16-3B54-4004-B9D3-3997C4C0CC91</officeGuid>
    <category>1</category>
    <inn>9999936973</inn>
    <kpp>999901001</kpp>
    <rnsPfr stPfr="1">099-999-111111</rnsPfr>
    <storageType>1</storageType>
    <fullName>Общество с ограниченной ответственностью "Тест"</fullName>
    <phone>89000000000</phone>
    <mobilePhone>89000000000</mobilePhone>
    <shortName>ООО "Тест from clersky"</shortName>
    <postalAddress>248000,40,,Калуга,,,,,</postalAddress>
    <locationAddress>248000,40,,Калуга,,,,,</locationAddress>
    <eMail>test@astralnalog.ru</eMail>
    <rnsFss>4000000000</rnsFss>
    <kpFss>4000</kpFss>
    <verifyKpp>0</verifyKpp>
    <ogrn>1111111111111</ogrn>
    <members><member>
    <firstName>Иван</firstName>
    <lastName>Иванов</lastName>
    <patronymic>Иванович</patronymic>
    <passportType>01</passportType>
    <passportSerial>1111</passportSerial>
    <passportNumber>111111</passportNumber>
    <passportDate>11.11.2011</passportDate>
    <passportComment>111-111 ОТДЕЛЕНИЕМ №3 ОТДЕЛА ПАСПОРТНО-ВИЗОВОЙ СЛУЖБЫ УВД ГОРОДА</passportComment>
    <signer>1</signer>
    <encrypt>1</encrypt>
    <certTemplate>3</certTemplate>
    <post>НаименованиеДолжности</post>
    <orgUnit>НазваниеПодразделения</orgUnit>
    <snils>000-000-000 00</snils>
    <mobilePhone>89000000000</mobilePhone>
    <eMail>test@astralnalog.ru</eMail>
    <signQualification>1</signQualification>
    </member></members>
    <plugins>
    <plugin guid="46472FBA-069F-4D41-9E49-736DF914CCD1"></plugin>
    </plugins>
    </regRequest>

  scala.xml.XML.save(s"doc_$phone.xml", doc, "UTF-8",xmlDecl = true)

  val fileId = "83e44db34b3645478d74702624090c7b"

  val packet =
    <packet version="2.0" date="08.03.2016 11:29:13" type="AbonentRegistration" transaction="Request" id="8d74e89d0c6642d8adb32d400301edaf">
    <sender type="Agent" id="40FA0D16-3B54-4004-B9D3-3997C4C0CC91"/>
    <recipient type="RegOffice" id="KalugaAstral"/>
    <document id="0f6ecc4dfb9d46608934bd7f130fa39f" type="RegRequest" contentType="xml" compressed="true" encrypted="false">
      <content fileName="{fileid}.bin"/>
    </document>
    </packet>


  import sys.process._


  Seq(s"mkdir", s"doc_$phone").lineStream
  Seq(s"mv", s"doc_$phone.xml", s"doc_$phone/file").lineStream
  Seq(s"zip", s"doc_$phone/${fileId}.bin", s"doc_$phone/file").lineStream
  Seq(s"rm", s"doc_$phone/file").lineStream

  scala.xml.XML.save(s"doc_$phone/packetDescription.xml", packet, "UTF-8",xmlDecl = true)
  val zipout = Seq(s"zip", s"doc_$phone/test.zip", s"doc_$phone/packetDescription.xml",s"doc_$phone/${fileId}.bin").lineStream
  val out = (Seq("cat", s"doc_$phone/test.zip")  #| Seq("openssl", "base64")  #| Seq("tr", "-d", "'\n")).lineStream

println(zipout)

  out.head.toString

}

def test2():Future[String] = {
     import CurrencyConvertor._
     val file:String = "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"
          def test(ws: WS[Param, Result]):Future[String] = ws.call(Param(file))
       .map { c =>
         println(c)
         println(XML.loadString(c.ConversionRateResult) \\ "errorMessage")
         (XML.loadString(c.ConversionRateResult) \\ "errorMessage").text
       }
          //must greaterThan(10000D).awaitFor(timeOut)

     //test(WS11).map(println)
     test(WS12)//.map(println)
}
   //}.pendingUntilFixed("webservicex.net/CurrencyConvertor return -1 so this test is failed!")

  // "callable GetCurrencyByCountry" >> new WithApplication {
def test() {
     import GetCurrencyByCountry._
     def test(ws: WS[Param, Result]) = ws.call(Param("vietnam"))
       .map(r => scala.xml.XML.loadString(r.GetCurrencyByCountryResult))
       .map(x => Xml.fromXml[String]((x \ "Table").head \ "CurrencyCode")) //must beSome("VND").awaitFor(timeOut)

     test(WS11)//.map(println)
     test(WS12)//.map(println)
   }
 }
