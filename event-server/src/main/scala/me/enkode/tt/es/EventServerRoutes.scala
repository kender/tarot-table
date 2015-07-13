package me.enkode.tt.es

import java.time.Instant
import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server._
import me.enkode.tt.http.{UuidUtils, JavaTimeMarshalling, Routeable}

import scala.util.{Failure, Success}

trait EventServerRoutes extends Routeable with Directives with JavaTimeMarshalling with UuidUtils with SprayJsonSupport {
  def sessions: Sessions

  val pollSessionChannel: Route = {
    (get & path("events" / "session" / Segment ) & parameter('since.as[Instant].?)) { (sessionId, since) ⇒
      onComplete(sessions.find(fromBase64(sessionId), since)) {
        case Success(None) ⇒ complete(StatusCodes.NotFound)
        case Success(Some(session)) ⇒ complete(session)
        case Failure(t) ⇒ complete(StatusCodes.InternalServerError, t)
      }
    }
  }

  val nextUuid: Route = {
    (get & path("uuid")) {
      complete(toBase64(UUID.randomUUID()))
    }
  }

  override def routes = pollSessionChannel :: nextUuid :: Nil
}

object EventServerRoutes {
  class EventServerRoutesImpl(val sessions: Sessions) extends EventServerRoutes
  def apply(sessions: Sessions): EventServerRoutes = new EventServerRoutesImpl(sessions)
}
