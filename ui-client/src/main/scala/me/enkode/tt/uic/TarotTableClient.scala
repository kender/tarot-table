package me.enkode.tt.uic

import java.util.UUID

import me.enkode.tt.models._
import me.enkode.tt.uic.assets.PogAsset
import org.scalajs.dom

import scala.scalajs.js.annotation.JSExport
import upickle.default._

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
      .styles(_.fontFamily = "monospace", _.width = "40ch", _.color = "blue")

    val button = document.createButton("generate") { e ⇒
      Ajax.get("/uuid") map { req ⇒
        input.setAttribute("value", req.responseText)
      }
    }

    document.createElement("div").appendChildren(input, button)
  }

  def testButton(): dom.Element = {
    val button = document.createButton("test") { e ⇒
      val pog = new PogAsset()
      val assetInstance = AssetInstance(UUID.randomUUID(), pog.id, AssetInstanceState(pog.initialState))
      Ajax.post(
        "/session/43706511-4C45-4F3E-9A85-0E19B15494FD/asset",
        write(assetInstance),
        headers = Map("content-type" → "application/json")) map { xhr ⇒
        val session = read[Session](xhr.responseText)
        console.log(session.toString)
      }
    }

    document.createElement("div").appendChildren(button)
  }

  @JSExport
  def run(root: dom.Element) = {
    root.appendChild(generateUuid())
    root.appendChild(testButton())
    Ajax.get("/session/43706511-4C45-4F3E-9A85-0E19B15494FD") map { xhr ⇒
      val session = read[Session](xhr.responseText)
      console.log(session.toString)
    }
  }
}
