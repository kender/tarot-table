package me.enkode.tt.uic.assets

import java.util.UUID
import upickle._
import me.enkode.tt.models._

class Pog extends Asset with Stateful with Clickable with Moveable {
  override val id = UUID.fromString("71F0B714-AADF-4C76-8688-103E20619DF3")

  override def onClick(currentState: Js.Obj) = {
    if (currentState.value.contains("color" → Js.Str("blue"))) {
      Js.Obj("color" → Js.Str("green"))
    } else {
      Js.Obj("color" → Js.Str("blue"))
    }
  }

  override def initialState = Js.Obj(
    "visible" → Js.True,
    "color" → Js.Str("blue")
  )
}

object Pog extends Pog

object PogRenderer extends Renderer[Pog] {
  import org.scalajs.dom._

  override def render(asset: Pog, assetInstance: AssetInstance) = {
    val canvas = document.createElement("canvas").asInstanceOf[html.Canvas]
    val size: Double = 50
    canvas.width = size.toInt + 4
    canvas.height = size.toInt + 4
    type Ctx2D = CanvasRenderingContext2D
    val ctx = canvas.getContext("2d").asInstanceOf[Ctx2D]
    val color: String = assetInstance.assetInstanceState.current.value.collect { case ("color", Js.Str(c)) ⇒ c }.head

    ctx.strokeStyle = color
    ctx.lineWidth = 4
    ctx.beginPath()
    ctx.arc(size/2, size/2, size/2, 0, math.Pi*2)
    ctx.stroke()

    canvas.onclick = { e: MouseEvent ⇒
      asset.onClick(assetInstance.assetInstanceState.current)
    }

    canvas
  }
}
