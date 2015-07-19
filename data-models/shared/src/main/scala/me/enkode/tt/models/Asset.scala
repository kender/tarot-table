package me.enkode.tt.models

import java.util.UUID

import upickle._

trait Asset {
  def id: Id
}

trait Stateful { self: Asset ⇒
  def initialState: Js.Obj

  def instantiate(): AssetInstance = AssetInstance(UUID.randomUUID(), self.id, AssetInstanceState(initialState))
}

object Interactive {
  sealed trait Interaction
  case class Move(to: Position) extends Interaction
}

trait Clickable { self: Asset with Stateful ⇒
  def onClick(currentState: Js.Obj): Js.Obj
}

trait Moveable { self: Asset with Stateful ⇒
  def onMove(currentState: Js.Obj, to: Position): Js.Obj = Js.Obj("position_x" → Js.Num(to.x), "position_y" → Js.Num(to.y))
}

trait Card extends Asset with Stateful with Clickable

trait Deck extends Asset with Stateful with Clickable

trait Discard extends Asset with Stateful with Clickable

trait Emote extends Asset
