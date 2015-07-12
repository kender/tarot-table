package me.enkode.tt.http

import java.time.Instant

import akka.http.scaladsl.unmarshalling._

import scala.concurrent.{Future, ExecutionContext}
import scala.util.Try

trait JavaTimeMarshalling {

  implicit object InstantFromIntUnmarshaller extends Unmarshaller[Long, Instant] {
    override def apply(value: Long)(implicit ec: ExecutionContext) = Future { Instant.ofEpochMilli(value) }
  }
  implicit object InstantFromStringUnmarshaller extends FromStringUnmarshaller[Instant] {
    override def apply(value: String)(implicit ec: ExecutionContext) = Future {
      Try {
        Instant.ofEpochMilli(value.toLong)
      } getOrElse {
        Instant.parse(value)
      }
    }
  }


}

object JavaTimeMarshalling extends JavaTimeMarshalling
