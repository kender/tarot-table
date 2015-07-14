package me.enkode.tt.uic

import org.scalajs.dom

import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.JSExport

@JSExport("TarotTableClient")
object TarotTableClient {
  import dom.ext.Ajax
  import dom._
  import scalajs.concurrent
  .JSExecutionContext
  .Implicits
  .queue

  def generateUuid(): dom.Element = {
    val input = document.createInput("uuid")
      .styles(_.fontFamily = "monospace", _.width = "23ch", _.color = "blue")

    val button = document.createButton("generate") { e ⇒
      Ajax.get("/uuid") map { req ⇒
        input.setAttribute("value", req.responseText)
      }
    }

    document.createElement("div").appendChildren(input, button)
  }

  @JSExport
  def run(root: dom.Element) = {
    root.appendChild(generateUuid())
    Ajax.get("/events/session/ekJMKcNgTnO4NxIfS0dd1Q") map { req ⇒
      console.log(JSON.parse(req.responseText))
    }
  }
}
