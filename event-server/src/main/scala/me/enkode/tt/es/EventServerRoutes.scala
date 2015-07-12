package me.enkode.tt.es

import akka.http.scaladsl.server._
import me.enkode.tt.http.Routeable

class EventServerRoutes extends Routeable with Directives {
  val pollSessionChannel: Route = {
    (get & path("events" / "session" / Segment / "channel" / Segment)) { (sessionId, channelId) ⇒
      complete(s"$sessionId $channelId")
    }
  }

  override def routes = pollSessionChannel :: Nil
}
