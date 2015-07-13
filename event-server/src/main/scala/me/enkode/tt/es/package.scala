package me.enkode.tt
import java.util.UUID

import scala.xml.NodeSeq


package object es {
  type SessionId = UUID
  type SessionActorId = UUID
  type AssetId = UUID
  type SessionObjectId = UUID
  type RichText = NodeSeq
  type Meta = Map[String, String]
}
