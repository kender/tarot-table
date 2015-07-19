package me.enkode.tt.uic

import org.scalajs.dom.XMLHttpRequest
import upickle.default._

import scala.concurrent.Future

abstract class XhrException(statusCode: Int, statusText: String) extends RuntimeException(s"$statusCode: $statusText")

case class ClientError(statusCode: Int, statusText: String) extends XhrException(statusCode, statusText)
case class ServerError(statusCode: Int, statusText: String) extends XhrException(statusCode, statusText)

trait ServiceHelpers {
  import scalajs.concurrent.JSExecutionContext.Implicits.queue

  def xhrResult[R : Reader](xhr: XMLHttpRequest): Future[R] = {
    if (xhr.status / 100 == 4) Future.failed(ClientError(xhr.status, xhr.statusText))
    else if (xhr.status / 100 == 5) Future.failed(ServerError(xhr.status, xhr.statusText))
    else Future {
      read[R](xhr.responseText)
    }
  }
}
