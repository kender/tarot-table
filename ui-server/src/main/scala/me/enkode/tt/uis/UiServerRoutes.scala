package me.enkode.tt.uis

import me.enkode.tt.http.Routeable
import akka.http.scaladsl.server.Directives._

class UiServerRoutes extends Routeable {
  val getResource = (get & path("ui" / "resource" / RestPath)) { resource ⇒
    complete(resource.toString())
  }

  override def routes = getResource :: Nil
}
