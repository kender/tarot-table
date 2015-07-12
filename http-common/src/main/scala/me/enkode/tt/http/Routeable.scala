package me.enkode.tt.http

import akka.http.scaladsl.server.Route

trait Routeable {
  def routes: Seq[Route]


}
