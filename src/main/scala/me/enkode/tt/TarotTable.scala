package me.enkode.tt

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import me.enkode.tt.es.{Sessions, EventServerRoutes}
import me.enkode.tt.http.Routeable
import me.enkode.tt.uis.UiServerRoutes


class TarotTable extends Routeable {
  val getRoot = (get & pathEndOrSingleSlash) {
    complete("Tarot Table")
  }

  def run(): Unit = {
    implicit val actorSystem = ActorSystem("TarotTable")
    implicit val actorMaterializer = ActorMaterializer()

    import actorSystem.dispatcher

    val (bindHost, bindPort) = {
      val config = actorSystem.settings.config.getConfig("tt.bind")
      (config.getString("host"), config.getInt("port"))
    }

    val sessions = Sessions()
    val routables = Seq(this, EventServerRoutes(sessions), UiServerRoutes())

    val route = routables.map(_.routes).reduce(_ ++ _).reduce(_ ~ _)

    val binding = Http().bindAndHandle(route, bindHost, bindPort)

    binding map { bound ⇒
      println(s"${classOf[TarotTable].getSimpleName} running at ${bound.localAddress}")
    }
  }

  override def routes = getRoot :: Nil
}

object TarotTable extends TarotTable with App {
  run()
}
