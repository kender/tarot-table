package me.enkode.tt.es

import java.time.Instant
import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server._
import me.enkode.tt.http._
import me.enkode.tt.models.{UuidUtils, SerializationSupport}

trait EventServerRoutes extends Routeable with Directives
  with JavaTimeMarshalling with UuidUtils
  with μPickleSupport with SerializationSupport {

  def sessions: Sessions

  val pollSessionChannel: Route = {
    (get & path("events" / "session" / Segment ) & parameter('since.as[Instant].?)) { (sessionId, since) ⇒
      onSuccess(sessions.find(fromBase64(sessionId), since)) {
        case None ⇒ complete(StatusCodes.NotFound)
        case Some(session) ⇒ complete(session)
      }
    }
  }

  val nextUuid: Route = {
    (get & path("uuid")) {
      complete(UUID.randomUUID())
    }
  }

  override def routes = pollSessionChannel :: nextUuid :: Nil
}

object EventServerRoutes {
  class EventServerRoutesImpl(val sessions: Sessions) extends EventServerRoutes
  def apply(sessions: Sessions): EventServerRoutes = new EventServerRoutesImpl(sessions)
}
