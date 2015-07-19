package me.enkode.tt.uic

import java.util.UUID

import me.enkode.tt.models._
import me.enkode.tt.uic.assets.{PogRenderer, Pog}
import org.scalajs.dom._

import scala.util.control.NonFatal

class SessionView(sessionId: UUID) {
  import scalajs.concurrent
  .JSExecutionContext
  .Implicits
  .queue

  def spawn(instance: AssetInstance): Unit = {
    console.log(s"spawning: $instance")
    SessionService.instantiateAsset(sessionId, instance).map(refresh)
  }

  def onClick(e: MouseEvent): Unit = {
    val x = e.pageX - field.offsetLeft
    val y = e.pageY - field.offsetTop
    val at = Position(x, y)
    val instance = Pog.instantiate()
    val move = Pog.onMove(instance.assetInstanceState.current, at)
    spawn(instance.updateState(move))
  }

  def refresh(session: Session): Unit = {
    console.log(s"refreshing session: $session")
    val instances = document.getElementsByClassName("assetInstance")
    val instanceIds = session.assetInstances.map(_.assetInstanceId)
    (0 until instances.length).map(instances.item).foreach { item ⇒
      try {
        val id = item.asInstanceOf[Element].id
        if (!(instanceIds contains id.uuid)) {
          item.parentNode.removeChild(item)
        }
      } catch {
        case NonFatal(t) ⇒ console.log(t.toString)
      }
    }

    session.assetInstances foreach { assetInstance ⇒
      console.log(s"drawing: $assetInstance")
      if (assetInstance.assetId == Pog.id) {
        try {
          console.log("drawing a pog")
          val currentState = assetInstance.assetInstanceState.current
          val x = currentState.findInt("position_x").getOrElse(0)
          val y = currentState.findInt("position_y").getOrElse(0)
          document.removeElementById(assetInstance.assetInstanceId)
          val pog = PogRenderer.render(Pog, assetInstance)
          pog.classList.add("assetInstance")
          pog.styles(_.position="absolute", _.left = s"${x}px", _.top = s"${y}px")
          pog.setAttributes(Map("id" → assetInstance.assetInstanceId.toString))
          field.appendChild(pog)
        } catch {
          case NonFatal(t) ⇒ console.log(s"${t.getClass.getSimpleName}: ${t.toString}")
        }
      }
    }
  }

  val container = document.createElement("div").asInstanceOf[html.Div]

  val field = document.createElement("div").asInstanceOf[html.Div]
  field.styles(_.backgroundColor = "#342", _.position = "relative")
  field.style.width = "640px"
  field.style.height = "480px"

  field.onclick = onClick _

  val buttons = document.createElement("div").asInstanceOf[html.Div]
  val clearButton  = document.createButton("Clear") { _ ⇒
    SessionService.clearSession(sessionId).map(refresh)
  }
  buttons.appendChild(clearButton)
  container.appendChildren(field, buttons)


  def run(): html.Div = {
    def reload(): Unit = {
      SessionService.getSession(sessionId).map(refresh)
    }
    window.setInterval(reload _, 1000)
    container
  }
}
