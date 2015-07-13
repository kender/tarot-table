package me.enkode.tt.uic

import org.scalajs.dom

import scala.scalajs.js.annotation.JSExport

@JSExport("TarotTableClient")
object TarotTableClient {
  import dom.ext.Ajax
  import dom._
  import scalajs.concurrent
  .JSExecutionContext
  .Implicits
  .queue


  @JSExport
  def run(root: dom.Element) = {
    val child = dom.document.createElement("p")
    child.appendChild(document.createTextNode("foo"))
    root.appendChild(child)
    Ajax.get("/events/session/ekJMKcNgTnO4NxIfS0dd1Q") map { req ⇒
      println(req.responseText)
    }
    println("oh yeah")
  }
}
