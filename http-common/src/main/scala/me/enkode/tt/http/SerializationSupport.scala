package me.enkode.tt.http

import java.nio.ByteBuffer
import java.time.Instant
import java.util.{Base64, UUID}

import spray.json.{JsNumber, JsString, JsValue, JsonFormat}

import scala.xml.{XML, NodeSeq}

trait SerializationSupport {
  import spray.json.DefaultJsonProtocol._

  implicit object UuidJsonFormat extends JsonFormat[UUID] with UuidUtils {
    override def read(json: JsValue) = {
      fromBase64(json.convertTo[String])
    }

    override def write(uuid: UUID) = {
      JsString(toBase64(uuid))
    }
  }

  implicit object InstantFormat extends JsonFormat[Instant] {
    override def read(json: JsValue) = Instant.ofEpochMilli(json.convertTo[Long])
    override def write(obj: Instant) = JsNumber(obj.toEpochMilli)
  }

  implicit object NodeSeqFormat extends JsonFormat[NodeSeq] {
    override def read(json: JsValue) = XML.load(json.convertTo[String])
    override def write(obj: NodeSeq) = JsString(obj.toString())
  }
}

object SerializationSupport extends SerializationSupport