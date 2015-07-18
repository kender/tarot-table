package me.enkode.tt.uic.assets

import java.util.UUID
import upickle._
import me.enkode.tt.models.{Interactive, Stateful, Asset}

class PogAsset extends Asset with Stateful with Interactive {
  override val id = UUID.fromString("71F0B714-AADF-4C76-8688-103E20619DF3")

  override def initialState = Js.Obj(
    "visible" → Js.True,
    "color" → Js.Str("blue")
  )
}
