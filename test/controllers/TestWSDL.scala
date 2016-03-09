/** @author giabao
  * created: 2013-10-30 10:13
  * (c) 2011-2013 sandinh.com
  */
package clersky.test
import com.sandinh.soap._
import com.sandinh.xml.{XmlReader, XmlWriter}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.ws.{WS => PlayWS, InMemoryBody}
import play.api.Play.current
import play.api.http.HeaderNames._
import scala.xml.NamespaceBinding
import org.slf4j.LoggerFactory

import com.sandinh.xml.{Xml, XmlReader, XmlWriter}
import com.sandinh.soap.DefaultImplicits._
import scala.xml.NodeSeq
import scala.concurrent.duration._
import play.api.test.{WithApplication, PlaySpecification}
import org.specs2.concurrent.ExecutionEnv

class WSSpec(implicit ee: ExecutionEnv) extends PlaySpecification {

  /** @see [[http://www.webservicex.net/CurrencyConvertor.asmx?op=ConversionRate]] */
  object CurrencyConvertor {
    case class Param(FromCurrency: String, ToCurrency: String)
    case class Result(ConversionRateResult: Double)

    implicit object ParamXmlW extends XmlWriter[Param] {
      def write(t: Param, base: NodeSeq): NodeSeq =
        <ConversionRate xmlns="http://www.webserviceX.NET/">
          <FromCurrency>{ t.FromCurrency }</FromCurrency>
          <ToCurrency>{ t.ToCurrency }</ToCurrency>
        </ConversionRate>
    }

    implicit object ResultXmlR extends XmlReader[Result] {
      def read(x: NodeSeq): Option[Result] =
        for {
          r <- (x \ "ConversionRateResponse").headOption
          rate <- Xml.fromXml[Double](r \ "ConversionRateResult")
        } yield Result(rate)
    }

    object WS11 extends SoapWS11[Param, Result](
      "http://www.webservicex.net/CurrencyConvertor.asmx",
      "http://www.webserviceX.NET/ConversionRate"
    )

    object WS12 extends SoapWS12[Param, Result]("http://www.webservicex.net/CurrencyConvertor.asmx")
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

  val timeOut = 2.minutes

  //  def xmlFromEscaped(s: String) = {
  //    import scala.xml.XML
  //    val x = XML.loadString(s"<foo>$s</foo>")
  //    XML.loadString(x.text)
  //  }

  "WS" should {
    "callable CurrencyConvertor" >> new WithApplication {
      import CurrencyConvertor._
      def test(ws: WS[Param, Result]) = ws.call(Param("USD", "RUB"))
        .map(_.ConversionRateResult) must greaterThan(10000D).awaitFor(timeOut)

      test(WS11)
      test(WS12)
    }.pendingUntilFixed("webservicex.net/CurrencyConvertor return -1 so this test is failed!")

    "callable GetCurrencyByCountry" >> new WithApplication {
      import GetCurrencyByCountry._
      def test(ws: WS[Param, Result]) = ws.call(Param("vietnam"))
        .map(r => scala.xml.XML.loadString(r.GetCurrencyByCountryResult))
        .map(x => Xml.fromXml[String]((x \ "Table").head \ "CurrencyCode")) must beSome("VND").awaitFor(timeOut)

      test(WS11)
      test(WS12)
    }
  }
}
