package me.enkode.tt.es

import java.time.Instant

import akka.http.scaladsl.server._
import me.enkode.tt.http.{JavaTimeMarshalling, Routeable}

trait EventServerRoutes extends Routeable with Directives with JavaTimeMarshalling {
  def sessions: Sessions

  val pollSessionChannel: Route = {
    (get & path("events" / "session" / Segment / "channel" / Segment) & parameter('since.as[Instant].?)) { (sessionId, channelId, since) ⇒
      complete(s"$sessionId $channelId $since")
    }
  }

  override def routes = pollSessionChannel :: Nil
}

object EventServerRoutes {
  class EventServerRoutesImpl(val sessions: Sessions) extends EventServerRoutes
  def apply(sessions: Sessions): EventServerRoutes = new EventServerRoutesImpl(sessions)
}
