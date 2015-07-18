package me.enkode.tt.models

import upickle._

case class AssetInstanceState(initial: Js.Obj, changes: Seq[(EpochMs, Js.Obj)] = Nil) {
  def rollUp: AssetInstanceState = {
    AssetInstanceState((initial /: changes.map(_._2))(_ merge _), Nil)
  }
}

case class AssetInstance(assetInstanceId: Id, assetId: Id, assetInstanceState: AssetInstanceState)

case class Session(assetInstances: Set[AssetInstance])