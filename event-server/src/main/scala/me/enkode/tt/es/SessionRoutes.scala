package me.enkode.tt.es

import java.time.Instant
import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import me.enkode.tt.http._

import me.enkode.tt.models._

import upickle.default._

trait SessionRoutes extends Routeable with Directives
  with JavaTimeMarshalling with UuidUtils
  with μPickleSupport {

  def sessions: Sessions

  implicit val actorSystem: ActorSystem
  implicit val materializer = ActorMaterializer()

  val pollSessionChannel: Route = {
    (get & path("session" / JavaUUID ) & parameter('since.as[Instant].?)) { (sessionId, since) ⇒
      onSuccess(sessions.find(sessionId, since)) {
        case None ⇒ complete(StatusCodes.NotFound)
        case Some(session) ⇒ complete(session)
      }
    }
  }

  val instantiateAsset: Route = {
    (post & path("session" / JavaUUID / "asset" ) & entity(as[AssetInstance])) { (sessionId, assetInstance) ⇒
      onSuccess(sessions.addAssetInstance(sessionId, assetInstance)) {
        case None ⇒ complete(StatusCodes.NotFound)
        case Some(session) ⇒ complete(session)
      }
    }
  }

  val clearSession: Route = {
    (post & path("session" / JavaUUID / "clear")) { (sessionId) ⇒
      onSuccess(sessions.clear(sessionId)) {
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

  override def routes = pollSessionChannel :: clearSession :: instantiateAsset :: nextUuid :: Nil
}

object SessionRoutes {
  class SessionRoutesImpl(val sessions: Sessions)(implicit val actorSystem: ActorSystem) extends SessionRoutes
  def apply(sessions: Sessions)(implicit actorSystem: ActorSystem): SessionRoutes = new SessionRoutesImpl(sessions)
}
