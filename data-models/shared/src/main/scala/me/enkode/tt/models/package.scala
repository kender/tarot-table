package me.enkode.tt

import java.util.UUID

package object models {
  type Id = UUID
  type EpochMs = Long

  import upickle._
  import upickle.default._

  case class Position(x: Int, y: Int)
  object Position {
    def apply(x: Double, y: Double): Position = Position(x.toInt, y.toInt)
  }

  implicit val jsValueReaderThunk = Reader[Js.Obj] {
    case jsObj: Js.Obj ⇒ jsObj
  }

  implicit val jsValueWriterThunk = Writer[Js.Obj] { jsObj: Js.Obj ⇒
    jsObj
  }

  implicit class JsObjHelper(jo: Js.Obj) {
    def merge(other: Js.Obj): Js.Obj = {
      // very naive…
      Js.Obj(jo.value.toSeq ++ other.value.toSeq: _*)
    }

    def findInt(key: String): Option[Int] = {
      jo.value.collect {
        case (`key`, Js.Num(n)) ⇒ n.toInt
      }.headOption
    }
  }
}
