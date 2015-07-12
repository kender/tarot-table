package me.enkode.tt

import java.time.Instant

import scala.xml.NodeSeq

package object es {
  type SessionId = String
  type SessionActorId = String
  type AssetId = String
  type RichText = NodeSeq

  trait Named {
    def name = getClass.getSimpleName
  }

  sealed trait SessionActorType extends Named
  object SessionActorType {
    case object Human extends SessionActorType
    case object System extends  SessionActorType
    case object Clock extends SessionActorType

    val all = Set(Human, System, Clock)
    def findByName(name: String): Option[SessionActorType] = {
      all.find(_.name.toLowerCase == name.toLowerCase)
    }
  }

  case class SessionActor(sessionActorType: SessionActorType, id: SessionActorId)

  case class Geometry(x: Double, y: Double, z: Double, θ: Double)

  sealed trait SessionObject extends Named {
    def createdAt: Instant
    def createdBy: SessionActorType
    def modifiedAt: Instant
    def modifiedBy: SessionActorType
  }

  object SessionObject {
    case class Interactable(assetId: AssetId, geometry: Geometry, createdAt: Instant, createdBy: SessionActorType, modifiedAt: Instant, modifiedBy: SessionActorType) extends SessionObject
    case class Say(sessionActor: SessionActor, text: RichText, createdAt: Instant, createdBy: SessionActorType, modifiedAt: Instant, modifiedBy: SessionActorType) extends SessionObject
    case class Emote(sessionActor: SessionActor, assetId: AssetId, createdAt: Instant, createdBy: SessionActorType, modifiedAt: Instant, modifiedBy: SessionActorType) extends SessionObject
  }

  case class SessionState(objects: Set[SessionObject]) {
    def Δ(since: Instant): SessionState = {
      copy(this.objects.filter(_.modifiedAt.isAfter(since)))
    }
  }

  case class Session(id: SessionId, creator: SessionActor, participants: Set[SessionActor], state: SessionState)
}
