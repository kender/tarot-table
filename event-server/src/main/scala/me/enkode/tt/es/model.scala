package me.enkode.tt.es

import java.time.Instant

import me.enkode.tt.http.SerializationSupport._
import spray.json.DefaultJsonProtocol._
import spray.json._


trait Named {
  lazy val name = getClass.getSimpleName.replaceAllLiterally("$", "").toLowerCase
}

trait Lookup[T <: Named] {
  def all: Set[T]
  def findByName(name: String): Option[T] = all.find(_.name == name.toLowerCase)
}

sealed trait SessionActorType extends Named
object SessionActorType extends Lookup[SessionActorType] {
  case object Human extends SessionActorType
  case object System extends  SessionActorType
  case object Clock extends SessionActorType

  override val all: Set[SessionActorType] = Set(Human, System, Clock)

  implicit object JsonFormat extends JsonFormat[SessionActorType] {
    override def read(json: JsValue) = SessionActorType
      .findByName(json.convertTo[String])
      .getOrElse(sys.error(s"invalid SessionActorType: $json"))
    override def write(obj: SessionActorType) = JsString(obj.name)
  }
}

case class SessionActor(sessionActorType: SessionActorType, id: SessionActorId, displayName: String)
object SessionActor {
  implicit val jsonFormat = jsonFormat3(SessionActor.apply)
  def human(id: SessionActorId, displayName: String) = SessionActor(SessionActorType.Human, id, displayName)
}

case class Geometry(x: Double, y: Double, z: Double, θ: Double)
object Geometry {
  implicit val jsonFormat = jsonFormat4(Geometry.apply)
}

sealed trait SessionObject extends Named {
  def id: SessionObjectId
  def createdAt: Instant
  def createdBy: SessionActor
  def modifiedAt: Instant
  def modifiedBy: SessionActor
  def meta: Meta
}

object SessionObject {
  case class Interactable(id: SessionObjectId, assetId: AssetId, geometry: Geometry, createdAt: Instant, createdBy: SessionActor, modifiedAt: Instant, modifiedBy: SessionActor, meta: Meta) extends SessionObject
  object Interactable {
    implicit val jsonFormatter = jsonFormat(Interactable.apply, "id", "assetId", "geometry", "createdAt", "createdBy", "modifiedAt", "modifiedBy", "meta")
  }
  case class Say(id: SessionObjectId, text: RichText, createdAt: Instant, createdBy: SessionActor, modifiedAt: Instant, modifiedBy: SessionActor, meta: Meta) extends SessionObject
  object Say {
    implicit val jsonFormatter = jsonFormat(Say.apply, "id", "text", "createdAt", "createdBy", "modifiedAt", "modifiedBy", "meta")
  }
  case class Emote(id: SessionObjectId, assetId: AssetId, createdAt: Instant, createdBy: SessionActor, modifiedAt: Instant, modifiedBy: SessionActor, meta: Meta) extends SessionObject
  object Emote {
    implicit val jsonFormatter = jsonFormat(Emote.apply, "id", "assetId", "createdAt", "createdBy", "modifiedAt", "modifiedBy", "meta")
  }

  implicit object JsonFormat extends JsonFormat[SessionObject] {
    override def read(json: JsValue) = {
      val jsObject = json.asJsObject
      val JsString(oType) :: Nil = jsObject.getFields("type")
      oType match {
        case "Interactable" ⇒ json.convertTo[Interactable]
        case "Say" ⇒ json.convertTo[Say]
        case "Emote" ⇒ json.convertTo[Emote]
      }
    }

    override def write(obj: SessionObject) = obj match {
      case interactable: Interactable ⇒
        JsObject(interactable.toJson.asJsObject.fields + ("type" → JsString("Interactable")))
      case say: Say ⇒
        JsObject(say.toJson.asJsObject.fields + ("type" → JsString("Say")))
      case emote: Emote ⇒
        JsObject(emote.toJson.asJsObject.fields + ("type" → JsString("Emote")))
    }
  }
}


case class SessionState(objects: Set[SessionObject]) {
  def Δ(since: Instant): SessionState = {
    copy(objects.filter(_.modifiedAt.isAfter(since)))
  }
}
object SessionState {
  implicit val jsonFormat = jsonFormat1(SessionState.apply)
}

sealed trait Participation extends Named
object Participation extends Lookup[Participation] {
  case object Creator extends Participation
  case object Full extends Participation
  case object Chat extends Participation
  case object View extends Participation
  override val all: Set[Participation] = Set(Creator, Full, Chat, View)

  implicit object JsonFormat extends JsonFormat[Participation] {
    override def read(json: JsValue) = findByName(json.convertTo[String]).getOrElse(sys.error(s"invalid participation: $json"))
    override def write(obj: Participation) = JsString(obj.name)
  }
}

case class SessionParticipant(actor: SessionActor, participation: Participation, joined: Instant)
object SessionParticipant {
  implicit val jsonFormat = jsonFormat3(SessionParticipant.apply)
}

case class Session(id: SessionId, creator: SessionActor, participants: Set[SessionParticipant], state: SessionState) {
  def Δ(since: Instant): Session = {
    copy(participants = participants.filter(_.joined isAfter since), state = (state Δ since))
  }
}
object Session {
  implicit val jsonFormat = jsonFormat4(Session.apply)
}
