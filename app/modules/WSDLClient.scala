package clersky

import com.sandinh.soap._
import com.sandinh.xml.{Xml, XmlReader, XmlWriter}
import com.sandinh.soap.DefaultImplicits._
import scala.xml.NodeSeq
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.mvc._
import play.api.libs.ws._


object MD5 {
  def hash(s: String) = {
    val m = java.security.MessageDigest.getInstance("MD5")
    val b = s.getBytes("UTF-8")
    m.update(b, 0, b.length)
    new java.math.BigInteger(1, m.digest()).toString(16)
  }
}

object RegFilling {
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









object GetPassword {
  case class Param(file: String)
  case class Result(ConversionRateResult: String)

  implicit object ParamXmlW extends XmlWriter[Param] {
    def write(t: Param, base: NodeSeq): NodeSeq =
    <GetPassword>
      <GUID>{ t.file }</GUID>
    </GetPassword>

 }



  implicit object ResultXmlR extends XmlReader[Result] {
    def read(x: NodeSeq): Option[Result] = {
      println(x)
      //for {
      //  r <- Xml.fromXml[String](x \ "SendPacketResponse")
      //  //rate <- Xml.fromXml[String](r \ "result")
      //} yield
      Some(Result(x.toString))
    }
  }



  object WS12 extends SoapWS12[Param, Result]("http://iotchet.ru/api/autorization")
}


 object GetSessionKeyBySMS {
   case class Param(GUID: String,Password: String)
   case class Result(ConversionRateResult: String)

   implicit object ParamXmlW extends XmlWriter[Param] {
     def write(t: Param, base: NodeSeq): NodeSeq =
         <GetSessionkeyBySMS xmlns="https://iotchet.ru/namespases">
   <GUID>{ t.GUID }</GUID>
   <Password>{ t.Password }</Password>
 </GetSessionkeyBySMS>
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


   object WS12 extends SoapWS12[Param, Result]("https://iotchet.ru/api/autorization?wsdl")
}

 object SendFilesFNS {
   case class Param(file: String)
   case class Result(ConversionRateResult: String)

   implicit object ParamXmlW extends XmlWriter[Param] {
     def write(t: Param, base: NodeSeq): NodeSeq =
<Send xmlns="https://iotchet.ru/namespases">
  <RegFiles>
    <RegFile>
      <Name>{ t.file }</Name>
      <Content>Pw==</Content>
      <Sessionkeys>
        <Sessionkey>Array</Sessionkey>
      </Sessionkeys>
      <SVDReg>Array</SVDReg>
    </RegFile>
  </RegFiles>
  <ReceiverFNS>Array</ReceiverFNS>
  <SenderType>IP</SenderType>
  <SignForList>Array</SignForList>
  <PrVisBum>1</PrVisBum>
  <ULNameFull>?</ULNameFull>
  <Email>?</Email>
</Send>

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



   object WS12 extends SoapWS12[Param, Result]("http://iotchet.ru/api/gos_reg?wsdl")
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
   //val path = play.Play.application().path().getAbsolutePath()


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

val officeGuid = "40FA0D16-3B54-4004-B9D3-3997C4C0CC91"
def uuid() = java.util.UUID.randomUUID.toString

def saveDoc(phoneBad: String,
            abnGuid: String,
            eMail: String,
            inn:String,
            shortName:String,
            postalAddress:String,
            locationAddress:String,
            snils:String,
            firstName:String,
            lastName:String,
            patronymic:String,
            passportType:String,
            passportSerial:String,
            passportNumber:String,
            passportDate:String,
            passportIssuedBy: String,
            region: String="",
            area: String="",
            city: String="",
            nasel: String = "",
            street: String="",
            house: String="",
            corpus: String="",
            appartment: String="",

            ws: WSClient
            ):String = {

val phone = {"8"+phoneBad.substring(1, phoneBad.length())}
val guid = uuid()
val dateTime = "08.03.2016 11:29:13"
val packetId = MD5.hash(abnGuid+"1")

val fileId = MD5.hash(abnGuid)
val document_id = MD5.hash(inn)
val fileName = s"${fileId}.bin"

//<postalAddress>{postalAddress}</postalAddress>       // index, region, area, g city, n nasel, uls ulitsa, dom, corpus, kv
//<locationAddress>{locationAddress}</locationAddress> // index, region, area, g city, n nasel, uls ulitsa, dom, corpus, kv

  val doc =
    <regRequest version="3.1" dateTime={dateTime} id={packetId}>
<type>1</type>
<abnType>3</abnType>
<officeGuid>{ officeGuid }</officeGuid>
<abnGuid>{ abnGuid }</abnGuid>
<category>1</category>
<inn>{inn}</inn>
<kpp></kpp>
<rnsPfr stPfr=""></rnsPfr>
<storageType>1</storageType>
<fullName>{inn}</fullName>
<phone>{phone}</phone>
<mobilePhone>{phone}</mobilePhone>
<shortName>{lastName} {firstName} {patronymic}</shortName>
<postalAddress>{postalAddress},{region},{area},{city},{nasel},{street},{house},{corpus},{appartment}</postalAddress>
<locationAddress>{postalAddress},{region},{area},{city},{nasel},{street},{house},{corpus},{appartment}</locationAddress>
<eMail>{eMail}</eMail>
<rnsFss></rnsFss>
<kpFss></kpFss>
<verifyKpp>0</verifyKpp>
<ogrn></ogrn>
<members>
<member>
<firstName>{firstName}</firstName>
<lastName>{lastName}</lastName>
<patronymic>{patronymic}</patronymic>
<passportType>01</passportType>
<passportSerial>{passportSerial}</passportSerial>
<passportNumber>{passportNumber}</passportNumber>
<passportDate>{passportDate}</passportDate>
<passportComment>{passportIssuedBy}</passportComment>
<signer>1</signer>
<encrypt>1</encrypt>
<certTemplate>3</certTemplate>
<post></post>
<orgUnit></orgUnit>
<snils>{snils}</snils>
<mobilePhone>{phone}</mobilePhone>
<eMail>{eMail}</eMail>
<signQualification></signQualification>
<provider type="75">CryptoPro HSM CSP</provider>
</member>
</members>
<plugins>
<plugin guid="46472FBA-069F-4D41-9E49-736DF914CCD1"></plugin>
</plugins>
</regRequest>



  val path = play.Play.application().path().getAbsolutePath()

  scala.xml.XML.save(s"${path}/doc_$phone.xml", doc, "UTF-8",xmlDecl = true)



  val packet =
<packet version="2.0" date={ dateTime } type="AbonentRegistration" transaction="Request" id={packetId}>
    <sender type="Agent" id={ officeGuid }/>
<recipient type="RegOffice" id="KalugaAstral"/>
<document id={ document_id } type="RegRequest" contentType="xml" compressed="true" encrypted="false">
<content fileName={fileName}/></document>
</packet>


  import sys.process._

  Seq(s"rm","-rf", s"${path}/doc_$phone").lineStream

  Seq(s"mkdir", s"${path}/doc_$phone").lineStream
  Seq(s"mv", s"${path}/doc_$phone.xml", s"${path}/doc_$phone/file").lineStream
  Seq(s"zip", "-j", s"${path}/doc_$phone/${fileId}.bin", s"${path}/doc_$phone/file").lineStream
  Seq(s"rm", s"${path}/doc_$phone/file").lineStream

  scala.xml.XML.save(s"${path}/doc_$phone/packetDescription.xml", packet, "UTF-8",xmlDecl = true)
  val zipout = Seq(s"zip", "-j", s"${path}/doc_$phone/test.zip", s"${path}/doc_$phone/packetDescription.xml",
    s"${path}/doc_$phone/${fileId}.bin").lineStream
  val out = (Seq("cat", s"${path}/doc_$phone/test.zip")  #| Seq("openssl", "base64")  #| Seq("tr", "-d", "'\n")).lineStream


  import scala.util.Random

  def uniqueRandomKey(chars: String, length: Int, uniqueFunc: String=>Boolean) : String =
  {
   val newKey = (1 to length).map(
     x =>
     {
       val index = Random.nextInt(chars.length)
       chars(index)
     }
    ).mkString("")

   if (uniqueFunc(newKey))
    newKey
   else
    uniqueRandomKey(chars, length, uniqueFunc)
  }

  /**
   * implement your own is unique here
   */
  def isUnique(s:String):Boolean = true

  val chars = ('a' to 'z') ++ ('A' to 'Z')
  //val key = uniqueRandomKey(chars.mkString(""), 8, isUnique)

  println(zipout)
  val packageId =  uniqueRandomKey(chars.mkString(""), 32, isUnique)


   // firstRequest(ws, out.head.toString)

  //  packet.toString
  //doc.toString + "        " + out.headOption.toString + "        " + 
  //test2(out.head.toString)
  out.head.toString
  // ИД Документоборота
  sendDocRequest(ws, out.head.toString)
  packetId
}


// getPassword
def sendDocRequest(ws: WSClient, packet:String):Future[String] = {

     val data = s"""
<x:Envelope xmlns:x="http://schemas.xmlsoap.org/soap/envelope/" xmlns:reg="http://regservice.keydisk.ru/">
    <x:Header/>
    <x:Body>
        <reg:SendPacket>
            <reg:packet>
              ${packet}
            </reg:packet>
        </reg:SendPacket>
    </x:Body>
</x:Envelope>
"""

     ws.url("http://regservice.keydisk.ru/regservice.asmx").withHeaders("Content-Type" -> "text-xml",
     "SOAPAction"->"urn:http://regservice.keydisk.ru/SendPacket")
     .post(data).map { r =>
       println(r)
       println(r.body)
       r.body.toString
     }
}

// getPassword
def firstRequest(ws: WSClient, packet:String):Future[String] = {

     val data = s"""
<x:Envelope xmlns:x="http://schemas.xmlsoap.org/soap/envelope/" xmlns:reg="http://regservice.keydisk.ru/">
    <x:Header/>
    <x:Body>
        <reg:SendPacket>
            <reg:packet>$packet</reg:packet>
        </reg:SendPacket>
    </x:Body>
</x:Envelope>
"""

     ws.url("http://regservice.keydisk.ru/regservice.asmx").withHeaders("Content-Type" -> "text-xml",
     "SOAPAction"->"urn:SendPacket")
     .post(data).map { r =>
       println(r)
       println(r.body)
       r.body.toString
     }
}



def test2(file: String):Future[String] = {
     import RegFilling._

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

import javax.inject.Inject
import scala.concurrent.Future


// getStatus
def getStatus(ws: WSClient, guid: String): Future[String] = {
  val data = s"""
  <x:Envelope xmlns:x="http://schemas.xmlsoap.org/soap/envelope/" xmlns:reg="http://regservice.keydisk.ru/">
    <x:Header/>
    <x:Body>
        <reg:ReceivePacket>
            <reg:packetId>$guid</reg:packetId>
        </reg:ReceivePacket>
    </x:Body>
</x:Envelope>
"""
     ws.url("http://regservice.keydisk.ru/regservice.asmx").withHeaders("Content-Type" -> "text/xml; charset=utf-8",
     "SOAPAction"->"http://regservice.keydisk.ru/ReceivePacket")
     .post(data).map { r =>
       println(r)
       println(r.body)
       r.body.toString
     }
}

// getPassword
def test3(ws: WSClient, guid:String):Future[String] = {
     val data = s"""
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ns1="tns">
  <SOAP-ENV:Body>
    <ns1:GetPassword>
      <ns1:GUID>$guid</ns1:GUID>
    </ns1:GetPassword>
  </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
"""
     ws.url("http://iotchet.ru/api/autorization").withHeaders("Content-Type" -> "text-xml",
     "SOAPAction"->"urn:GetPassword")
     .post(data).map { r =>
       println(r)
       println(r.body)
       r.body.toString
     }
}


def test4(ws: WSClient, guid:String, smsPass:String):Future[String] = {
     val data = s"""
     <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ns1="tns">
       <SOAP-ENV:Body>
         <ns1:GetSessionkeyBySMS>
           <ns1:GUID>$guid</ns1:GUID>
           <ns1:Password>$smsPass</ns1:Password>
         </ns1:GetSessionkeyBySMS>
       </SOAP-ENV:Body>
     </SOAP-ENV:Envelope>
"""
     ws.url("http://iotchet.ru/api/autorization").withHeaders("Content-Type" -> "text-xml",
     "SOAPAction"->"urn:GetSessionkeyBySMS")
     .post(data).map { r =>
       println(r)
       println(r.body)
       r.body.toString
     }
}



def sendFiles(ws: WSClient, sessionKey: String, fullName: String, phone: String): Future[String] = {
  /*
                  <nam:RegFile>
                    <nam:Name>Р21001.tif</nam:Name>
                    <nam:Content>${file1}</nam:Content>
                    <nam:Sessionkeys>
                        <nam:Sessionkey>${sessionKey}</nam:Sessionkey>
                    </nam:Sessionkeys>
                    <nam:SVDReg>011011</nam:SVDReg>
                </nam:RegFile>
                */
//"openssl base64 -in files/doc_${phone}/P21001/Р21001.tif -out files/doc_${phone}/P21001/Р21001.tif.base64"
//"openssl base64 -in files/doc_${phone}/PASSPORT/Паспорт.tif -out files/doc_${phone}/PASSPORT/Паспорт.tif.base64"
//"openssl base64 -in files/doc_${phone}/POSHLINA/Пошлина.tif -out files/doc_${phone}/POSHLINA/Пошлина.tif.base64"
//"openssl base64 -in files/doc_${phone}/USN/УСН.tif -out files/doc_${phone}/USN/УСН.tif.base64"
val path = play.Play.application().path().getAbsolutePath()+"/public"

import sys.process._
val out1 = (Seq("openssl", "base64", "-in", s"${path}/files/doc_${phone}/P21001/Р21001.tif",
                                      "-out", s"${path}/files/doc_${phone}/P21001/Р21001.tif.base64")).lineStream
val out2 = (Seq("openssl", "base64", "-in", s"${path}/files/doc_${phone}/PASSPORT/Паспорт.tif",
                                      "-out", s"${path}/files/doc_${phone}/PASSPORT/Паспорт.tif.base64")).lineStream
val out3 = (Seq("openssl", "base64", "-in", s"${path}/files/doc_${phone}/POSHLINA/Пошлина.tif",
                                      "-out", s"${path}/files/doc_${phone}/POSHLINA/Пошлина.tif.base64")).lineStream
val out4 = (Seq("openssl", "base64", "-in", s"${path}/files/doc_${phone}/USN/УСН.tif",
                                      "-out", s"${path}/files/doc_${phone}/USN/УСН.tif.base64")).lineStream


val file1_content = (Seq("cat", s"${path}/files/doc_${phone}/P21001/Р21001.tif.base64")).lineStream.head.toString
val file2_content = (Seq("cat", s"${path}/files/doc_${phone}/PASSPORT/Паспорт.tif.base64")).lineStream.head.toString
val file3_content = (Seq("cat", s"${path}/files/doc_${phone}/POSHLINA/Пошлина.tif.base64")).lineStream.head.toString
val file4_content = (Seq("cat", s"${path}/files/doc_${phone}/USN/УСН.tif.base64")).lineStream.head.toString         

  val file1:String = s"""
<nam:RegFile>
<nam:Name>Р21001.tif</nam:Name>
<nam:Content>${file1_content}</nam:Content>
<nam:Sessionkeys>
    <nam:Sessionkey>${sessionKey}</nam:Sessionkey>
</nam:Sessionkeys>
<nam:SVDReg>011011</nam:SVDReg>
</nam:RegFile>
  """     
  val file2:String = s"""
<nam:RegFile>
<nam:Name>УСН.tif</nam:Name>
<nam:Content>${file2_content}</nam:Content>
<nam:Sessionkeys>
    <nam:Sessionkey>${sessionKey}</nam:Sessionkey>
</nam:Sessionkeys>
<nam:SVDReg>020111</nam:SVDReg>
</nam:RegFile>
  """     
  val file3:String = s"""
<nam:RegFile>
<nam:Name>Паспорт.tif</nam:Name>
<nam:Content>${file3_content}</nam:Content>
<nam:Sessionkeys>
    <nam:Sessionkey>${sessionKey}</nam:Sessionkey>
</nam:Sessionkeys>
<nam:SVDReg>022011</nam:SVDReg>
</nam:RegFile>
  """     
  val file4:String = s"""
<nam:RegFile>
<nam:Name>Пошлина.tif</nam:Name>
<nam:Content>${file4_content}</nam:Content>
<nam:Sessionkeys>
    <nam:Sessionkey>${sessionKey}</nam:Sessionkey>
</nam:Sessionkeys>
<nam:SVDReg>020001</nam:SVDReg>
</nam:RegFile>
  """                    
  val files:String = List(file1,file2,file3,file4).mkString("")

  val data = s"""
  <x:Envelope xmlns:x="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tns="tns" xmlns:nam="https://iotchet.ru/namespases">
    <x:Header/>
    <x:Body>
        <tns:Send>
            <tns:RegFiles>
                ${files}
            </tns:RegFiles>
            <tns:ReceiverFNS>0000</tns:ReceiverFNS>
            <tns:SenderType>IP</tns:SenderType>
            <tns:SignForList>${sessionKey}</tns:SignForList>
            <tns:PrVisBum>1</tns:PrVisBum>
            <tns:ULNameFull>ИП ${fullName}</tns:ULNameFull>
            <tns:Email>gett.mail@ya.ru</tns:Email>
        </tns:Send>
    </x:Body>
</x:Envelope>
"""


     ws.url("http://iotchet.ru/api/gos_reg").withHeaders("Content-Type" -> "text-xml",
     "SOAPAction"->"GetSessionkeyBySMS")
     .post(data).map { r =>
       println(r)
       println(r.body)
       r.body.toString
     }
}


def test() {
     import GetCurrencyByCountry._
     def test(ws: WS[Param, Result]) = ws.call(Param("vietnam"))
       .map(r => scala.xml.XML.loadString(r.GetCurrencyByCountryResult))
       .map(x => Xml.fromXml[String]((x \ "Table").head \ "CurrencyCode")) //must beSome("VND").awaitFor(timeOut)

     // TEMP ATTEMPTS test(WS11)//.map(println)
     // TEMP ATTEMPTS test(WS12)//.map(println)
   }
 }
