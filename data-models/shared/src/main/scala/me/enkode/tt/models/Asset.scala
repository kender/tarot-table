package me.enkode.tt.models

trait Asset {
  def id: Id
}

trait Interactable { self: Asset ⇒  }

trait Card extends Asset with Interactable

trait Deck extends Asset with Interactable

trait Discard extends Asset with Interactable

trait Emote extends Asset
