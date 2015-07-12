package me.enkode.tt

import java.time.Instant
import java.util.UUID

import scala.xml.NodeSeq

package object es {
  type SessionId = UUID
  type SessionActorId = UUID
  type AssetId = UUID
  type SessionObjectId = UUID
  type RichText = NodeSeq

  trait Named {
    def name = getClass.getSimpleName
  }

  trait Lookup[T <: Named] {
    def all: Set[T]
    def findByName(name: String): Option[T] = all.find(_.name.toLowerCase == name.toLowerCase)
  }

  sealed trait SessionActorType extends Named
  object SessionActorType extends Lookup[SessionActorType] {
    case object Human extends SessionActorType
    case object System extends  SessionActorType
    case object Clock extends SessionActorType

    override val all = Set(Human, System, Clock)
  }

  case class SessionActor(sessionActorType: SessionActorType, id: SessionActorId)

  case class Geometry(x: Double, y: Double, z: Double, θ: Double)

  sealed trait SessionObject extends Named {
    def id: SessionObjectId
    def createdAt: Instant
    def createdBy: SessionActorType
    def modifiedAt: Instant
    def modifiedBy: SessionActorType
  }

  object SessionObject {
    case class Interactable(id: SessionObjectId, assetId: AssetId, geometry: Geometry, createdAt: Instant, createdBy: SessionActorType, modifiedAt: Instant, modifiedBy: SessionActorType) extends SessionObject
    case class Say(id: SessionObjectId, sessionActor: SessionActor, text: RichText, createdAt: Instant, createdBy: SessionActorType, modifiedAt: Instant, modifiedBy: SessionActorType) extends SessionObject
    case class Emote(id: SessionObjectId, sessionActor: SessionActor, assetId: AssetId, createdAt: Instant, createdBy: SessionActorType, modifiedAt: Instant, modifiedBy: SessionActorType) extends SessionObject
  }

  case class SessionState(objects: Set[SessionObject]) {
    def Δ(since: Instant): SessionState = {
      copy(this.objects.filter(_.modifiedAt.isAfter(since)))
    }
  }

  sealed trait Participation
  object Participation extends Lookup[Participation] {
    case object Creator extends Participation
    case object Full extends Participation
    case object Chat extends Participation
    case object View extends Participation
    override val all = Set(Creator, Full, Chat, View)
  }
  case class SessionParticipant(actor: SessionActor, participation: Participation)

  case class Session(id: SessionId, creator: SessionActor, participants: Set[SessionParticipant], state: SessionState)
}
