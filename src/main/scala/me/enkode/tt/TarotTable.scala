package me.enkode.tt

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._


class TarotTable {
  def run(): Unit = {
    implicit val actorSystem = ActorSystem("TarotTable")
    implicit val actorMaterializer = ActorMaterializer()

    import actorSystem.dispatcher

    val route = (get & pathEndOrSingleSlash) {
      complete("OK")
    }

    val binding = Http().bindAndHandle(route, "0.0.0.0", 8080)

    binding map { bound ⇒
      println(s"bound: ${bound.localAddress}")
    }
  }
}

object TarotTable extends TarotTable with App {
  run()
}
