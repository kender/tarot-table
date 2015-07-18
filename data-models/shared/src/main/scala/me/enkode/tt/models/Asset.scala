package me.enkode.tt.models

import upickle._

trait Asset {
  def id: Id
}

trait Stateful { self: Asset ⇒
  def initialState: Js.Obj
}

trait Interactive { self: Asset ⇒  }

trait Card extends Asset with Interactive

trait Deck extends Asset with Interactive

trait Discard extends Asset with Interactive

trait Emote extends Asset
