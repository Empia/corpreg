package com.sandinh.soap

import scala.language.implicitConversions
import scala.xml.{Elem, NamespaceBinding}
import com.sandinh.xml._
import org.slf4j.LoggerFactory

object SOAP extends SOAP

trait SOAP {
  def toSoap[T](t: T, ns: NamespaceBinding = SoapNS)(implicit w: XmlWriter[T]): xml.Elem =
    DefaultImplicits.SoapEnvelopeWriter[T](w).write(SoapEnvelope(t)(ns), xml.NodeSeq.Empty).asInstanceOf[Elem]

  def fromSOAP[T](x: xml.NodeSeq)(implicit r: XmlReader[T]): Option[T] =
    DefaultImplicits.SoapEnvelopeReader[T](r).read(x) match {
      case Some(SoapEnvelope(t)) => Some(t)
      case None                  => None
    }

  val SoapNS = NamespaceBinding("soapenv", "http://schemas.xmlsoap.org/soap/envelope/", xml.TopScope)
  val SoapNS12 = NamespaceBinding("soapenv", "http://schemas.xmlsoap.org/soap/envelope/", xml.TopScope)
}

case class SoapEnvelope[T](t: T)(implicit _namespace: NamespaceBinding = SOAP.SoapNS) {
  def namespace = _namespace
}

case class SoapFault[T](
  faultcode:   String,
  faultstring: String,
  faultactor:  String,
  detail:      T
)

object SoapFault {
  object FaultCode {
    /** Found an invalid namespace for the SOAP Envelope element */
    val VersionMismatch = "SOAP-ENV:VersionMismatch"
    /** An immediate child element of the Header element, with the mustUnderstand attribute set to "1", was not understood */
    val MustUnderstand = "SOAP-ENV:MustUnderstand"
    /** The message was incorrectly formed or contained incorrect information */
    val Client = "SOAP-ENV:Client"
    /** There was a problem with the server so the message could not proceed */
    val Server = "SOAP-ENV:Server"
  }
}

object DefaultImplicits extends DefaultSOAPFormatters with BasicReaders with SpecialReaders with BasicWriters with SpecialWriters

trait DefaultSOAPFormatters {
  private lazy val logger = LoggerFactory getLogger getClass.getName

  implicit def SoapEnvelopeReader[T](implicit reader: XmlReader[T]): XmlReader[SoapEnvelope[T]] = new XmlReader[SoapEnvelope[T]] {
    def read(x: xml.NodeSeq): Option[SoapEnvelope[T]] = {
      x.collectFirst { case x: xml.Elem if x.label == "Envelope" => x }
        .flatMap { env =>
          (env \ "Body").headOption.flatMap { body =>
            reader.read(body).map { t =>
              SoapEnvelope(t)(env.scope)
            }
          }
        }
    }
  }

  implicit def SoapEnvelopeWriter[T](implicit fmt: XmlWriter[T]): XmlWriter[SoapEnvelope[T]] = new XmlWriter[SoapEnvelope[T]] {
    /** @param base not used */
    def write(st: SoapEnvelope[T], base: xml.NodeSeq) = {
      val env =
        <soapenv:Envelope>
          <soapenv:Header/>
          <soapenv:Body>{ Xml.toXml(st.t) }</soapenv:Body>
        </soapenv:Envelope>
      env.copy(scope = st.namespace)
    }
  }

  implicit def SoapFaultReader[T](implicit fmt: XmlReader[T], strR: XmlReader[String]): XmlReader[SoapFault[T]] = new XmlReader[SoapFault[T]] {
    def read(x: xml.NodeSeq): Option[SoapFault[T]] = {
      val envelope = x \\ "Fault"
      envelope.headOption.flatMap[SoapFault[T]]({ elt =>
        (
          strR.read(elt \ "faultcode"), strR.read(elt \ "faultstring"), strR.read(elt \ "faultactor"), fmt.read(elt \ "detail")
        ) match {
            case (None, _, _, _) =>
              logger.debug("Code part missing in SOAP Fault"); None
            case (_, None, _, _) =>
              logger.debug("Message part missing in SOAP Fault"); None
            case (_, _, None, _) =>
              logger.debug("Actor part missing in SOAP Fault"); None
            case (_, _, _, None) =>
              logger.debug("Detail part missing in SOAP Fault"); None
            case (Some(code), Some(msg), Some(actor), Some(detail)) => Some(SoapFault(code, msg, actor, detail))
            case _ => None
          }
      })
    }
  }

  implicit def SoapFaultWriter[T](implicit fmt: XmlWriter[T]): XmlWriter[SoapFault[T]] = new XmlWriter[SoapFault[T]] {
    def write(fault: SoapFault[T], base: xml.NodeSeq) =
      <soapenv:Fault>
        <faultcode>{ fault.faultcode }</faultcode>
        <faultstring>{ fault.faultstring }</faultstring>
        <faultactor>{ fault.faultactor }</faultactor>
        <detail>{ Xml.toXml(fault.detail) }</detail>
      </soapenv:Fault>
  }
}
