package me.enkode.tt.models

import upickle._

case class AssetInstanceState(initial: Js.Obj, changes: Seq[(EpochMs, Js.Obj)] = Nil) {
  lazy val current: Js.Obj = (initial /: changes.map(_._2))(_ merge _)

  def position: Position = {
    def findInt(key: String, default: Int = 0) = {
      current.value find {
        case (`key`, Js.Num(_)) ⇒ true
        case _ ⇒ false
      } map {
        case (_, Js.Num(x)) ⇒ x.toInt
      } getOrElse default
    }

    Position(findInt("x"), findInt("y"))
  }
}

case class AssetInstance(assetInstanceId: Id, assetId: Id, assetInstanceState: AssetInstanceState) {
  def updateState(change: Js.Obj, when: EpochMs = System.currentTimeMillis()) = {
    val changes = Seq(when → change)
    copy(assetInstanceState = assetInstanceState.copy(changes = assetInstanceState.changes ++ changes))
  }
}

case class Session(assetInstances: Set[AssetInstance])