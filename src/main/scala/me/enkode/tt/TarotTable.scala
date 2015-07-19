package me.enkode.tt

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import me.enkode.tt.es.{SessionStoreActor, SessionStore, Sessions, SessionRoutes}
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

    val sessionStoreActorRef = actorSystem.actorOf(SessionStoreActor.props(), "sessionStore")
    val sessionStore = SessionStore(sessionStoreActorRef)
    val sessions = Sessions(sessionStore)
    val routables = Seq(this, SessionRoutes(sessions), UiServerRoutes())

    val route = routables.map(_.routes).reduce(_ ++ _).reduce(_ ~ _)

    val binding = Http().bindAndHandle(route, bindHost, bindPort)

    binding map { bound ⇒
      println(s"${classOf[TarotTable].getSimpleName} running at ${bound.localAddress}")
    }

    actorSystem registerOnTermination {
      binding.map(_.unbind())
    }
  }

  override def routes = getRoot :: Nil
}

object TarotTable extends TarotTable with App {
  run()
}
