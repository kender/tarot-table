package me.enkode.tt.uis

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import me.enkode.tt.http.Routeable

trait UiServerRoutes extends Routeable {
  val getResource: Route = (get & path("ui" / RestPath)) { resource ⇒
    getFromResource(s"content/$resource")
  }

  val getJavaScript: Route = (get & path("js"/ RestPath)) { resource ⇒
    getFromResource(s"js/$resource")
  }

  override def routes = getResource :: getJavaScript :: Nil
}

object UiServerRoutes {
  class UiServerRoutesImpl() extends UiServerRoutes
  def apply(): UiServerRoutes = new UiServerRoutesImpl()
}
